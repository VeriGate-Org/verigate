/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.webbff.auth;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Validates AWS Cognito JWT tokens from the {@code Authorization: Bearer <token>}
 * header.
 * <p>
 * When a valid Bearer token is present the filter:
 * <ol>
 *   <li>Verifies the token signature against the Cognito JWKS endpoint</li>
 *   <li>Validates the {@code iss} (issuer) and {@code aud}/{@code client_id} claims</li>
 *   <li>Extracts the {@code custom:partnerId} claim and sets it on
 *       {@link PartnerContextHolder}</li>
 *   <li>Sets Spring Security authentication with {@code ROLE_PARTNER} authority</li>
 * </ol>
 * <p>
 * If no Bearer token is present, the filter passes the request through to the
 * next filter in the chain (typically the {@link ApiKeyAuthenticationFilter}) so
 * that API key authentication can be attempted instead.
 * <p>
 * When the Cognito integration is disabled ({@code verigate.auth.cognito.enabled=false}),
 * this filter is a complete no-op.
 */
public class CognitoJwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(CognitoJwtAuthenticationFilter.class);

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  private static final String PARTNER_ID_CLAIM = "custom:partnerId";

  private final CognitoJwtConfig cognitoConfig;
  private volatile ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

  public CognitoJwtAuthenticationFilter(CognitoJwtConfig cognitoConfig) {
    this.cognitoConfig = cognitoConfig;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // If Cognito is not enabled, pass through immediately
    if (!cognitoConfig.isEnabled()) {
      filterChain.doFilter(request, response);
      return;
    }

    String authHeader = request.getHeader(AUTHORIZATION_HEADER);

    // No Bearer token — let the next filter (API key) handle authentication
    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(BEARER_PREFIX.length()).trim();
    if (token.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      ConfigurableJWTProcessor<SecurityContext> processor = getOrCreateJwtProcessor();
      JWTClaimsSet claims = processor.process(token, null);

      // Validate issuer
      String issuer = claims.getIssuer();
      if (!cognitoConfig.issuerUrl().equals(issuer)) {
        logger.warn("JWT issuer mismatch: expected={}, actual={}",
            cognitoConfig.issuerUrl(), issuer);
        sendUnauthorized(response, "Invalid token issuer");
        return;
      }

      // Validate audience / client_id
      // Cognito access tokens use "client_id" claim, ID tokens use "aud"
      String clientId = cognitoConfig.getAppClientId();
      if (clientId != null && !clientId.isBlank()) {
        boolean audienceMatch = false;

        // Check "aud" claim (ID tokens)
        List<String> audience = claims.getAudience();
        if (audience != null && audience.contains(clientId)) {
          audienceMatch = true;
        }

        // Check "client_id" claim (access tokens)
        Object clientIdClaim = claims.getClaim("client_id");
        if (clientIdClaim != null && clientId.equals(clientIdClaim.toString())) {
          audienceMatch = true;
        }

        if (!audienceMatch) {
          logger.warn("JWT audience/client_id mismatch: expected={}", clientId);
          sendUnauthorized(response, "Invalid token audience");
          return;
        }
      }

      // Validate token_use (must be "access" or "id")
      Object tokenUse = claims.getClaim("token_use");
      if (tokenUse != null) {
        String tokenUseStr = tokenUse.toString();
        if (!"access".equals(tokenUseStr) && !"id".equals(tokenUseStr)) {
          logger.warn("Unexpected token_use claim: {}", tokenUseStr);
          sendUnauthorized(response, "Invalid token type");
          return;
        }
      }

      // Extract partner ID
      String partnerId = extractPartnerId(claims);
      if (partnerId == null || partnerId.isBlank()) {
        logger.warn("JWT does not contain a valid {} claim", PARTNER_ID_CLAIM);
        sendUnauthorized(response, "Token missing partner identity");
        return;
      }

      // Set partner context
      PartnerContextHolder.setPartnerId(partnerId);

      // Build authorities — admin partners get ROLE_ADMIN in addition
      List<SimpleGrantedAuthority> authorities = buildAuthorities(claims);

      // Set Spring Security authentication
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(partnerId, null, authorities);
      SecurityContextHolder.getContext().setAuthentication(authentication);

      logger.debug("Cognito JWT authenticated partner {} for request to {}",
          partnerId, request.getRequestURI());

      try {
        filterChain.doFilter(request, response);
      } finally {
        PartnerContextHolder.clear();
      }

    } catch (Exception e) {
      logger.error("Cognito JWT validation failed for request to {}: {}",
          request.getRequestURI(), e.getMessage());
      PartnerContextHolder.clear();
      sendUnauthorized(response, "Invalid or expired token");
    }
  }

  /**
   * Extracts the partner ID from JWT claims. Checks the custom claim first,
   * then falls back to the {@code sub} claim.
   */
  private String extractPartnerId(JWTClaimsSet claims) {
    // Prefer custom:partnerId claim
    Object partnerIdClaim = claims.getClaim(PARTNER_ID_CLAIM);
    if (partnerIdClaim != null && !partnerIdClaim.toString().isBlank()) {
      return partnerIdClaim.toString();
    }

    // Fall back to sub (subject) claim
    String subject = claims.getSubject();
    if (subject != null && !subject.isBlank()) {
      logger.debug("Using 'sub' claim as partnerId: {}", subject);
      return subject;
    }

    return null;
  }

  /**
   * Builds the granted authority list. All authenticated users get ROLE_PARTNER.
   * Users in Cognito groups containing "admin" also receive ROLE_ADMIN.
   */
  private List<SimpleGrantedAuthority> buildAuthorities(JWTClaimsSet claims) {
    SimpleGrantedAuthority partnerAuthority = new SimpleGrantedAuthority("ROLE_PARTNER");

    // Check cognito:groups for admin membership
    Object groupsClaim = claims.getClaim("cognito:groups");
    if (groupsClaim instanceof List<?> groups) {
      boolean isAdmin = groups.stream()
          .anyMatch(g -> g != null && g.toString().toLowerCase().contains("admin"));
      if (isAdmin) {
        return List.of(partnerAuthority, new SimpleGrantedAuthority("ROLE_ADMIN"));
      }
    }

    return List.of(partnerAuthority);
  }

  /**
   * Lazily initialises the Nimbus JWT processor with the Cognito JWKS endpoint.
   * Uses double-checked locking to ensure thread-safe singleton creation.
   */
  private ConfigurableJWTProcessor<SecurityContext> getOrCreateJwtProcessor() throws Exception {
    if (jwtProcessor == null) {
      synchronized (this) {
        if (jwtProcessor == null) {
          ConfigurableJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();

          JWKSource<SecurityContext> keySource = JWKSourceBuilder
              .create(new URL(cognitoConfig.jwksUrl()))
              .retrying(true)
              .build();

          JWSKeySelector<SecurityContext> keySelector =
              new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);
          processor.setJWSKeySelector(keySelector);

          jwtProcessor = processor;
          logger.info("Cognito JWT processor initialised with JWKS URL: {}",
              cognitoConfig.jwksUrl());
        }
      }
    }
    return jwtProcessor;
  }

  /**
   * Sends a 401 Unauthorized JSON response.
   */
  private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.getWriter().write(
        String.format("{\"error\":\"Unauthorized\",\"message\":\"%s\"}", message));
  }
}
