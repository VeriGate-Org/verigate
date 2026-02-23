/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.mappers;

import java.time.LocalDate;
import java.time.Period;

/**
 * The AgeCalculator class provides methods for calculating a person's age.
 */
public class AgeCalculator {
  public static Integer calculate(LocalDate dateOfBirth) {
    LocalDate currentDate = LocalDate.now();
    return calculate(dateOfBirth, currentDate);
  }

  public static Integer calculate(LocalDate dateOfBirth, LocalDate currentDate) {
    return Period.between(dateOfBirth, currentDate).getYears();
  }
}
