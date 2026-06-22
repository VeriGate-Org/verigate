package verigate.adapter.dharesponse.infrastructure.functions.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.dharesponse.application.handlers.DefaultProcessDhaResponseCommandHandler;
import verigate.adapter.dharesponse.infrastructure.functions.lambda.di.factories.ProcessDhaResponseDependencyFactory;

/** Lambda handler that processes DHA response emails from SQS. */
public class ProcessDhaResponseLambdaHandler
    implements RequestHandler<SQSEvent, SQSBatchResponse> {

  private static final Logger logger =
      LoggerFactory.getLogger(ProcessDhaResponseLambdaHandler.class);
  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper();

  private final DefaultProcessDhaResponseCommandHandler
      commandHandler;

  /** Creates a handler with default dependency factory. */
  public ProcessDhaResponseLambdaHandler() {
    this(new ProcessDhaResponseDependencyFactory());
  }

  /** Creates a handler with the given dependency factory. */
  public ProcessDhaResponseLambdaHandler(
      ProcessDhaResponseDependencyFactory factory) {
    this.commandHandler = factory.getCommandHandler();
  }

  @Override
  public SQSBatchResponse handleRequest(
      SQSEvent event, Context context) {
    List<SQSBatchResponse.BatchItemFailure> failures =
        new ArrayList<>();

    for (SQSEvent.SQSMessage message : event.getRecords()) {
      try {
        // Parse S3 event notification from the SQS message body
        JsonNode sqsBody =
            OBJECT_MAPPER.readTree(message.getBody());
        JsonNode records = sqsBody.get("Records");

        if (records == null || !records.isArray()
            || records.isEmpty()) {
          logger.warn("No S3 records in SQS message: {}",
              message.getMessageId());
          continue;
        }

        for (JsonNode record : records) {
          String bucketName = record.path("s3")
              .path("bucket").path("name").asText();
          String objectKey = record.path("s3")
              .path("object").path("key").asText();

          if (bucketName.isBlank() || objectKey.isBlank()) {
            logger.warn(
                "Invalid S3 event: bucket={}, key={}",
                bucketName, objectKey);
            continue;
          }

          // URL-decode the object key
          objectKey = URLDecoder.decode(
              objectKey, StandardCharsets.UTF_8);

          commandHandler.handle(bucketName, objectKey);
        }
      } catch (DefaultProcessDhaResponseCommandHandler
          .PermanentProcessingException e) {
        logger.error(
            "Permanent failure processing SQS message {}: {}",
            message.getMessageId(), e.getMessage());
        failures.add(new SQSBatchResponse.BatchItemFailure(
            message.getMessageId()));
      } catch (DefaultProcessDhaResponseCommandHandler
          .TransientProcessingException e) {
        logger.warn(
            "Transient failure processing SQS message {}: {}",
            message.getMessageId(), e.getMessage());
        failures.add(new SQSBatchResponse.BatchItemFailure(
            message.getMessageId()));
      } catch (Exception e) {
        logger.error(
            "Unexpected error processing SQS message {}",
            message.getMessageId(), e);
        failures.add(new SQSBatchResponse.BatchItemFailure(
            message.getMessageId()));
      }
    }

    return SQSBatchResponse.builder()
        .withBatchItemFailures(failures).build();
  }
}
