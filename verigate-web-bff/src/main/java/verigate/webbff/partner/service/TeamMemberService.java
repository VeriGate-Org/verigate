package verigate.webbff.partner.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import verigate.webbff.auth.CognitoJwtConfig;
import verigate.webbff.partner.features.PartnerFeatureAccessService;
import verigate.webbff.partner.model.TeamMemberModels.*;
import verigate.webbff.partner.repository.PartnerDataRepository;

@Service
public class TeamMemberService {

  private static final Logger logger = LoggerFactory.getLogger(TeamMemberService.class);

  private static final String ENTITY_PREFIX = "TEAM_MEMBER#";
  private static final Set<String> VALID_ROLES = Set.of("admin", "operator", "viewer", "auditor");

  private final PartnerDataRepository repository;
  private final CognitoIdentityProviderClient cognitoClient;
  private final CognitoJwtConfig cognitoConfig;
  private final PartnerFeatureAccessService featureAccessService;

  public TeamMemberService(
      PartnerDataRepository repository,
      CognitoIdentityProviderClient cognitoClient,
      CognitoJwtConfig cognitoConfig,
      PartnerFeatureAccessService featureAccessService) {
    this.repository = repository;
    this.cognitoClient = cognitoClient;
    this.cognitoConfig = cognitoConfig;
    this.featureAccessService = featureAccessService;
  }

  public TeamMemberListResponse listMembers(String partnerId) {
    List<Map<String, Object>> entities = repository.listCustomEntities(partnerId, ENTITY_PREFIX);
    int maxAllowed = getMaxUsers(partnerId);
    List<TeamMemberResponse> members = entities.stream()
        .map(this::toResponse)
        .toList();
    return new TeamMemberListResponse(members, members.size(), maxAllowed);
  }

  public TeamMemberResponse getMember(String partnerId, String memberId) {
    return repository.getCustomEntity(partnerId, ENTITY_PREFIX, memberId)
        .map(this::toResponse)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team member not found"));
  }

