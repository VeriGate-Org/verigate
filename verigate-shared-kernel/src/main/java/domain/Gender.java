/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

/**
 * Enum for gender types.
 */
public enum Gender {
  MALE("Male"),
  FEMALE("Female"),
  NONBINARY("Nonbinary"),
  PREFER_NOT_TO_SAY("Prefer not to say");

  private final String userFriendlyValue;

  Gender(String userFriendlyValue) {
    this.userFriendlyValue = userFriendlyValue;
  }

  public String getUserFriendlyValue() {
    return userFriendlyValue;
  }
}
