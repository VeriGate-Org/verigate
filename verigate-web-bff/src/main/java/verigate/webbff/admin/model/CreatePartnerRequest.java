package verigate.webbff.admin.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreatePartnerRequest(
    @NotBlank String name,
    @NotBlank @Email String contactEmail,
    String billingPlan) {}
