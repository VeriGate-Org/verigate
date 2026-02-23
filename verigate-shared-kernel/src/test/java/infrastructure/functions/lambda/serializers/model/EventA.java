package infrastructure.functions.lambda.serializers.model;

import crosscutting.serialization.DataContract;
import java.util.Objects;

public class EventA extends RootEvent {
  @DataContract private final String eventAField;

  public EventA() {
    super(0);
    eventAField = null;
  }

  public EventA(int rootField, String eventAField) {
    super(rootField);
    this.eventAField = eventAField;
  }

  public String getEventAField() {
    return eventAField;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof EventA eventA)) return false;
    return Objects.equals(eventAField, eventA.eventAField) && Objects.equals(getRootEventField(), eventA.getRootEventField());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(eventAField) + Objects.hashCode(getRootEventField());
  }

  @Override
  public String toString() {
    return "EventA{" + "eventAField='" + eventAField + '\'' + ", rootEventField='" + getRootEventField() + '\'' + '}';
  }
}