/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.model;

import crosscutting.serialization.DataContract;
import domain.Entity;
import domain.IncomeType;
import domain.Percentage;
import domain.invariants.SpecificationResult;

public final class ComplexEntity extends Entity<Integer> {

  @DataContract private Percentage percentage;

  @DataContract private IncomeType incomeType;

  private ComplexEntity() {}

  @Override
  public SpecificationResult checkSpecification() {
    return null;
  }

  public ComplexEntity(Percentage percentage, IncomeType incomeType) {
    this.percentage = percentage;
    this.incomeType = incomeType;
  }
}
