/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import javax.money.Monetary;
import org.javamoney.moneta.Money;

/**
 * This class is a custom deserializer for JSON data into {@link Money} instances, extending
 * Jackson's {@link StdDeserializer}. It's designed to convert a JSON representation that includes a
 * monetary amount and currency code back into a {@link Money} object, facilitating the handling of
 * monetary values in financial applications and services that consume JSON data streams.
 */
public final class MoneyDeserializer extends StdDeserializer<Money> {

  /**
   * Constructs a new deserializer instance for {@link Money} objects. This constructor allows for
   * this deserializer to be registered with Jackson's deserialization process, specifically for
   * handling {@link Money} instances.
   */
  public MoneyDeserializer() {
    super(Money.class);
  }

  @Override
  public Money deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {

    JsonNode node = jp.getCodec().readTree(jp);
    var amount = node.get("number").asDouble();
    var currency = node.get("currency").asText();

    return Money.of(amount, Monetary.getCurrency(currency));
  }
}
