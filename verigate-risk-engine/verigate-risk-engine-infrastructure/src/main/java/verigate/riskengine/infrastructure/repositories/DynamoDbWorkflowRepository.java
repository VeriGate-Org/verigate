/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.infrastructure.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import verigate.riskengine.domain.enums.VerificationType;
import verigate.riskengine.domain.enums.WorkflowStatus;
import verigate.riskengine.domain.models.AdapterScore;
import verigate.riskengine.domain.models.VerificationWorkflow;
import verigate.riskengine.domain.services.WorkflowRepository;
import verigate.riskengine.infrastructure.repositories.datamodels.VerificationWorkflowDataModel;

public class DynamoDbWorkflowRepository implements WorkflowRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoDbWorkflowRepository.class);

    private final DynamoDbTable<VerificationWorkflowDataModel> table;
    private final DynamoDbIndex<VerificationWorkflowDataModel> statusIndex;
    private final ObjectMapper objectMapper;

    @Inject
    public DynamoDbWorkflowRepository(
            DynamoDbEnhancedClient enhancedClient,
            ObjectMapper objectMapper,
            @Named("verificationWorkflowsTableName") String tableName) {
        this.table = enhancedClient.table(tableName,
            TableSchema.fromBean(VerificationWorkflowDataModel.class));
        this.statusIndex = table.index("status-created-index");
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(VerificationWorkflow workflow) {
        try {
            table.putItem(toDataModel(workflow));
        } catch (Exception e) {
            LOG.error("Failed to save workflow {}", workflow.workflowId(), e);
            throw new RuntimeException("Failed to save workflow", e);
        }
    }

    @Override
    public Optional<VerificationWorkflow> findByWorkflowId(UUID workflowId) {
        try {
            var item = table.getItem(
                Key.builder().partitionValue(workflowId.toString()).build());
            if (item == null) return Optional.empty();
            return Optional.of(toDomain(item));
        } catch (Exception e) {
            LOG.error("Failed to fetch workflow {}", workflowId, e);
            return Optional.empty();
        }
    }

    @Override
    public List<VerificationWorkflow> findPendingBefore(Instant cutoff, int limit) {
        try {
            var request = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.sortLessThanOrEqualTo(
                    Key.builder()
                        .partitionValue(WorkflowStatus.PENDING.name())
                        .sortValue(cutoff.toString())
                        .build()))
                .limit(limit)
                .build();

            return statusIndex.query(request).stream()
                .flatMap(page -> page.items().stream())
                .map(this::toDomain)
                .limit(limit)
                .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Failed to query pending workflows before {}", cutoff, e);
            return List.of();
        }
    }

    private VerificationWorkflow toDomain(VerificationWorkflowDataModel model) {
        try {
            Map<VerificationType, UUID> expectedChecks = objectMapper.readValue(
                model.getExpectedChecksJson(), new TypeReference<>() {});
            Map<VerificationType, AdapterScore> completedChecks = objectMapper.readValue(
                model.getCompletedChecksJson(), new TypeReference<>() {});

            return new VerificationWorkflow(
                UUID.fromString(model.getWorkflowId()),
                model.getPartnerId(),
                model.getPolicyId(),
                expectedChecks,
                completedChecks,
                WorkflowStatus.valueOf(model.getStatus()),
                Instant.parse(model.getCreatedAt()),
                model.getCompletedAt() != null ? Instant.parse(model.getCompletedAt()) : null
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize workflow", e);
        }
    }

    private VerificationWorkflowDataModel toDataModel(VerificationWorkflow workflow) {
        try {
            return VerificationWorkflowDataModel.builder()
                .workflowId(workflow.workflowId().toString())
                .partnerId(workflow.partnerId())
                .policyId(workflow.policyId())
                .expectedChecksJson(objectMapper.writeValueAsString(workflow.expectedChecks()))
                .completedChecksJson(objectMapper.writeValueAsString(workflow.completedChecks()))
                .status(workflow.status().name())
                .createdAt(workflow.createdAt().toString())
                .completedAt(workflow.completedAt() != null ? workflow.completedAt().toString() : null)
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize workflow", e);
        }
    }
}
