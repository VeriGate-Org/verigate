package infrastructure.functions.lambda.serializers.model;

import crosscutting.serialization.DataContract;
import java.util.Objects;

public class EventB extends RootEvent {
  @DataContract private final String eventBField;

  public EventB() {
    super(0);
    this.eventBField = null;
  }

  public EventB(int rootField, String eventBField) {
    super(rootField);
    this.eventBField = eventBField;
  }

  public String getEventBField() {
    return eventBField;
  }


  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof EventB eventB)) return false;
    return Objects.equals(eventBField, eventB.eventBField) && Objects.equals(getRootEventField(), eventB.getRootEventField());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(eventBField) + Objects.hashCode(getRootEventField());
  }
}
