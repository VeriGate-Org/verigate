/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.webbff.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import verigate.webbff.auth.ApiKeyAuthenticationFilter;
import verigate.webbff.auth.ApiKeyResolver;
import verigate.webbff.auth.CognitoJwtAuthenticationFilter;
import verigate.webbff.auth.CognitoJwtConfig;
import verigate.webbff.auth.RateLimitConfig;
import verigate.webbff.auth.RateLimitingFilter;

/**
 * Spring Security configuration for the VeriGate Web BFF.
 * <p>
 * Filter chain order (earliest to latest):
 * <ol>
 *   <li>{@link CognitoJwtAuthenticationFilter} — validates Cognito JWT
 *       Bearer tokens (optional, disabled by default)</li>
 *   <li>{@link ApiKeyAuthenticationFilter} — validates X-API-Key header
 *       against DynamoDB (primary authentication method)</li>
 *   <li>{@link RateLimitingFilter} — enforces per-partner request rate
 *       limits after authentication has established the partner context</li>
 * </ol>
 * <p>
 * The Cognito JWT filter is a no-op when
 * {@code verigate.auth.cognito.enabled=false}. If a Bearer token is
 * present and valid, the request is authenticated immediately and the
 * API key filter is skipped (the security context is already populated).
 * If no Bearer token is present, the Cognito filter passes through and
 * the API key filter handles authentication.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private final ApiKeyResolver apiKeyResolver;
  private final CognitoJwtConfig cognitoJwtConfig;
  private final RateLimitConfig rateLimitConfig;

  @Value("${verigate.cors.allowed-origins:http://localhost:3000}")
  private String allowedOrigins;

  public SecurityConfig(
      ApiKeyResolver apiKeyResolver,
      CognitoJwtConfig cognitoJwtConfig,
      RateLimitConfig rateLimitConfig) {
    this.apiKeyResolver = apiKeyResolver;
    this.cognitoJwtConfig = cognitoJwtConfig;
    this.rateLimitConfig = rateLimitConfig;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/health", "/actuator/info").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/**").authenticated()
            .anyRequest().denyAll())
        // 1. Cognito JWT filter — runs first, falls through if no Bearer token
        .addFilterBefore(
            new CognitoJwtAuthenticationFilter(cognitoJwtConfig),
            UsernamePasswordAuthenticationFilter.class)
        // 2. API key filter — runs second, authenticates via X-API-Key header
        .addFilterAfter(
            new ApiKeyAuthenticationFilter(apiKeyResolver),
            CognitoJwtAuthenticationFilter.class)
        // 3. Rate limiting filter — runs after authentication, needs partnerId
        .addFilterAfter(
            new RateLimitingFilter(rateLimitConfig),
            ApiKeyAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(allowedOrigins.split(",")));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-API-Key", "X-Request-ID", "X-Correlation-ID", "Accept"));
    configuration.setExposedHeaders(List.of("X-API-Key", "X-Correlation-ID"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);
    return source;
  }
}
