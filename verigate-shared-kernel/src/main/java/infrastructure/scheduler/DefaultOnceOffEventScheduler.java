/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.scheduler;

import com.google.inject.Inject;
import crosscutting.environment.Environment;
import java.time.Instant;
import java.util.Collections;
import java.util.logging.Logger;
import software.amazon.awssdk.services.scheduler.SchedulerClient;
import software.amazon.awssdk.services.scheduler.model.ActionAfterCompletion;
import software.amazon.awssdk.services.scheduler.model.CreateScheduleRequest;
import software.amazon.awssdk.services.scheduler.model.FlexibleTimeWindow;
import software.amazon.awssdk.services.scheduler.model.FlexibleTimeWindowMode;
import software.amazon.awssdk.services.scheduler.model.Target;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

/**
 * The {@code DefaultOnceOffEventScheduler} class provides a default implementation of the
 * {@link OnceOffEventScheduler} interface.
 */
public final class DefaultOnceOffEventScheduler implements OnceOffEventScheduler {
  SchedulerClient schedulerClient;
  String schedulerExecutionRoleArn;
  SqsClient sqsClient;
  private static final Logger logger =
      Logger.getLogger(DefaultOnceOffEventScheduler.class.getName());

  /**
   * Constructor for the DefaultOnceOffEventScheduler.
   *
   * @param environment The environment object.
   */
  @Inject
  public DefaultOnceOffEventScheduler(Environment environment) {
    this.schedulerClient = SchedulerClient.builder().build();
    this.sqsClient = SqsClient.builder().build();
    this.schedulerExecutionRoleArn = environment.get("EVENTBRIDGE_SCHEDULER_ASSUMED_ROLE_ARN");
  }

  /**
   * Schedule a one-off queued event.
   */
  public void scheduleQueueMessage(
      String queueName, String eventBody, String scheduleName, Instant scheduledInstant) {

    logger.info("Scheduling queue message with event body: " + eventBody);

    var queueUrl = this.getSqsQueueUrl(queueName);
    var queueArn = this.getSqsQueueArn(queueUrl);

    logger.info("Queue ARN: " + queueArn);
    logger.info("Queue URL: " + queueUrl);

    var target =
        Target.builder()
            .arn(queueArn)
            .input(eventBody)
            .roleArn(this.schedulerExecutionRoleArn)
            .build();

    String scheduleExpression = "at(" + this.parseInstant(scheduledInstant) + ")";

    logger.info("Schedule expression: " + scheduleExpression);

    var createScheduleRequest =
        this.createScheduleRequest(scheduleName, scheduleExpression, target);

    logger.info("Creating schedule");

    var result = schedulerClient.createSchedule(createScheduleRequest);

    logger.info(result.toString());
  }

  private String parseInstant(Instant instant) {
    return instant.toString().substring(0, 19);
  }

  private String getSqsQueueUrl(String queueName) {
    return sqsClient
        .getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build())
        .queueUrl();
  }

  private String getSqsQueueArn(String queueUrl) {

    var attr = Collections.singletonList(QueueAttributeName.QUEUE_ARN);

    GetQueueAttributesRequest request =
        GetQueueAttributesRequest.builder().queueUrl(queueUrl).attributeNames(attr).build();

    GetQueueAttributesResponse response = sqsClient.getQueueAttributes(request);
    return response.attributes().get(QueueAttributeName.QUEUE_ARN);
  }

  private CreateScheduleRequest createScheduleRequest(
      String scheduleName, String scheduleExpression, Target target) {

    return CreateScheduleRequest.builder()
        .name(scheduleName)
        .scheduleExpression(scheduleExpression)
        .target(target)
        .actionAfterCompletion(ActionAfterCompletion.DELETE)
        .flexibleTimeWindow(FlexibleTimeWindow.builder().mode(FlexibleTimeWindowMode.OFF).build())
        .build();
  }
}
