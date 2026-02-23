package domain;

public class MockEventRecord extends EventRecord {

  public MockEventRecord(String guid, long logicalClockReading, int version) {
    super(guid);
    setLogicalClockReading(logicalClockReading);
    setVersion(version);
    setDirtyFlag(FlagStatus.CLEAN);
  }

  // In standard implementation, this method would read from the database and set the
  // fields to the values read from the database
  @Override
  protected void readFromDB() {
    // Do nothing
  }
}