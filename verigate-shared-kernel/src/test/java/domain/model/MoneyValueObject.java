/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public final class MoneyValueObject {

  private final BigDecimal amount;
  private final Currency currency;

  public MoneyValueObject() {
    this.amount = null;
    this.currency = null;
  }

  public MoneyValueObject(BigDecimal amount, Currency currency) {
    this.amount = amount;
    this.currency = currency;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Currency getCurrency() {
    return currency;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MoneyValueObject money = (MoneyValueObject) o;
    return Objects.equals(amount, money.amount) && Objects.equals(currency, money.currency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, currency);
  }

  @Override
  public String toString() {
    return String.format("%s %s", currency.getSymbol(), amount);
  }
}
