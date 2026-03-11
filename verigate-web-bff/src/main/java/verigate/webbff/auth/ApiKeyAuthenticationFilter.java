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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that extracts the X-API-Key header and resolves the partner identity.
 * The resolver performs secure verification with salt and constant-time comparison.
 * Sets the partner context for downstream use.
 */
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);
  private static final String API_KEY_HEADER = "X-API-Key";

  private final ApiKeyResolver apiKeyResolver;

  public ApiKeyAuthenticationFilter(ApiKeyResolver apiKeyResolver) {
    this.apiKeyResolver = apiKeyResolver;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // Skip if already authenticated (e.g. by CognitoJwtAuthenticationFilter)
    if (SecurityContextHolder.getContext().getAuthentication() != null
        && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
      logger.debug("Request already authenticated, skipping API key filter for {}",
          request.getRequestURI());
      filterChain.doFilter(request, response);
      return;
    }

    String apiKey = request.getHeader(API_KEY_HEADER);

    if (apiKey == null || apiKey.isBlank()) {
      logger.debug("No API key provided in request to {}", request.getRequestURI());
      filterChain.doFilter(request, response);
      return;
    }

    try {
      // Pass raw API key to resolver for secure verification
      String partnerId = apiKeyResolver.resolvePartnerId(apiKey);

      if (partnerId == null) {
        logger.warn("Invalid API key provided for request to {}", request.getRequestURI());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\":\"Invalid API key\"}");
        return;
      }

      // Set partner context
      PartnerContextHolder.setPartnerId(partnerId);

      // Set Spring Security authentication
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              partnerId, null,
              List.of(new SimpleGrantedAuthority("ROLE_PARTNER")));
      SecurityContextHolder.getContext().setAuthentication(authentication);

      logger.debug("Authenticated partner {} for request to {}", partnerId,
          request.getRequestURI());

      filterChain.doFilter(request, response);

    } catch (Exception e) {
      logger.error("API key authentication failed", e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.getWriter().write("{\"error\":\"Authentication error\"}");
    } finally {
      PartnerContextHolder.clear();
    }
  }
}
