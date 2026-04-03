package verigate.webbff.partner.controller;

import java.util.Map;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import verigate.webbff.partner.service.PartnerFeatureService;

/**
 * Public (unauthenticated) endpoint for tenant branding configuration.
 * Used by the partner portal to resolve subdomain-based branding
 * before the user has authenticated.
 */
@RestController
@RequestMapping("/api/public")
public class PublicTenantController {

  private final PartnerFeatureService partnerFeatureService;

  public PublicTenantController(PartnerFeatureService partnerFeatureService) {
    this.partnerFeatureService = partnerFeatureService;
  }

  @GetMapping("/tenant/{slug}")
  public ResponseEntity<Map<String, Object>> getTenantBranding(@PathVariable String slug) {
    var branding = partnerFeatureService.getPublicBranding(slug);
    if (branding == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(java.time.Duration.ofMinutes(5)).cachePublic())
        .body(branding);
  }
}
