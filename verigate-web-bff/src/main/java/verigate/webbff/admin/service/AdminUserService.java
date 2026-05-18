package verigate.webbff.admin.service;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import verigate.webbff.auth.CognitoJwtConfig;

@Service
public class AdminUserService {

  private static final Logger logger = LoggerFactory.getLogger(AdminUserService.class);
  private static final String ADMIN_GROUP = "admin";

  private final CognitoIdentityProviderClient cognitoClient;
  private final CognitoJwtConfig cognitoConfig;

  public AdminUserService(
      CognitoIdentityProviderClient cognitoClient,
      CognitoJwtConfig cognitoConfig) {
    this.cognitoClient = cognitoClient;
    this.cognitoConfig = cognitoConfig;
  }

  public record AdminUserResponse(
      String id,
      String email,
      String name,
      String status,
      String createdAt) {}

  public List<AdminUserResponse> listAdminUsers() {
    try {
      ListUsersInGroupResponse response = cognitoClient.listUsersInGroup(
          ListUsersInGroupRequest.builder()
              .userPoolId(cognitoConfig.getUserPoolId())
              .groupName(ADMIN_GROUP)
              .build());

      return response.users().stream()
          .map(this::toAdminUserResponse)
          .toList();
    } catch (CognitoIdentityProviderException e) {
      logger.error("Failed to list admin users: {}", e.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to list admin users");
    }
  }

  public AdminUserResponse inviteAdminUser(String email, String name) {
    String username;
    try {
      AdminCreateUserResponse response = cognitoClient.adminCreateUser(
          AdminCreateUserRequest.builder()
              .userPoolId(cognitoConfig.getUserPoolId())
              .username(email)
              .userAttributes(
                  AttributeType.builder().name("email").value(email).build(),
                  AttributeType.builder().name("email_verified").value("true").build(),
                  AttributeType.builder().name("name").value(name).build(),
                  AttributeType.builder().name("custom:role").value("admin").build())
              .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
              .build());
      username = response.user().username();
      logger.info("Created admin Cognito user: {}", username);
    } catch (UsernameExistsException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with this email already exists");
    } catch (CognitoIdentityProviderException e) {
      logger.error("Failed to create admin user: {}", e.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create admin user");
    }

    // Add to admin group
    try {
      cognitoClient.adminAddUserToGroup(AdminAddUserToGroupRequest.builder()
          .userPoolId(cognitoConfig.getUserPoolId())
          .username(username)
          .groupName(ADMIN_GROUP)
          .build());
    } catch (CognitoIdentityProviderException e) {
      logger.error("Failed to add user to admin group: {}", e.getMessage());
      // Clean up: delete the user we just created
      try {
        cognitoClient.adminDeleteUser(AdminDeleteUserRequest.builder()
            .userPoolId(cognitoConfig.getUserPoolId())
            .username(username)
            .build());
      } catch (CognitoIdentityProviderException ignored) {
        // Best effort cleanup
      }
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to assign admin role");
    }

    return new AdminUserResponse(username, email, name, "FORCE_CHANGE_PASSWORD", java.time.Instant.now().toString());
  }

  public void deactivateAdminUser(String userId) {
    try {
      cognitoClient.adminDisableUser(AdminDisableUserRequest.builder()
          .userPoolId(cognitoConfig.getUserPoolId())
          .username(userId)
          .build());
      logger.info("Deactivated admin user: {}", userId);
    } catch (UserNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found");
    } catch (CognitoIdentityProviderException e) {
      logger.error("Failed to deactivate admin user: {}", e.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to deactivate admin user");
    }
  }

  public void reactivateAdminUser(String userId) {
    try {
      cognitoClient.adminEnableUser(AdminEnableUserRequest.builder()
          .userPoolId(cognitoConfig.getUserPoolId())
          .username(userId)
          .build());
      logger.info("Reactivated admin user: {}", userId);
    } catch (UserNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found");
    } catch (CognitoIdentityProviderException e) {
      logger.error("Failed to reactivate admin user: {}", e.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to reactivate admin user");
    }
  }

  public void removeAdminUser(String userId) {
    try {
      // Remove from admin group first
      cognitoClient.adminRemoveUserFromGroup(AdminRemoveUserFromGroupRequest.builder()
          .userPoolId(cognitoConfig.getUserPoolId())
          .username(userId)
          .groupName(ADMIN_GROUP)
          .build());
    } catch (CognitoIdentityProviderException e) {
      logger.warn("Failed to remove user from admin group (may not exist): {}", e.getMessage());
    }

    try {
      cognitoClient.adminDeleteUser(AdminDeleteUserRequest.builder()
          .userPoolId(cognitoConfig.getUserPoolId())
          .username(userId)
          .build());
      logger.info("Removed admin user: {}", userId);
    } catch (UserNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found");
    } catch (CognitoIdentityProviderException e) {
      logger.error("Failed to remove admin user: {}", e.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to remove admin user");
    }
  }

  private AdminUserResponse toAdminUserResponse(UserType user) {
    String email = user.attributes().stream()
        .filter(a -> "email".equals(a.name()))
        .map(AttributeType::value)
        .findFirst()
        .orElse("");
    String name = user.attributes().stream()
        .filter(a -> "name".equals(a.name()))
        .map(AttributeType::value)
        .findFirst()
        .orElse("");
    String status = user.enabled() != null && !user.enabled() ? "DEACTIVATED"
        : user.userStatusAsString() != null ? user.userStatusAsString()
        : "UNKNOWN";
    String createdAt = user.userCreateDate() != null ? user.userCreateDate().toString() : null;

    return new AdminUserResponse(user.username(), email, name, status, createdAt);
  }
}
