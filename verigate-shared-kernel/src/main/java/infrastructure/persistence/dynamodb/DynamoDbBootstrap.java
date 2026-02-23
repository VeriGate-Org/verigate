/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.persistence.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * This class is the base class for all DynamoDb objects in the application. It contains common
 * functionality for interacting with DynamoDB tables, such as initializing the DynamoDB client and
 * table, and adding, updating, and deleting items in the table. It uses the AWS SDK for Java to
 * interact with DynamoDB.
 */
public abstract class DynamoDbBootstrap<T> {
  protected final String tableName;
  protected final DynamoDbEnhancedClient enhancedClient;
  protected final DynamoDbTable<T> myTable;
  protected String globalSecondaryIndex;

  /**
   * Construct with all attributes supplied.
   *
   * @param dynamoDbClient The DynamoDb client to use.
   * @param tableName The target table name.
   * @param globalSecondaryIndex The GSI name.
   */
  public DynamoDbBootstrap(DynamoDbClient dynamoDbClient, String tableName,
      String globalSecondaryIndex) {
    this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    this.tableName = tableName;
    this.myTable = enhancedClient.table(tableName, getTableSchema());
    this.globalSecondaryIndex = globalSecondaryIndex;
  }

  /**
   * Construct with a default DynamoDb client.
   *
   * @param tableName table name
   * @param globalSecondaryIndex global secondary index
   */
  public DynamoDbBootstrap(String tableName, String globalSecondaryIndex) {
    this(
        DynamoDbClient.builder().build(),
        tableName,
        globalSecondaryIndex
    );
  }

  /**
   * Construct with a default DynamoDb client and no GSI.
   *
   * @param tableName table name
   */
  public DynamoDbBootstrap(String tableName) {
    this(tableName, null);
  }

  protected abstract TableSchema<T> getTableSchema();
}
