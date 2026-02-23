package domain;

/**
 * Record of an event which is used for idempotency checks. Implementations are
 * required for each services event type.
 *
 * <p>At a minimum a record must contain the GUID of the event, the logical clock
 * reading of the node at the time the event was created, and the current version
 * of the record. The dirty flag is used to indicate if the record has been updated
 * since this information was read from the DB.
 */
public abstract class EventRecord {
  private final String guid;
  private long logicalClockReading;
  private int version;
  private FlagStatus dirtyFlag;

  /**
   * Constructor for an event record.
   *
   * @param guid The GUID of the event.
   */
  public EventRecord(String guid) {
    // read from DB, populate fields
    this.guid = guid;
    this.dirtyFlag = FlagStatus.CLEAN;
    readFromDB();
  }

  /**
   * This method should set the logicalClockReading, version and any other
   * implemented fields to the values read from the DB.
   */
  protected abstract void readFromDB();

  public void setLogicalClockReading(long logicalClockReading) {
    this.logicalClockReading = logicalClockReading;
  }

  public void setDirtyFlag(FlagStatus dirtyFlag) {
    this.dirtyFlag = dirtyFlag;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public int getVersion() {
    return version;
  }

  public long getLogicalClockReading() {
    return logicalClockReading;
  }

  public FlagStatus getDirtyFlag() {
    return dirtyFlag;
  }

  public String getGuid() {
    return guid;
  }
}