  public TeamMemberResponse inviteMember(String partnerId, String invitedBy, InviteTeamMemberRequest request) {
    validateRole(request.role());

    // Enforce quota
    List<Map<String, Object>> existing = repository.listCustomEntities(partnerId, ENTITY_PREFIX);
    int maxAllowed = getMaxUsers(partnerId);
    if (existing.size() >= maxAllowed) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          String.format("Team member limit reached (%d of %d). Upgrade your plan to add more members.",
              existing.size(), maxAllowed));
    }

    // Create Cognito user with temporary password (triggers invite email)
    String cognitoUsername;
    try {
      AdminCreateUserResponse cognitoResponse = cognitoClient.adminCreateUser(AdminCreateUserRequest.builder()
          .userPoolId(cognitoConfig.getUserPoolId())
          .username(request.email())
          .userAttributes(
              AttributeType.builder().name("email").value(request.email()).build(),
              AttributeType.builder().name("email_verified").value("true").build(),
              AttributeType.builder().name("name").value(request.name()).build(),
              AttributeType.builder().name("custom:partnerId").value(partnerId).build(),
              AttributeType.builder().name("custom:role").value(request.role()).build())
          .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
          .build());
      cognitoUsername = cognitoResponse.user().username();
      logger.info("Created Cognito user: {} for partner: {}", cognitoUsername, partnerId);
    } catch (UsernameExistsException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with this email already exists");
    } catch (CognitoIdentityProviderException e) {
      logger.error("Cognito user creation failed: {}", e.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user account");
    }

    // Add to admin group if role is admin
    if ("admin".equals(request.role())) {
      addToAdminGroup(cognitoUsername);
    }

    // Save DynamoDB record
    Map<String, Object> data = Map.of(
        "email", request.email(),
        "name", request.name(),
        "role", request.role(),
        "status", "INVITED",
        "invitedBy", invitedBy,
        "cognitoUsername", cognitoUsername);

    String memberId = repository.saveCustomEntity(partnerId, ENTITY_PREFIX, "TEAM_MEMBER", null, data);
    logger.info("Invited team member: partnerId={}, memberId={}, email={}", partnerId, memberId, request.email());

    return getMember(partnerId, memberId);
  }

  public TeamMemberResponse updateRole(String partnerId, String memberId, String newRole) {
    validateRole(newRole);

    Map<String, Object> existing = repository.getCustomEntity(partnerId, ENTITY_PREFIX, memberId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team member not found"));

    String oldRole = (String) existing.getOrDefault("role", "viewer");
    String cognitoUsername = (String) existing.get("cognitoUsername");

    // Update Cognito custom:role attribute
    if (cognitoUsername != null) {
      try {
        cognitoClient.adminUpdateUserAttributes(AdminUpdateUserAttributesRequest.builder()
            .userPoolId(cognitoConfig.getUserPoolId())
            .username(cognitoUsername)
            .userAttributes(AttributeType.builder().name("custom:role").value(newRole).build())
            .build());
      } catch (CognitoIdentityProviderException e) {
        logger.error("Failed to update Cognito role: {}", e.getMessage());
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update user role");
      }

      // Manage admin group membership
      if ("admin".equals(newRole) && !"admin".equals(oldRole)) {
        addToAdminGroup(cognitoUsername);
      } else if (!"admin".equals(newRole) && "admin".equals(oldRole)) {
        removeFromAdminGroup(cognitoUsername);
      }
    }

    // Update DynamoDB
    Map<String, Object> updatedData = new java.util.HashMap<>(existing);
    updatedData.put("role", newRole);
    repository.saveCustomEntity(partnerId, ENTITY_PREFIX, "TEAM_MEMBER", memberId, updatedData);

    logger.info("Updated role: partnerId={}, memberId={}, {} -> {}", partnerId, memberId, oldRole, newRole);
    return getMember(partnerId, memberId);
  }

  public void deactivateMember(String partnerId, String memberId) {
    Map<String, Object> existing = repository.getCustomEntity(partnerId, ENTITY_PREFIX, memberId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team member not found"));

    String cognitoUsername = (String) existing.get("cognitoUsername");
    if (cognitoUsername != null) {
      try {
        cognitoClient.adminDisableUser(AdminDisableUserRequest.builder()
            .userPoolId(cognitoConfig.getUserPoolId())
            .username(cognitoUsername)
            .build());
      } catch (CognitoIdentityProviderException e) {
        logger.error("Failed to disable Cognito user: {}", e.getMessage());
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to deactivate user");
      }
    }

    Map<String, Object> updatedData = new java.util.HashMap<>(existing);
    updatedData.put("status", "DEACTIVATED");
    repository.saveCustomEntity(partnerId, ENTITY_PREFIX, "TEAM_MEMBER", memberId, updatedData);
    logger.info("Deactivated member: partnerId={}, memberId={}", partnerId, memberId);
  }

  public void reactivateMember(String partnerId, String memberId) {
    Map<String, Object> existing = repository.getCustomEntity(partnerId, ENTITY_PREFIX, memberId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team member not found"));

    String cognitoUsername = (String) existing.get("cognitoUsername");
    if (cognitoUsername != null) {
      try {
        cognitoClient.adminEnableUser(AdminEnableUserRequest.builder()
            .userPoolId(cognitoConfig.getUserPoolId())
            .username(cognitoUsername)
            .build());
      } catch (CognitoIdentityProviderException e) {
        logger.error("Failed to enable Cognito user: {}", e.getMessage());
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to reactivate user");
      }
    }

    Map<String, Object> updatedData = new java.util.HashMap<>(existing);
    updatedData.put("status", "ACTIVE");
    repository.saveCustomEntity(partnerId, ENTITY_PREFIX, "TEAM_MEMBER", memberId, updatedData);
    logger.info("Reactivated member: partnerId={}, memberId={}", partnerId, memberId);
  }

  public void removeMember(String partnerId, String memberId) {
    Map<String, Object> existing = repository.getCustomEntity(partnerId, ENTITY_PREFIX, memberId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team member not found"));

    String cognitoUsername = (String) existing.get("cognitoUsername");
    if (cognitoUsername != null) {
      try {
        cognitoClient.adminDeleteUser(AdminDeleteUserRequest.builder()
            .userPoolId(cognitoConfig.getUserPoolId())
            .username(cognitoUsername)
            .build());
      } catch (UserNotFoundException e) {
        logger.warn("Cognito user already deleted: {}", cognitoUsername);
      } catch (CognitoIdentityProviderException e) {
        logger.error("Failed to delete Cognito user: {}", e.getMessage());
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to remove user");
      }
    }

    repository.deleteCustomEntity(partnerId, ENTITY_PREFIX, memberId);
    logger.info("Removed member: partnerId={}, memberId={}", partnerId, memberId);
  }

  // ── Helpers ──────────────────────────────────────────────────────────

  private TeamMemberResponse toResponse(Map<String, Object> data) {
    return new TeamMemberResponse(
        (String) data.get("id"),
        (String) data.get("email"),
        (String) data.get("name"),
        (String) data.getOrDefault("role", "viewer"),
        (String) data.getOrDefault("status", "INVITED"),
        (String) data.get("invitedBy"),
        (String) data.get("createdAt"),
        (String) data.get("lastLoginAt"));
  }

  private void validateRole(String role) {
    if (!VALID_ROLES.contains(role)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid role: " + role + ". Valid roles are: " + VALID_ROLES);
    }
  }

  private int getMaxUsers(String partnerId) {
    var entitlements = featureAccessService.getEntitlements(partnerId);
    return entitlements.quotas().getOrDefault("maxUsers", 25);
  }

  private void addToAdminGroup(String username) {
    try {
      cognitoClient.adminAddUserToGroup(AdminAddUserToGroupRequest.builder()
          .userPoolId(cognitoConfig.getUserPoolId())
          .username(username)
          .groupName("admin")
          .build());
    } catch (CognitoIdentityProviderException e) {
      logger.warn("Failed to add user to admin group: {}", e.getMessage());
    }
  }

  private void removeFromAdminGroup(String username) {
    try {
      cognitoClient.adminRemoveUserFromGroup(AdminRemoveUserFromGroupRequest.builder()
          .userPoolId(cognitoConfig.getUserPoolId())
          .username(username)
          .groupName("admin")
          .build());
    } catch (CognitoIdentityProviderException e) {
      logger.warn("Failed to remove user from admin group: {}", e.getMessage());
    }
  }
}
