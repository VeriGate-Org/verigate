/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.postgres;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A record that holds information about the database cluster endpoints and the
 * associated secret path.
 * This information is typically stored in AWS Systems Manager (SSM) Parameter Store and is used to
 * retrieve database connection details as well as secrets needed for authentication.
 *
 *
 * <p>This record is used to encapsulate the following details:
 * <ul>
 *   <li><strong>Cluster Endpoint:</strong> The primary endpoint for the database cluster,
 * used for read/write operations.</li>
 *   <li><strong>Cluster Read-Only Endpoint:</strong> The read-only endpoint
 * for the database cluster, used for read operations.</li>
 *   <li><strong>Secret Path:</strong> The ARN of the secret stored in AWS Secrets Manager,
 * which contains the credentials for the database.</li>
 * </ul>
 * </p>
 *
 * <p>Usage Example:
 * <pre>
 * {@code
 * // Assuming you have the JSON data stored in SSM and retrieved as a string
 * String json = ...; // Retrieve JSON from SSM
 * ObjectMapper objectMapper = new ObjectMapper();
 * DatabaseClusterInfo clusterInfo = objectMapper.readValue(json, DatabaseClusterInfo.class);
 *
 * // Access the cluster information
 * String endpoint = clusterInfo.clusterEndpoint();
 * String readonlyEndpoint = clusterInfo.clusterReadonlyEndpoint();
 * String secretArn = clusterInfo.secretPath();
 * }
 * </pre>
 * </p>
 *
 * <p>Dependencies:
 * <ul>
 *   <li>Jackson Databind for JSON parsing</li>
 * </ul>
 * </p>
 *
 * @param clusterEndpoint The primary endpoint for the database cluster.
 * @param clusterReadonlyEndpoint The read-only endpoint for the database cluster.
 * @param secretPath The ARN of the secret in AWS Secrets Manager that stores
 *     the database credentials.
 */
public record DatabaseClusterInfo(
    @JsonProperty("cluster_endpoint") String clusterEndpoint,
    @JsonProperty("cluster_readonly_endpoint") String clusterReadonlyEndpoint,
    @JsonProperty("secret_path") String secretPath,
    @JsonProperty("database_port") String databasePort) {

  /**
   * Returns the primary endpoint for the database cluster.
   */
  public String getHost() {
    if (databasePort == null) {
      return String.format("jdbc:postgresql://%s:5432/", clusterEndpoint);
    }
    return String.format("jdbc:postgresql://%s:" + databasePort + "/", clusterEndpoint);
  }
}
