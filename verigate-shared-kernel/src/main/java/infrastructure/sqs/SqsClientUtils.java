/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.sqs;

import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

/** Utilities for classes in this package. */
final class SqsClientUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(SqsClientUtils.class);

  private SqsClientUtils() {} // static utility methods only

  /**
   * Sends the specified message.
   *
   * @throws TransientException if the send request failed for any reason
   *     (so calling code can retry)
   */
  public static void sendMessage(String message, SqsClient sqsClient, String queueUrl) {

    LOGGER.debug("Sending message to queue {}: {}.", queueUrl, message);

    final SendMessageResponse result;
    try {
      result =
          sqsClient.sendMessage(
              SendMessageRequest.builder().queueUrl(queueUrl).messageBody(message).build());
    } catch (Exception e) { // can tweak if we find some exceptions equate to permanent exceptions
      throw new TransientException(e);
    }

    if (result.sdkHttpResponse().statusCode() != 200) {
      throw new TransientException(
          "Failed to send message to queue %s: %s".formatted(queueUrl, message));
    }
    LOGGER.info("Message {} sent successfully to queue {}.", result.messageId(), queueUrl);
  }

  /**
   * Deletes the message with the specified receiptHandle from the queue.
   *
   * @throws TransientException if the delete request failed for any reason
   *     (so calling code can retry)
   */
  public static void deleteMessage(String receiptHandle, SqsClient sqsClient, String queueUrl) {

    LOGGER.debug("Deleting receiptHandle from queue {}: {}.", queueUrl, receiptHandle);

    final DeleteMessageResponse result;
    try {
      result = sqsClient.deleteMessage(
          DeleteMessageRequest.builder().queueUrl(queueUrl).receiptHandle(receiptHandle).build());
    } catch (Exception e) { // can tweak if we find some exceptions equate to permanent exceptions
      throw new TransientException(e);
    }

    if (result.sdkHttpResponse().statusCode() != 200) {
      throw new TransientException(
          "Failed to delete receiptHandle from queue %s: %s".formatted(queueUrl, receiptHandle));
    }
    LOGGER.info("Receipt handle {} deleted successfully from queue {}.", receiptHandle, queueUrl);
  }

  /**
   * Gets the message at the front of queue.
   *
   * @throws TransientException if the get request failed for any reason
   *     (so calling code can retry)
   */
  public static Message peekAtMessage(SqsClient sqsClient, String queueUrl) {

    LOGGER.debug("Getting message from queue {}.", queueUrl);

    final ReceiveMessageResponse result;
    try {
      result =
          sqsClient.receiveMessage(
              ReceiveMessageRequest.builder()
                  .queueUrl(queueUrl)
                  .maxNumberOfMessages(1) // only receive the message at the front of the queue
                  .visibilityTimeout(0) // don't hide the message from other consumers
                  .build());
    } catch (Exception e) { // can tweak if we find some exceptions equate to permanent exceptions
      throw new TransientException(e);
    }

    if (result.sdkHttpResponse().statusCode() != 200) {
      throw new TransientException(
          "Failed to peek at message at front of queue %s.".formatted(queueUrl));
    }

    if (result.messages().isEmpty()) {
      LOGGER.debug("No message found at front of queue {}.", queueUrl);
      return null;
    }
    
    Message message = result.messages().get(0);
    LOGGER.info("Peeked at message {} at front of queue {}.", message.messageId(), queueUrl);

    return message;
  }
}
