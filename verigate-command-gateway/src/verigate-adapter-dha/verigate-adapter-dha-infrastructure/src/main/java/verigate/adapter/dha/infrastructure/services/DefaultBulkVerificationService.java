/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.dha.domain.models.BillingGroupSelection;
import verigate.adapter.dha.domain.models.BulkVerificationJob;
import verigate.adapter.dha.domain.models.BulkVerificationResult;
import verigate.adapter.dha.infrastructure.bulk.BulkCsvBuilder;
import verigate.adapter.dha.infrastructure.bulk.BulkJobRepository;
import verigate.adapter.dha.infrastructure.bulk.BulkResultParser;
import verigate.adapter.dha.infrastructure.config.HanisConfiguration;
import verigate.adapter.dha.infrastructure.soap.HanisBulkSoapClient;

/**
 * Service for managing bulk verification jobs via HANIS.
 */
public class DefaultBulkVerificationService {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultBulkVerificationService.class);

  private final HanisBulkSoapClient bulkSoapClient;
  private final BulkCsvBuilder csvBuilder;
  private final BulkResultParser resultParser;
  private final BulkJobRepository jobRepository;
  private final HanisConfiguration configuration;

  public DefaultBulkVerificationService(
      HanisBulkSoapClient bulkSoapClient,
      BulkCsvBuilder csvBuilder,
      BulkResultParser resultParser,
      BulkJobRepository jobRepository,
      HanisConfiguration configuration) {
    this.bulkSoapClient = bulkSoapClient;
    this.csvBuilder = csvBuilder;
    this.resultParser = resultParser;
    this.jobRepository = jobRepository;
    this.configuration = configuration;
  }

  /**
   * Creates a new bulk verification job and uploads it to HANIS.
   *
   * @param partnerId     the partner submitting the job
   * @param billingGroups the billing group selection
   * @param idNumbers     the list of ID numbers to verify
   * @return the created job
   */
  public BulkVerificationJob createJob(
      String partnerId, BillingGroupSelection billingGroups, List<String> idNumbers) {

    String jobId = UUID.randomUUID().toString();
    BulkVerificationJob job = BulkVerificationJob.create(jobId, partnerId, billingGroups,
        idNumbers.size());

    logger.info("Creating bulk verification job: jobId={}, idCount={}", jobId, idNumbers.size());

    // Build and upload the CSV
    byte[] zipData = csvBuilder.buildZippedCsv(billingGroups, idNumbers);
    HanisBulkSoapClient.UploadResult uploadResult = bulkSoapClient.uploadFile(
        configuration.getSiteId(), configuration.getWorkstationId(), zipData);

    if (uploadResult.isSuccess()) {
      job = job.withRequestId(uploadResult.requestId());
      logger.info("Bulk job uploaded: jobId={}, requestId={}", jobId, uploadResult.requestId());
    } else {
      job = job.withError(uploadResult.errorCode(), uploadResult.errorDescription());
      logger.error("Bulk job upload failed: jobId={}, error={} - {}",
          jobId, uploadResult.errorCode(), uploadResult.errorDescription());
    }

    jobRepository.save(job);
    return job;
  }

  /**
   * Checks the status of a bulk verification job with HANIS.
   *
   * @param jobId the job ID
   * @return the updated job
   */
  public Optional<BulkVerificationJob> checkJobStatus(String jobId) {
    return jobRepository.findById(jobId).map(job -> {
      if (job.status() != BulkVerificationJob.BulkJobStatus.UPLOADED
          && job.status() != BulkVerificationJob.BulkJobStatus.PROCESSING) {
        return job; // Nothing to check
      }

      if (job.requestId() == null) {
        return job;
      }

      HanisBulkSoapClient.RequestStatusResult statusResult =
          bulkSoapClient.requestStatus(
              configuration.getSiteId(), configuration.getWorkstationId(), job.requestId());

      BulkVerificationJob updatedJob;
      if (statusResult.isReady()) {
        updatedJob = job.withCompletion();
        logger.info("Bulk job completed: jobId={}", jobId);
      } else if (statusResult.isProcessing()) {
        updatedJob = job.withStatus(BulkVerificationJob.BulkJobStatus.PROCESSING);
        logger.info("Bulk job still processing: jobId={}", jobId);
      } else {
        updatedJob = job.withError(statusResult.errorCode(), statusResult.errorDescription());
        logger.error("Bulk job error: jobId={}, error={}", jobId, statusResult.errorDescription());
      }

      jobRepository.save(updatedJob);
      return updatedJob;
    });
  }

  /**
   * Lists bulk verification jobs for a partner.
   */
  public List<BulkVerificationJob> listJobs(String partnerId, int limit) {
    return jobRepository.findByPartnerId(partnerId, limit);
  }

  /**
   * Gets a job by ID.
   */
  public Optional<BulkVerificationJob> getJob(String jobId) {
    return jobRepository.findById(jobId);
  }
}
