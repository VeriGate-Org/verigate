package verigate.webbff.admin.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import verigate.webbff.admin.model.PartnerRegistrationRequest;
import verigate.webbff.admin.model.PartnerRegistrationResponse;
import verigate.webbff.auth.CognitoJwtConfig;
import verigate.webbff.config.properties.PartnerHubProperties;
import verigate.webbff.partner.repository.PartnerDataRepository;

@Service
public class PartnerRegistrationService {

  private static final Logger logger = LoggerFactory.getLogger(PartnerRegistrationService.class);

  private static final String PARTNER_GROUP = "partner";

  private final DynamoDbClient dynamoDbClient;
  private final CognitoIdentityProviderClient cognitoClient;
  private final CognitoJwtConfig cognitoConfig;
  private final PartnerDataRepository partnerDataRepository;
  private final String tableName;

  public PartnerRegistrationService(
      DynamoDbClient dynamoDbClient,
      CognitoIdentityProviderClient cognitoClient,
      CognitoJwtConfig cognitoConfig,
      PartnerDataRepository partnerDataRepository,
      PartnerHubProperties partnerHubProperties) {
    this.dynamoDbClient = dynamoDbClient;
    this.cognitoClient = cognitoClient;
    this.cognitoConfig = cognitoConfig;
    this.partnerDataRepository = partnerDataRepository;
    this.tableName = partnerHubProperties.getTableName();
  }

  public PartnerRegistrationResponse register(PartnerRegistrationRequest request) {
    // Validate consent
    if (!Boolean.TRUE.equals(request.termsAccepted())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Terms of service must be accepted");
    }
    if (!Boolean.TRUE.equals(request.privacyPolicyAccepted())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Privacy policy must be accepted");
    }

    String partnerId = "partner-" + UUID.randomUUID();
    String now = Instant.now().toString();
    String fullName = request.firstName() + " " + request.lastName();

    // Step 1: Create partner METADATA record in DynamoDB
    try {
      Map<String, AttributeValue> item = new HashMap<>();
      item.put("partnerId", AttributeValue.builder().s(partnerId).build());
      item.put("entityType", AttributeValue.builder().s("METADATA").build());
      item.put("name", AttributeValue.builder().s(request.companyName()).build());
      item.put("contactEmail", AttributeValue.builder().s(request.email()).build());
      item.put("billingPlan", AttributeValue.builder().s(request.billingPlan()).build());
      item.put("partnerStatus", AttributeValue.builder().s("ACTIVE").build());
      item.put("companyType", AttributeValue.builder().s(request.companyType()).build());
      item.put("createdAt", AttributeValue.builder().s(now).build());

      dynamoDbClient.putItem(PutItemRequest.builder()
          .tableName(tableName)
          .item(item)
          .conditionExpression("attribute_not_exists(partnerId)")
          .build());
      logger.info("Created partner METADATA: partnerId={}", partnerId);
    } catch (Exception e) {
      logger.error("Failed to create partner record: {}", e.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create partner record");
    }

    // Step 2: Create Cognito user (sends temp password email)
    String cognitoUsername;
    try {
      AdminCreateUserResponse cognitoResponse = cognitoClient.adminCreateUser(
          AdminCreateUserRequest.builder()
              .userPoolId(cognitoConfig.getUserPoolId())
              .username(request.email())
              .userAttributes(
                  AttributeType.builder().name("email").value(request.email()).build(),
                  AttributeType.builder().name("email_verified").value("true").build(),
                  AttributeType.builder().name("name").value(fullName).build(),
                  AttributeType.builder().name("custom:partnerId").value(partnerId).build(),
                  AttributeType.builder().name("custom:partnerName").value(request.companyName()).build(),
                  AttributeType.builder().name("custom:role").value("admin").build())
              .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
              .build());
      cognitoUsername = cognitoResponse.user().username();
      logger.info("Created Cognito user: {} for partner: {}", cognitoUsername, partnerId);
    } catch (UsernameExistsException e) {
      // Rollback: delete partner METADATA
      rollbackPartnerRecord(partnerId);
      throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with this email already exists");
    } catch (CognitoIdentityProviderException e) {
      logger.error("Cognito user creation failed: {}", e.getMessage());
      rollbackPartnerRecord(partnerId);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user account");
    }

    // Step 3: Add user to "partner" group
    try {
      cognitoClient.adminAddUserToGroup(AdminAddUserToGroupRequest.builder()
          .userPoolId(cognitoConfig.getUserPoolId())
          .username(cognitoUsername)
          .groupName(PARTNER_GROUP)
          .build());
      logger.info("Added user {} to group '{}'", cognitoUsername, PARTNER_GROUP);
    } catch (CognitoIdentityProviderException e) {
      logger.error("Failed to add user to partner group: {}", e.getMessage());
      // Rollback: delete Cognito user and partner record
      rollbackCognitoUser(cognitoUsername);
      rollbackPartnerRecord(partnerId);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to assign partner role");
    }

    // Step 4: Create partner PROFILE record
    try {
      Map<String, Object> profileData = new HashMap<>();
      profileData.put("name", request.companyName());
      profileData.put("contactEmail", request.email());
      profileData.put("billingPlan", request.billingPlan());
      profileData.put("companyType", request.companyType());
      profileData.put("verificationTypes", request.verificationTypes());
      profileData.put("status", "ACTIVE");
      profileData.put("contactName", fullName);
      partnerDataRepository.saveProfile(partnerId, profileData);
      logger.info("Created partner PROFILE: partnerId={}", partnerId);
    } catch (Exception e) {
      logger.warn("Failed to create partner profile (non-fatal): {}", e.getMessage());
      // Profile creation is non-fatal — the partner can set it up later
    }

    return new PartnerRegistrationResponse(
        partnerId,
        request.email(),
        "REGISTERED",
        "Registration successful. Please check your email for a temporary password.");
  }

  private void rollbackPartnerRecord(String partnerId) {
    try {
      dynamoDbClient.deleteItem(software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest.builder()
          .tableName(tableName)
          .key(Map.of(
              "partnerId", AttributeValue.builder().s(partnerId).build(),
              "entityType", AttributeValue.builder().s("METADATA").build()))
          .build());
      logger.info("Rolled back partner METADATA: partnerId={}", partnerId);
    } catch (Exception e) {
      logger.error("Failed to rollback partner record {}: {}", partnerId, e.getMessage());
    }
  }

  private void rollbackCognitoUser(String username) {
    try {
      cognitoClient.adminDeleteUser(AdminDeleteUserRequest.builder()
          .userPoolId(cognitoConfig.getUserPoolId())
          .username(username)
          .build());
      logger.info("Rolled back Cognito user: {}", username);
    } catch (Exception e) {
      logger.error("Failed to rollback Cognito user {}: {}", username, e.getMessage());
    }
  }
}
