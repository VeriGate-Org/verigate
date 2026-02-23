package infrastructure.functions.lambda.serializers.model;

import crosscutting.serialization.DataContract;

public class RootEvent  {
  @DataContract private final int rootEventField;

  public RootEvent(int rootEventField) {
    this.rootEventField = rootEventField;
  }

  public int getRootEventField() {
    return rootEventField;
  }
}
