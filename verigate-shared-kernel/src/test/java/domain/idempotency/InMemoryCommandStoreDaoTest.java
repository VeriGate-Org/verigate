/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.idempotency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * The {@code InMemoryCommandStoreDaoTest} class contains tests for the
 * {@link InMemoryCommandStoreDao} class.
 */
public class InMemoryCommandStoreDaoTest {

  @Mock private InMemoryCommandStoreDao commandStoreDao;

  @BeforeEach
  public void setUp() {
    commandStoreDao = new InMemoryCommandStoreDao(true);
  }

  @Test
  public void testAdd() {
    UUID id = UUID.randomUUID();
    String contractId = "contractId";
    commandStoreDao.add(id, contractId, 1);
    assertTrue(commandStoreDao.exists(id));
  }

  @Test
  public void testGetStatus() {
    UUID id = UUID.randomUUID();
    String contractId = "contractId";
    commandStoreDao.add(id, contractId, 1);
    assertEquals(CommandStatus.PENDING, commandStoreDao.getStatus(id));
  }

  @Test
  public void testAddingCommandUsingCheckStatusOrAdd() {
    UUID id = UUID.randomUUID();
    String contractId = "contractId";
    assertFalse(commandStoreDao.checkStatusOrAdd(id, contractId, 1));
    assertEquals(CommandStatus.PENDING, commandStoreDao.getStatus(id));
  }

  @Test
  public void testCommandExistsInFailedStateUsingCheckStatusOrAdd() {
    UUID id = UUID.randomUUID();
    String contractId = "contractId";
    assertFalse(commandStoreDao.checkStatusOrAdd(id, contractId, 1));
    commandStoreDao.setToFailed(id);
    assertFalse(commandStoreDao.checkStatusOrAdd(id, contractId, 1));
    assertEquals(CommandStatus.PENDING, commandStoreDao.getStatus(id));
  }

  @Test
  public void testCommandExistsInSucceededStateUsingCheckStatusOrAdd() {
    UUID id = UUID.randomUUID();
    String contractId = "contractId";
    assertFalse(commandStoreDao.checkStatusOrAdd(id, contractId, 1));
    commandStoreDao.setToSucceeded(id);
    assertTrue(commandStoreDao.checkStatusOrAdd(id, contractId, 1));
    assertEquals(CommandStatus.SUCCEEDED, commandStoreDao.getStatus(id));
  }

  @Test
  public void testCommandExistsInPendingStateUsingCheckStatusOrAdd() {
    UUID id = UUID.randomUUID();
    String contractId = "contractId";
    assertFalse(commandStoreDao.checkStatusOrAdd(id, contractId, 1));
    assertTrue(commandStoreDao.checkStatusOrAdd(id, contractId, 1));
    assertEquals(CommandStatus.PENDING, commandStoreDao.getStatus(id));
  }

  @Test
  public void testSetToSucceeded() {
    UUID id = UUID.randomUUID();
    String contractId = "contractId";
    commandStoreDao.add(id, contractId, 1);
    commandStoreDao.setToSucceeded(id);
    assertEquals(CommandStatus.SUCCEEDED, commandStoreDao.getStatus(id));
  }

  @Test
  public void testSetToFailed() {
    UUID id = UUID.randomUUID();
    String contractId = "contractId";
    commandStoreDao.add(id, contractId, 1);
    commandStoreDao.setToFailed(id);
    assertEquals(CommandStatus.FAILED, commandStoreDao.getStatus(id));
  }

  @Test
  public void testExists() {
    UUID id = UUID.randomUUID();
    String contractId = "contractId";
    assertFalse(commandStoreDao.exists(id));
    commandStoreDao.add(id, contractId, 1);
    assertTrue(commandStoreDao.exists(id));
  }

  @Test
  public void testNoPendingAdd() {
    UUID id1 = UUID.randomUUID();
    String contractId1 = "contractId1";
    commandStoreDao.add(id1, contractId1, 1);

    UUID id2 = UUID.randomUUID();
    String contractId2 = "contractId2";
    commandStoreDao.add(id2, contractId2, 1);

    UUID id3 = UUID.randomUUID();
    String contractId3 = "contractId3";
    commandStoreDao.add(id3, contractId3, 1);

    commandStoreDao.setToSucceeded(id1);
    commandStoreDao.setToFailed(id2);
    commandStoreDao.setToSucceeded(id3);

    String contractId4 = "contractId4";

    assertFalse(commandStoreDao.hasPendingCommandOrOutOfSequence(contractId4, 1));
  }

  @Test
  public void testAnyPendingAdd() {
    UUID id1 = UUID.randomUUID();
    String contractId1 = "contractId1";
    commandStoreDao.add(id1, contractId1, 1);

    UUID id2 = UUID.randomUUID();
    String contractId2 = "contractId2";
    commandStoreDao.add(id2, contractId2, 1);

    UUID id3 = UUID.randomUUID();
    String contractId3 = "contractId3";
    commandStoreDao.add(id3, contractId3, 1);

    commandStoreDao.setToSucceeded(id1);
    commandStoreDao.setToFailed(id2);

    String contractId4 = "contractId4";

    assertTrue(commandStoreDao.hasPendingCommandOrOutOfSequence(contractId4, 1));
  }

  @Test
  public void testOutOfOrder() {
    UUID id1 = UUID.randomUUID();
    String contractId1 = "contractId1";
    commandStoreDao.add(id1, contractId1, 1);

    UUID id2 = UUID.randomUUID();
    String contractId2 = "contractId2";
    commandStoreDao.add(id2, contractId2, 1);

    UUID id3 = UUID.randomUUID();
    String contractId3 = "contractId3";
    commandStoreDao.add(id3, contractId3, 1);

    commandStoreDao.setToSucceeded(id1);
    commandStoreDao.setToFailed(id2);
    commandStoreDao.setToSucceeded(id3);

    UUID id4 = UUID.randomUUID();
    commandStoreDao.add(id4, contractId2, 2);
    commandStoreDao.setToSucceeded(id4);

    UUID id5 = UUID.randomUUID();
    commandStoreDao.add(id5, contractId2, 3);
    commandStoreDao.setToSucceeded(id5);

    assertTrue(commandStoreDao.hasPendingCommandOrOutOfSequence(contractId2, 5));
  }

  @Test
  public void testOutOfOrderAndPending() {
    UUID id1 = UUID.randomUUID();
    String contractId1 = "contractId1";
    commandStoreDao.add(id1, contractId1, 1);

    UUID id2 = UUID.randomUUID();
    String contractId2 = "contractId2";
    commandStoreDao.add(id2, contractId2, 1);

    UUID id3 = UUID.randomUUID();
    String contractId3 = "contractId3";
    commandStoreDao.add(id3, contractId3, 1);

    commandStoreDao.setToSucceeded(id1);
    commandStoreDao.setToFailed(id2);
    commandStoreDao.setToSucceeded(id3);

    UUID id4 = UUID.randomUUID();
    commandStoreDao.add(id4, contractId2, 2);
    commandStoreDao.setToSucceeded(id4);

    UUID id5 = UUID.randomUUID();
    commandStoreDao.add(id5, contractId2, 3);

    assertTrue(commandStoreDao.hasPendingCommandOrOutOfSequence(contractId2, 5));
  }
}
