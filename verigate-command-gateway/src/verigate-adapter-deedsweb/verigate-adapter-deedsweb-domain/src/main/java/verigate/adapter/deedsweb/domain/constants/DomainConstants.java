/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.constants;

/**
 * Domain constants for OpenSanctions adapter.
 */
public class DomainConstants {

  // Default API parameters
  public static final String DEFAULT_DATASET = "sanctions";
  public static final int DEFAULT_LIMIT = 10;
  public static final double DEFAULT_THRESHOLD = 0.7;
  public static final double DEFAULT_CUTOFF = 0.5;
  public static final String DEFAULT_ALGORITHM = "logic-v1";
  
  // Timeout and retry settings
  public static final int DEFAULT_TIMEOUT_SECONDS = 30;
  public static final int DEFAULT_RETRY_ATTEMPTS = 3;
  public static final int DEFAULT_RETRY_DELAY_MS = 1000;
  
  // Match score thresholds for verification outcomes
  public static final double HIGH_MATCH_THRESHOLD = 0.9;
  public static final double MEDIUM_MATCH_THRESHOLD = 0.7;
  public static final double LOW_MATCH_THRESHOLD = 0.5;
  
  // Entity schemas
  public static final String PERSON_SCHEMA = "Person";
  public static final String COMPANY_SCHEMA = "Company";
  public static final String ORGANIZATION_SCHEMA = "Organization";
  
  // Common topics for sanctions screening
  public static final String SANCTIONS_TOPIC = "sanction";
  public static final String PEP_TOPIC = "role.pep";
  
  private DomainConstants() {
    // Utility class
  }
}
