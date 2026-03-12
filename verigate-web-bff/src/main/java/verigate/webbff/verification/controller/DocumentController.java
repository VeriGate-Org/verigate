package verigate.webbff.verification.controller;

import java.time.Duration;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.config.properties.DocumentProperties;

@RestController
@RequestMapping("/api/partner/documents")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    private static final Duration PRESIGNED_URL_EXPIRY = Duration.ofMinutes(15);

    private final S3Presigner s3Presigner;
    private final DocumentProperties documentProperties;

    public DocumentController(S3Presigner s3Presigner, DocumentProperties documentProperties) {
        this.s3Presigner = s3Presigner;
        this.documentProperties = documentProperties;
    }

    @PostMapping("/presigned-url")
    public ResponseEntity<PresignedUrlResponse> generatePresignedUrl(
            @RequestBody PresignedUrlRequest request) {
        String partnerId = PartnerContextHolder.requirePartnerId();
        String s3ObjectKey = partnerId + "/" + request.documentType() + "/" + UUID.randomUUID() + "/" + request.fileName();
        String bucketName = documentProperties.getS3BucketName();

        logger.info("Generating presigned PUT URL: partnerId={}, documentType={}, fileName={}",
                partnerId, request.documentType(), request.fileName());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3ObjectKey)
                .contentType(request.contentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(PRESIGNED_URL_EXPIRY)
                .putObjectRequest(putObjectRequest)
                .build();

        String uploadUrl = s3Presigner.presignPutObject(presignRequest).url().toString();

        logger.info("Presigned URL generated: s3ObjectKey={}", s3ObjectKey);

        return ResponseEntity.ok(new PresignedUrlResponse(uploadUrl, bucketName, s3ObjectKey));
    }

    public record PresignedUrlRequest(String fileName, String contentType, String documentType) {}

    public record PresignedUrlResponse(String uploadUrl, String s3BucketName, String s3ObjectKey) {}
}
