package verigate.webbff.admin.model;

import jakarta.validation.constraints.NotNull;

public record UpdatePartnerStatusRequest(
    @NotNull PartnerStatus status) {}
