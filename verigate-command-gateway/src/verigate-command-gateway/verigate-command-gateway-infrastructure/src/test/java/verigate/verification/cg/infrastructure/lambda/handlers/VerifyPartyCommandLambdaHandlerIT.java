package verigate.verification.cg.infrastructure.lambda.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.inject.Guice;
import com.google.inject.util.Modules;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import verigate.verification.cg.infrastructure.functions.lambda.di.factories.VerifyPartyDependencyFactory;
import verigate.verification.cg.infrastructure.functions.lambda.handlers.VerifyPartyCommandLambdaHandler;
import verigate.verification.cg.infrastructure.lambda.di.TestVerifyPartyServiceModule;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * This class contains integration tests for the {@link VerifyPartyCommandLambdaHandler} class.
 */
public class VerifyPartyCommandLambdaHandlerIT {
  /**
   * Tests the handleRequest method of the CalculateClaimPayoutCommandLambdaHandler class.
   *
   * @throws IOException if an I/O error occurs.
   */
  @Test
  void verifyPartyCommandLambdaIT() throws IOException {
    var context = Mockito.mock(Context.class);
    var mockSqsClient = Mockito.mock(SqsClient.class);

    var json =
        new String(
            Files.readAllBytes(
                Paths.get("src/test/resources/sqs/VerifyPartyCommand-PersonalDetailsMock.json")));

    SQSEvent.SQSMessage sqsMessage = new SQSEvent.SQSMessage();
    sqsMessage.setBody(json);
    sqsMessage.setMessageId("mockId");
    sqsMessage.setEventSource("aws:sqs");

    SQSEvent sqsEvent = new SQSEvent();
    sqsEvent.setRecords(Collections.singletonList(sqsMessage));

    var dependencyFactory =
        new VerifyPartyDependencyFactory(
            Guice.createInjector(
                Modules.override(new TestVerifyPartyServiceModule())
                    .with(
                        binder -> {
                          binder.bind(SqsClient.class).toInstance(mockSqsClient);
                        })));

    try (var mockedGuice = Mockito.mockStatic(Guice.class)) {
      var handler = new VerifyPartyCommandLambdaHandler(dependencyFactory);

      var result = handler.handleRequest(sqsEvent, context);

      var failures = new SQSBatchResponse();
      failures.setBatchItemFailures(Collections.emptyList());
      assertEquals(result, failures);
    }
  }
}
