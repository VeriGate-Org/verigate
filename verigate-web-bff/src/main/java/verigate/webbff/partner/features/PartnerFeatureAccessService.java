package verigate.webbff.partner.features;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import verigate.webbff.partner.repository.PartnerDataRepository;

@Service
public class PartnerFeatureAccessService {

  private final PartnerDataRepository repository;

  public PartnerFeatureAccessService(PartnerDataRepository repository) {
    this.repository = repository;
  }

  public PartnerEntitlements getEntitlements(String partnerId) {
    Map<String, Object> profile = repository.getProfile(partnerId).orElse(Map.of());
    String billingPlan = PartnerFeatureCatalog.normalizePlan((String) profile.getOrDefault("billingPlan", "enterprise"));
    List<String> enabledFeatures = PartnerFeatureCatalog.normalizeEnabledFeatures(castStringList(profile.get("enabledFeatures")));
    return new PartnerEntitlements(
        partnerId,
        billingPlan,
        enabledFeatures,
        PartnerFeatureCatalog.getResolvedFeatures(billingPlan, enabledFeatures),
        PartnerFeatureCatalog.getPlanQuotas(billingPlan));
  }

  public boolean hasFeature(String partnerId, String feature) {
    PartnerEntitlements entitlements = getEntitlements(partnerId);
    return PartnerFeatureCatalog.partnerHasFeature(
        entitlements.billingPlan(),
        entitlements.enabledFeatures(),
        feature);
  }

  public void requireFeature(String partnerId, String feature) {
    if (!hasFeature(partnerId, feature)) {
      String requiredPlan = PartnerFeatureCatalog.getPlanLabel(PartnerFeatureCatalog.getRequiredPlan(feature));
      String featureLabel = PartnerFeatureCatalog.getFeatureLabel(feature);
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          featureLabel + " requires the " + requiredPlan + " plan or a tenant feature override.");
    }
  }

  @SuppressWarnings("unchecked")
  private List<String> castStringList(Object raw) {
    if (raw instanceof List<?> list) {
      return list.stream().map(Object::toString).toList();
    }
    return List.of();
  }
}
