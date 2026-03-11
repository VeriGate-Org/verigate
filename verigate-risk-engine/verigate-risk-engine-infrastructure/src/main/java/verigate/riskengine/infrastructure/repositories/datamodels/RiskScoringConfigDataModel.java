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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class RiskScoringConfigDataModel {

    private String partnerId;
    private String weightsJson;
    private String strategy;
    private String tiersJson;
    private String overrideRulesJson;
    private String version;
    private String updatedAt;

    @DynamoDbPartitionKey
    public String getPartnerId() {
        return partnerId;
    }
}
