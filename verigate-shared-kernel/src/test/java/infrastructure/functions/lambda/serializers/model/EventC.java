package infrastructure.functions.lambda.serializers.model;

import crosscutting.serialization.DataContract;
import java.util.Objects;

public class EventC extends RootEvent {
  @DataContract private final String eventCField;

  public EventC() {
    super(0);
    this.eventCField = null;
  }

  public EventC(int rootField, String eventCField) {
    super(rootField);
    this.eventCField = eventCField;
  }

  public String getEventCField() {
    return eventCField;
  }



  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof EventC eventC)) return false;
    return Objects.equals(eventCField, eventC.eventCField) && Objects.equals(getRootEventField(), eventC.getRootEventField());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(eventCField) + Objects.hashCode(getRootEventField());
  }
}
