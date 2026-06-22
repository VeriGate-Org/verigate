package verigate.adapter.dharesponse.infrastructure.functions.lambda.di.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ses.SesClient;
import verigate.adapter.dharesponse.application.handlers.DefaultProcessDhaResponseCommandHandler;
import verigate.adapter.dharesponse.domain.services.DhaResponseParserService;
import verigate.adapter.dharesponse.domain.services.DhaResponseReportService;
import verigate.adapter.dharesponse.infrastructure.ai.AiDhaResponseAnalyzer;
import verigate.adapter.dharesponse.infrastructure.email.MimeEmailParser;
import verigate.adapter.dharesponse.infrastructure.email.S3EmailFetcher;
import verigate.adapter.dharesponse.infrastructure.notification.PartnerNotificationSender;
import verigate.adapter.dharesponse.infrastructure.persistence.DhaResponseCommandStoreAdapter;
import verigate.adapter.dharesponse.infrastructure.report.DhaResponsePdfReportGenerator;

/** Guice module providing DHA response processing dependencies. */
public class ProcessDhaResponseServiceModule extends AbstractModule {

  @Override
  protected void configure() {
    // Interface bindings are handled by provider methods
  }

  @Provides
  @Singleton
  ObjectMapper provideObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }

  @Provides
  @Singleton
  S3Client provideS3Client() {
    return S3Client.builder().build();
  }

  @Provides
  @Singleton
  DynamoDbClient provideDynamoDbClient() {
    return DynamoDbClient.builder().build();
  }

  @Provides
  @Singleton
  SesClient provideSesClient() {
    return SesClient.builder().build();
  }

  @Provides
  @Singleton
  BedrockRuntimeClient provideBedrockClient() {
    String region = System.getenv("BEDROCK_REGION");
    return BedrockRuntimeClient.builder()
        .region(Region.of(region != null ? region : "us-east-1"))
        .build();
  }

  @Provides
  @Singleton
  DefaultProcessDhaResponseCommandHandler.EmailFetcher
      provideEmailFetcher(S3Client s3Client) {
    return new S3EmailFetcher(s3Client);
  }

  @Provides
  @Singleton
  DefaultProcessDhaResponseCommandHandler.MimeParser
      provideMimeParser() {
    return new MimeEmailParser();
  }

  @Provides
  @Singleton
  DefaultProcessDhaResponseCommandHandler.CommandStoreAdapter
      provideCommandStoreAdapter(DynamoDbClient dynamoDbClient) {
    String tableName =
        System.getenv("VERIFICATION_COMMAND_STORE_DB");
    return new DhaResponseCommandStoreAdapter(
        dynamoDbClient, tableName);
  }

  @Provides
  @Singleton
  DhaResponseParserService provideAiAnalyzer(
      BedrockRuntimeClient bedrockClient,
      ObjectMapper objectMapper) {
    String modelId = System.getenv("BEDROCK_MODEL_ID");
    return new AiDhaResponseAnalyzer(bedrockClient,
        modelId != null
            ? modelId
            : "us.anthropic.claude-sonnet-4-5-20250929-v1:0",
        objectMapper);
  }

  @Provides
  @Singleton
  DhaResponseReportService provideReportService(S3Client s3Client) {
    String bucketName = System.getenv("DOCUMENTS_BUCKET");
    return new DhaResponsePdfReportGenerator(s3Client, bucketName);
  }

  @Provides
  @Singleton
  DefaultProcessDhaResponseCommandHandler.PartnerNotifier
      providePartnerNotifier(
          SesClient sesClient, DynamoDbClient dynamoDbClient) {
    String partnerHubTable =
        System.getenv("VERIGATE_PARTNER_HUB_TABLE");
    String senderEmail =
        System.getenv("DHA_RESPONSE_SENDER_EMAIL");
    String portalBaseUrl = System.getenv("PORTAL_BASE_URL");
    return new PartnerNotificationSender(
        sesClient, dynamoDbClient,
        partnerHubTable,
        senderEmail != null
            ? senderEmail
            : "dhaverifications@verigate.co.za",
        portalBaseUrl != null
            ? portalBaseUrl
            : "https://portal.verigate.co.za");
  }

  @Provides
  @Singleton
  DefaultProcessDhaResponseCommandHandler provideCommandHandler(
      DefaultProcessDhaResponseCommandHandler.EmailFetcher
          emailFetcher,
      DefaultProcessDhaResponseCommandHandler.MimeParser mimeParser,
      DefaultProcessDhaResponseCommandHandler.CommandStoreAdapter
          commandStoreAdapter,
      DhaResponseParserService aiAnalyzer,
      DhaResponseReportService reportService,
      DefaultProcessDhaResponseCommandHandler.PartnerNotifier
          partnerNotifier) {
    return new DefaultProcessDhaResponseCommandHandler(
        emailFetcher, mimeParser, commandStoreAdapter,
        aiAnalyzer, reportService, partnerNotifier);
  }
}
