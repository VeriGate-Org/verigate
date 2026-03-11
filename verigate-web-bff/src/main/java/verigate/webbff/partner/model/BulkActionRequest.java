package verigate.webbff.partner.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BulkActionRequest(
    @NotNull @NotEmpty List<String> commandIds,
    String format) {}
