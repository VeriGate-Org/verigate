package infrastructure.messaging.headers;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Supported encoding types for header fields.
 */
public enum EncodingType {
  AVRO((byte) 0x01),
  JSON((byte) 0x02);

  private static final Map<Byte, EncodingType> byteIndicatorIndex = Arrays.stream(
          EncodingType.values())
      .collect(Collectors.toMap(EncodingType::byteIndicator, Function.identity()));

  private final byte indicator;

  EncodingType(byte indicator) {
    this.indicator = indicator;
  }

  public static Optional<EncodingType> fromByteIndicator(byte indicator) {
    return Optional.ofNullable(byteIndicatorIndex.get(indicator));
  }

  /**
   * Returns the byte flag indicating the message type.
   */
  public byte byteIndicator() {
    return indicator;
  }
}
