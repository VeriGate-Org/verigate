package verigate.webbff.partner.model;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record PolicyRequest(
    @NotBlank String name,
    String description,
    List<PolicyStep> steps) {

  public record PolicyStep(
      @NotBlank String type,
      String name,
      java.util.Map<String, Object> config,
      String next,
      String onSuccess,
      String onFail,
      List<String> parallel) {}
}
