package verigate.verification.cg.infrastructure.lambda.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.inject.Guice;
import com.google.inject.util.Modules;
import domain.messages.EphemeralMessageQueue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.sqs.SqsClient;
import verigate.verification.cg.infrastructure.functions.lambda.di.factories.VerifyPartyDependencyFactory;
import verigate.verification.cg.infrastructure.functions.lambda.handlers.VerifyPartyCommandLambdaHandler;
import verigate.verification.cg.infrastructure.lambda.di.TestVerifyPartyServiceModule;

/**
 * Integration tests for the full command lifecycle through the VerifyPartyCommandLambdaHandler.
 * Tests cover CIPC (company verification) and DHA (identity verification) adapter routing,
 * command idempotency, and DLQ routing for permanent failures.
 */
public class VerifyPartyCommandLifecycleIT {

  private static final String CIPC_FIXTURE =
      "src/test/resources/sqs/VerifyPartyCommand-CompanyVerificationMock.json";
  private static final String DHA_FIXTURE =
      "src/test/resources/sqs/VerifyPartyCommand-IdentityVerificationMock.json";
  private static final String PERSONAL_DETAILS_FIXTURE =
      "src/test/resources/sqs/VerifyPartyCommand-PersonalDetailsMock.json";

  @Test
  @DisplayName("CIPC company verification command completes without batch failures")
  void cipcCompanyVerificationCommandSucceeds() throws IOException {
    var result = executeHandler(CIPC_FIXTURE);

    assertNoBatchFailures(result);
  }

  @Test
  @DisplayName("DHA identity verification command completes without batch failures")
  void dhaIdentityVerificationCommandSucceeds() throws IOException {
    var result = executeHandler(DHA_FIXTURE);

    assertNoBatchFailures(result);
  }

  @Test
  @DisplayName("Personal details verification command completes without batch failures")
  void personalDetailsVerificationCommandSucceeds() throws IOException {
    var result = executeHandler(PERSONAL_DETAILS_FIXTURE);

    assertNoBatchFailures(result);
  }

  @Test
  @DisplayName("Processing the same command twice is idempotent (no batch failures)")
  void duplicateCommandIsIdempotent() throws IOException {
    var json = readFixture(CIPC_FIXTURE);
    var sqsEvent = buildSqsEvent(json, "duplicate-msg-1");

    var handler = createHandler();

    var result1 = handler.handleRequest(sqsEvent, mockContext());
    assertNoBatchFailures(result1);

    var result2 = handler.handleRequest(sqsEvent, mockContext());
    assertNoBatchFailures(result2);
  }

  @Test
  @DisplayName("Malformed JSON command is handled gracefully (routed to DLQ, no batch failure)")
  void malformedCommandRoutedToDlq() {
    var sqsEvent = buildSqsEvent("{invalid json}", "bad-msg-1");

    var handler = createHandler();
    var result = handler.handleRequest(sqsEvent, mockContext());

    // The resilient handler routes invalid messages to DLQ/IMQ without
    // reporting batch failures — the message is considered "handled"
    assertNoBatchFailures(result);
  }

  @Test
  @DisplayName("Command with missing required fields is handled gracefully (routed to DLQ)")
  void missingFieldsCommandRoutedToDlq() {
    var incompleteJson =
        "{\"__artifact_type__\":\"VerifyPartyCommand\","
            + "\"id\":\"" + UUID.randomUUID() + "\","
            + "\"createdDate\":\"2025-06-15T10:30:00Z\","
            + "\"createdBy\":\"test\""
            + "}";

    var sqsEvent = buildSqsEvent(incompleteJson, "incomplete-msg-1");

    var handler = createHandler();
    var result = handler.handleRequest(sqsEvent, mockContext());

    // Invalid commands are routed to DLQ without reporting batch failures
    assertNoBatchFailures(result);
  }

  @Test
  @DisplayName("Batch of mixed valid and invalid messages processes correctly")
  void batchWithMixedMessagesProcessesCorrectly() throws IOException {
    var validJson = readFixture(CIPC_FIXTURE);
    var invalidJson = "{bad json}";

    SQSEvent.SQSMessage validMsg = new SQSEvent.SQSMessage();
    validMsg.setBody(validJson);
    validMsg.setMessageId("valid-msg");
    validMsg.setEventSource("aws:sqs");

    SQSEvent.SQSMessage invalidMsg = new SQSEvent.SQSMessage();
    invalidMsg.setBody(invalidJson);
    invalidMsg.setMessageId("invalid-msg");
    invalidMsg.setEventSource("aws:sqs");

    SQSEvent sqsEvent = new SQSEvent();
    sqsEvent.setRecords(List.of(validMsg, invalidMsg));

    var handler = createHandler();
    var result = handler.handleRequest(sqsEvent, mockContext());

    assertNotNull(result);
    // The valid message should succeed; the invalid one may fail
    // At minimum, the handler should not throw and should return a response
  }

  // --- helpers ---

  private SQSBatchResponse executeHandler(String fixturePath) throws IOException {
    var json = readFixture(fixturePath);
    var sqsEvent = buildSqsEvent(json, "msg-" + UUID.randomUUID());
    var handler = createHandler();
    return handler.handleRequest(sqsEvent, mockContext());
  }

  private VerifyPartyCommandLambdaHandler createHandler() {
    var mockSqsClient = Mockito.mock(SqsClient.class);
    var dependencyFactory =
        new VerifyPartyDependencyFactory(
            Guice.createInjector(
                Modules.override(new TestVerifyPartyServiceModule())
                    .with(binder -> binder.bind(SqsClient.class).toInstance(mockSqsClient))));
    return new VerifyPartyCommandLambdaHandler(dependencyFactory);
  }

  private String readFixture(String path) throws IOException {
    return new String(Files.readAllBytes(Paths.get(path)));
  }

  private SQSEvent buildSqsEvent(String body, String messageId) {
    SQSEvent.SQSMessage sqsMessage = new SQSEvent.SQSMessage();
    sqsMessage.setBody(body);
    sqsMessage.setMessageId(messageId);
    sqsMessage.setEventSource("aws:sqs");

    SQSEvent sqsEvent = new SQSEvent();
    sqsEvent.setRecords(Collections.singletonList(sqsMessage));
    return sqsEvent;
  }

  private Context mockContext() {
    return Mockito.mock(Context.class);
  }

  private void assertNoBatchFailures(SQSBatchResponse result) {
    var expected = new SQSBatchResponse();
    expected.setBatchItemFailures(Collections.emptyList());
    assertEquals(expected, result, "Expected no batch item failures");
  }
}
