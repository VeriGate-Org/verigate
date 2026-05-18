package verigate.webbff.partner.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public final class TeamMemberModels {

  private TeamMemberModels() {}

  public record InviteTeamMemberRequest(
      @NotBlank @Email String email,
      @NotBlank String name,
      @NotBlank String role) {}

  public record UpdateRoleRequest(
      @NotBlank String role) {}

  public record TeamMemberResponse(
      String id,
      String email,
      String name,
      String role,
      String status,
      String invitedBy,
      String createdAt,
      String lastLoginAt) {}

  public record TeamMemberListResponse(
      List<TeamMemberResponse> members,
      int total,
      int maxAllowed) {}
}
