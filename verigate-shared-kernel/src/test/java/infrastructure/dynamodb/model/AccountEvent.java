/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.dynamodb.model;

import crosscutting.serialization.DataContract;
import java.util.UUID;

public record AccountEvent(
    @DataContract UUID id,
    @DataContract String accountNumber,
    @DataContract String transactionType,
    @DataContract int amount,
    @DataContract long logicalClock) {
  public AccountEvent() {
    this(null, null, null, 0, 0);
  }
}
