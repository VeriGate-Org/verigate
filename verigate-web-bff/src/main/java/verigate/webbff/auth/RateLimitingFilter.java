/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.webbff.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Per-partner rate limiting filter using an in-memory token bucket
 * (counter-per-window) approach.
 * <p>
 * Each partner is tracked independently using a
 * {@link ConcurrentHashMap} of {@link AtomicInteger} counters that
 * are reset to zero every minute by a background scheduler.
 * <p>
 * This filter must run <strong>after</strong> authentication so that
 * the partner identity is available via {@link PartnerContextHolder}.
 * Unauthenticated requests (no partnerId set) are passed through — the
 * security layer will reject them anyway.
 * <p>
 * When a partner exceeds their configured rate, the filter returns
 * HTTP 429 (Too Many Requests) with a {@code Retry-After} header.
 */
public class RateLimitingFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

  private final RateLimitConfig rateLimitConfig;
  private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler;

  public RateLimitingFilter(RateLimitConfig rateLimitConfig) {
    this.rateLimitConfig = rateLimitConfig;

    // Create a daemon scheduler to reset counters every minute
    this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
      Thread t = new Thread(r, "rate-limit-reset");
      t.setDaemon(true);
      return t;
    });
    this.scheduler.scheduleAtFixedRate(this::resetCounters, 1, 1, TimeUnit.MINUTES);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // If rate limiting is disabled, pass through
    if (!rateLimitConfig.isEnabled()) {
      filterChain.doFilter(request, response);
      return;
    }

    String partnerId = PartnerContextHolder.getPartnerId();

    // If no partner context is set (unauthenticated), pass through —
    // Spring Security will handle the rejection downstream
    if (partnerId == null || partnerId.isBlank()) {
      filterChain.doFilter(request, response);
      return;
    }

    int limit = rateLimitConfig.getLimitForPartner(partnerId);
    AtomicInteger counter = requestCounts.computeIfAbsent(partnerId, k -> new AtomicInteger(0));
    int currentCount = counter.incrementAndGet();

    // Set rate limit headers on every response
    response.setIntHeader("X-RateLimit-Limit", limit);
    response.setIntHeader("X-RateLimit-Remaining", Math.max(0, limit - currentCount));

    if (currentCount > limit) {
      logger.warn("Rate limit exceeded for partner {}: {}/{} requests/min",
          partnerId, currentCount, limit);

      response.setStatus(429); // Too Many Requests
      response.setContentType("application/json");
      response.setIntHeader("Retry-After", 60);
      response.getWriter().write(String.format(
          "{\"error\":\"Too Many Requests\","
              + "\"message\":\"Rate limit of %d requests per minute exceeded for partner %s\","
              + "\"retryAfterSeconds\":60}",
          limit, partnerId));
      return;
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Resets all partner counters. Called once per minute by the background scheduler.
   */
  private void resetCounters() {
    if (!requestCounts.isEmpty()) {
      logger.debug("Resetting rate limit counters for {} partners", requestCounts.size());
      requestCounts.clear();
    }
  }

  /**
   * Shuts down the background scheduler. Called by Spring when the filter
   * is destroyed (application shutdown).
   */
  @Override
  public void destroy() {
    scheduler.shutdownNow();
    super.destroy();
  }
}
