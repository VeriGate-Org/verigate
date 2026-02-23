/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

import java.time.Instant;

/**
 * This class provides utility methods to add time to an Instant object. It includes methods to add
 * minutes and hours to an Instant.
 */
public class CreateFutureInstant {

  /**
   * Adds the specified number of minutes to the given Instant object.
   *
   * @param minutes The number of minutes to add to the Instant object.
   * @return The Instant object with the specified number of minutes added.
   */
  public static Instant nowWithAddedMinutes(long minutes) {
    return Instant.now().plusSeconds(minutes * 60);
  }
}
