package verigate.webbff.admin.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PartnerRegistrationRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank @Email String email,
    @NotBlank String companyName,
    @NotBlank String companyType,
    @NotBlank String billingPlan,
    @NotEmpty List<String> verificationTypes,
    @NotNull Boolean termsAccepted,
    @NotNull Boolean privacyPolicyAccepted) {}
