/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories.datamodels;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import verigate.billing.domain.models.BillingPlan;

/**
 * DynamoDB data model for billing plans.
 * Table: {@code verigate-billing-plans}
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 * </ul>
 */
@DynamoDbBean
public class BillingPlanDataModel {

    private String partnerId;
    private String planId;
    private Map<String, String> pricePerVerificationType;
    private String monthlyMinimum;
    private boolean active;

    public BillingPlanDataModel() {
        // Required by DynamoDB enhanced client
    }

    /**
     * Creates a data model from a domain billing plan.
     *
     * @param plan the domain billing plan
     * @return the corresponding data model
     */
    public static BillingPlanDataModel fromDomain(BillingPlan plan) {
        BillingPlanDataModel model = new BillingPlanDataModel();
        model.setPartnerId(plan.partnerId());
        model.setPlanId(plan.planId());
        model.setActive(plan.active());
        model.setMonthlyMinimum(plan.monthlyMinimum().toPlainString());

        Map<String, String> priceMap = new HashMap<>();
        for (Map.Entry<String, BigDecimal> entry : plan.pricePerVerificationType().entrySet()) {
            priceMap.put(entry.getKey(), entry.getValue().toPlainString());
        }
        model.setPricePerVerificationType(priceMap);

        return model;
    }

    /**
     * Converts this data model to a domain billing plan.
     *
     * @return the corresponding domain billing plan
     */
    public BillingPlan toDomain() {
        Map<String, BigDecimal> domainPriceMap = new HashMap<>();
        if (pricePerVerificationType != null) {
            for (Map.Entry<String, String> entry : pricePerVerificationType.entrySet()) {
                domainPriceMap.put(entry.getKey(), new BigDecimal(entry.getValue()));
            }
        }

        return new BillingPlan(
            planId,
            partnerId,
            domainPriceMap,
            new BigDecimal(monthlyMinimum),
            active
        );
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("partnerId")
    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    @DynamoDbAttribute("planId")
    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    @DynamoDbAttribute("pricePerVerificationType")
    public Map<String, String> getPricePerVerificationType() {
        return pricePerVerificationType;
    }

    public void setPricePerVerificationType(Map<String, String> pricePerVerificationType) {
        this.pricePerVerificationType = pricePerVerificationType;
    }

    @DynamoDbAttribute("monthlyMinimum")
    public String getMonthlyMinimum() {
        return monthlyMinimum;
    }

    public void setMonthlyMinimum(String monthlyMinimum) {
        this.monthlyMinimum = monthlyMinimum;
    }

    @DynamoDbAttribute("active")
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
