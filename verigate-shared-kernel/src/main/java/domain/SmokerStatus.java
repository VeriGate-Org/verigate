/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

/**
 * Enum for smoker status.
 */
public enum SmokerStatus {
  CURRENT_SMOKER,
  LAST_SMOKED_LAST_MONTH,
  LAST_SMOKED_LAST_YEAR,  
  LAST_SMOKED_MORE_THAN_A_YEAR_AGO,
  NEVER_SMOKED,
}