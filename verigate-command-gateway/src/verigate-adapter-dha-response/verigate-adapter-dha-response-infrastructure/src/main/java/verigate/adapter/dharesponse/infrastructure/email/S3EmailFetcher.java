package verigate.adapter.dharesponse.infrastructure.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import verigate.adapter.dharesponse.application.handlers.DefaultProcessDhaResponseCommandHandler;

public class S3EmailFetcher implements DefaultProcessDhaResponseCommandHandler.EmailFetcher {

    private static final Logger logger = LoggerFactory.getLogger(S3EmailFetcher.class);

    private final S3Client s3Client;

    public S3EmailFetcher(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public byte[] fetch(String bucketName, String objectKey) {
        logger.debug("Fetching email from S3: bucket={}, key={}", bucketName, objectKey);
        try (var response = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build())) {
            return response.readAllBytes();
        } catch (Exception e) {
            throw new DefaultProcessDhaResponseCommandHandler.TransientProcessingException(
                    "Failed to fetch email from S3: " + objectKey, e);
        }
    }
}
