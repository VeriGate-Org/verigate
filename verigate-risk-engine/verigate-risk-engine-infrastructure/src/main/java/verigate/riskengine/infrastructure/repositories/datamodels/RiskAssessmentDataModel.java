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
public class RiskAssessmentDataModel {

    private String verificationId;
    private String assessmentId;
    private String partnerId;
    private int compositeScore;
    private String riskTier;
    private String decision;
    private String decisionReason;
    private String individualScoresJson;
    private boolean overrideApplied;
    private String overrideRuleId;
    private String assessedAt;

    @DynamoDbPartitionKey
    public String getVerificationId() {
        return verificationId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "partner-assessed-index")
    public String getPartnerId() {
        return partnerId;
    }

    @DynamoDbSecondarySortKey(indexNames = "partner-assessed-index")
    public String getAssessedAt() {
        return assessedAt;
    }
}
