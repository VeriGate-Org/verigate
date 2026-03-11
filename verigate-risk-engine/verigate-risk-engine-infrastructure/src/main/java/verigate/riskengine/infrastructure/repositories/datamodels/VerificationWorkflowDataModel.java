/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.infrastructure.repositories.datamodels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class VerificationWorkflowDataModel {

    private String workflowId;
    private String partnerId;
    private String policyId;
    private String expectedChecksJson;
    private String completedChecksJson;
    private String status;
    private String createdAt;
    private String completedAt;

    @DynamoDbPartitionKey
    public String getWorkflowId() {
        return workflowId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "status-created-index")
    public String getStatus() {
        return status;
    }

    @DynamoDbSecondarySortKey(indexNames = "status-created-index")
    public String getCreatedAt() {
        return createdAt;
    }
}
