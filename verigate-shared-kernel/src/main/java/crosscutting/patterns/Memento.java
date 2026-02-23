/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

/**
 * The Memento class is designed to encapsulate and preserve the state of an object at a given
 * moment in a manner that's externalizable without exposing the internal details of the
 * encapsulated state. It supports multiple data types including text, binary streams, and
 * structured property bags.
 */
public final class Memento {

  // The encapsulated data, represented generically to support multiple data types.
  private final Data data;

  /**
   * Constructs a Memento object encapsulating a generic data object.
   *
   * @param data The data to encapsulate within this Memento.
   */
  public Memento(Data data) {
    this.data = data;
  }

  /**
   * Constructs a Memento object to encapsulate a text string.
   *
   * @param text The text string to encapsulate.
   */
  public Memento(String text) {
    this.data = new StringData(text);
  }

  /**
   * Constructs a Memento object to encapsulate a structured property bag.
   *
   * @param propertyBag The ImmutablePropertyBag to encapsulate.
   */
  public Memento(ImmutablePropertyBag propertyBag) {
    this.data = new PropertyData(propertyBag);
  }

  /**
   * Constructs a Memento object to encapsulate a binary data stream.
   *
   * @param stream The InputStream representing the binary data to encapsulate.
   */
  public Memento(InputStream stream) {
    this.data = new StreamData(stream);
  }

  /**
   * Attempts to retrieve the encapsulated data as a structured property bag.
   *
   * @return An Optional containing the ImmutablePropertyBag if the encapsulated data is of that
   *     type, otherwise empty.
   */
  public Optional<ImmutablePropertyBag> asPropertyBag() {
    if (data instanceof PropertyData) {
      return Optional.of(((PropertyData) data).bag());
    }
    return Optional.empty();
  }

  /**
   * Attempts to retrieve the encapsulated data as a map.
   *
   * @return An Optional containing the map representation of the encapsulated data if it is of type
   *     PropertyData, otherwise empty.
   */
  public Optional<Map<String, Object>> asMap() {
    if (data instanceof PropertyData) {
      return Optional.of(((PropertyData) data).bag().asMap());
    }
    return Optional.empty();
  }

  /**
   * Attempts to retrieve the encapsulated data as a string.
   *
   * @return An Optional containing the string if the encapsulated data is of type StringData,
   *     otherwise empty.
   */
  public Optional<String> asString() {
    if (data instanceof StringData) {
      return Optional.of(((StringData) data).text());
    }
    return Optional.empty();
  }

  /**
   * Attempts to retrieve the encapsulated data as an InputStream.
   *
   * @return An Optional containing the InputStream if the encapsulated data is of type StreamData,
   *     otherwise empty.
   */
  public Optional<InputStream> asStream() {
    if (data instanceof StreamData) {
      return Optional.of(((StreamData) data).stream());
    }
    return Optional.empty();
  }

  /**
   * Attempts to convert the encapsulated stream data into a byte array.
   *
   * @return An Optional containing the byte array if the encapsulated data is of type StreamData
   *     and can be read, otherwise empty.
   */
  public Optional<byte[]> asByteArray(int size) {
    if (data instanceof StreamData) {
      try (InputStream inputStream = ((StreamData) data).stream()) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] dataBuffer = new byte[size]; // Define a buffer size for reading
        int bytesRead;
        while ((bytesRead = inputStream.read(dataBuffer, 0, dataBuffer.length)) != -1) {
          buffer.write(dataBuffer, 0, bytesRead);
        }
        buffer.flush();
        return Optional.of(buffer.toByteArray());
      } catch (IOException e) {
        // IOException handling logic here
        return Optional.empty();
      }
    }
    return Optional.empty();
  }

  /** The Data interface represents a generic type of data that can be encapsulated in a Memento. */
  public interface Data {}

  /** A record that encapsulates an ImmutablePropertyBag as Data. */
  public record PropertyData(ImmutablePropertyBag bag) implements Data {}

  /** A record that encapsulates a String as Data. */
  public record StringData(String text) implements Data {}

  /** A record that encapsulates an InputStream as Data. */
  public record StreamData(InputStream stream) implements Data {}
}
