/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.formatting;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.javamoney.moneta.Money;

/**
 * The UserFriendlyCurrencyFormatter class is responsible for formatting currency amounts in a
 * user-friendly way.
 */
public class UserFriendlyCurrencyFormatter {

  /**
   * Formats the given Money amount as a user-friendly currency string.
   *
   * @param amount The Money amount to format.
   * @return The formatted currency string.
   */
  public static String format(Money amount) {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("en-ZA"));
    // The defaults for the en-ZA locale should be "," as decimal separator,
    // and a "non-breaking space" as grouping separator. But for unknown reasons on some JVMs,
    // the defaults seems to be different.
    // Hard-coding it here ensures consistency across different VMs.
    symbols.setDecimalSeparator('.');
    symbols.setGroupingSeparator(',');
    DecimalFormat moneyFormat = new DecimalFormat("#,##0.00", symbols);
    String formattedAmount = "R" + moneyFormat.format(amount.getNumberStripped());
    return formattedAmount;
  }
}
