/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.javamoney.moneta.Money;

/**
 * This class is a custom serializer for objects of type {@link Money}, extending the Jackson
 * library's {@link StdSerializer}. It is designed to convert {@link Money} instances into a JSON
 * representation that includes the monetary amount and currency code, ensuring compatibility and
 * ease of use in financial applications and services that exchange monetary values.
 */
public final class MoneySerializer extends StdSerializer<Money> {

  /**
   * Constructs a new serializer instance for {@link Money} objects. This constructor allows for
   * this serializer to be registered with Jackson's serialization process, specifically for
   * handling instances of the {@link Money} class.
   *
   */
  public MoneySerializer() {
    super(Money.class);
  }

  /**
   * Serializes a {@link Money} object into JSON format. This method overrides the {@code serialize}
   * method from the superclass {@link StdSerializer}. It writes out the monetary amount as a
   * numeric field and the currency as a string field.
   *
   * @param value The {@link Money} object to serialize.
   * @param gen The {@link JsonGenerator} used to output the JSON content.
   * @param serializers The {@link SerializerProvider} that can be used to get serializers for
   *     serializing the object's properties. Not directly used in this implementation, but required
   *     by the method signature.
   * @throws IOException If any IO errors occur during writing to the {@link JsonGenerator}.
   */
  @Override
  public void serialize(Money value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeStartObject(); // Begins the object serialization process.
    gen.writeNumberField(
        "number", value.getNumber().doubleValue()); // Serializes the monetary amount.
    gen.writeStringField(
        "currency", value.getCurrency().toString()); // Serializes the currency code.
    gen.writeEndObject(); // Ends the object serialization process.
  }
}
