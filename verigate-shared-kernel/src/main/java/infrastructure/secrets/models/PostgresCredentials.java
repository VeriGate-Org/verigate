/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.secrets.models;

/**
 * PostgresCredentials is a simple model for holding the username and password for a Postgres
 * database.
 */
public record PostgresCredentials(String username, String password, String host, String port) {

  public String getHost() {
    return String.format("jdbc:postgresql://%s:%s/", host, port);
  }
}
