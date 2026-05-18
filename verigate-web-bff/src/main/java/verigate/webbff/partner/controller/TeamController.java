package verigate.webbff.partner.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.partner.model.TeamMemberModels.InviteTeamMemberRequest;
import verigate.webbff.partner.model.TeamMemberModels.TeamMemberListResponse;
import verigate.webbff.partner.model.TeamMemberModels.TeamMemberResponse;
import verigate.webbff.partner.model.TeamMemberModels.UpdateRoleRequest;
import verigate.webbff.partner.service.TeamMemberService;

@RestController
@RequestMapping("/api/partner/team")
public class TeamController {

  private static final Logger logger = LoggerFactory.getLogger(TeamController.class);

  private final TeamMemberService teamMemberService;

  public TeamController(TeamMemberService teamMemberService) {
    this.teamMemberService = teamMemberService;
  }

  private String requirePartnerId() {
    return PartnerContextHolder.requirePartnerId();
  }

  @GetMapping
  public TeamMemberListResponse listMembers() {
    return teamMemberService.listMembers(requirePartnerId());
  }

  @PostMapping("/invite")
  public ResponseEntity<TeamMemberResponse> inviteMember(
      @Valid @RequestBody InviteTeamMemberRequest request) {
    String partnerId = requirePartnerId();
    logger.info("Inviting team member: partnerId={}, email={}", partnerId, request.email());
    // Use the partner ID as the inviter for now (could be extracted from JWT claims)
    TeamMemberResponse response = teamMemberService.inviteMember(partnerId, partnerId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{memberId}")
  public TeamMemberResponse getMember(@PathVariable String memberId) {
    return teamMemberService.getMember(requirePartnerId(), memberId);
  }

  @PutMapping("/{memberId}/role")
  public TeamMemberResponse updateRole(
      @PathVariable String memberId,
      @Valid @RequestBody UpdateRoleRequest request) {
    String partnerId = requirePartnerId();
    logger.info("Updating role: partnerId={}, memberId={}, role={}", partnerId, memberId, request.role());
    return teamMemberService.updateRole(partnerId, memberId, request.role());
  }

  @PutMapping("/{memberId}/deactivate")
  public ResponseEntity<Void> deactivateMember(@PathVariable String memberId) {
    String partnerId = requirePartnerId();
    logger.info("Deactivating member: partnerId={}, memberId={}", partnerId, memberId);
    teamMemberService.deactivateMember(partnerId, memberId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{memberId}/reactivate")
  public ResponseEntity<Void> reactivateMember(@PathVariable String memberId) {
    String partnerId = requirePartnerId();
    logger.info("Reactivating member: partnerId={}, memberId={}", partnerId, memberId);
    teamMemberService.reactivateMember(partnerId, memberId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{memberId}")
  public ResponseEntity<Void> removeMember(@PathVariable String memberId) {
    String partnerId = requirePartnerId();
    logger.info("Removing member: partnerId={}, memberId={}", partnerId, memberId);
    teamMemberService.removeMember(partnerId, memberId);
    return ResponseEntity.noContent().build();
  }
}
