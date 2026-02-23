/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.mappers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/** A utility class to map Instant objects to date objects. */
public final class InstantToDateMapper {
  public static LocalDate mapToLocalDate(Instant instant) {
    return instant == null ? null : instant.atZone(ZoneId.systemDefault()).toLocalDate();
  }

  public static LocalDateTime mapToLocalDateTime(Instant instant) {
    return instant == null ? null : instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
  }
}
