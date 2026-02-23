/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.mappers;


import java.time.Instant;

/** A utility class to map ISO 8601 date strings to Instant objects. */
public class IsoDateTimeStringMapper {
  public IsoDateTimeStringMapper() {}

  /**
   * Maps an ISO 8601 date string to a Instant object.
   *
   * @param isoDate The ISO 8601 date string to map.
   * @return The Instant object.
   */
  public static Instant map(String isoDate) {
    // Check if isoDate is null or empty
    if (isoDate == null || isoDate.isEmpty()) {
      return null;
    }

    // Parse the ISO 8601 date string
    // FIXME: AB#4067 This will parse the date in UTC - what time zones are the dates in?
    return Instant.parse(isoDate);
  }
}
