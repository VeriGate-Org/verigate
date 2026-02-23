package infrastructure.messaging.headers;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Supported compression types for header fields.
 */
public enum CompressionType {
  NONE((byte) 0x00),
  GZIP((byte) 0x01);

  private final byte indicator;

  private static final Map<Byte, CompressionType> byteIndicatorIndex = Arrays.stream(
          CompressionType.values())
      .collect(Collectors.toMap(CompressionType::byteIndicator, Function.identity()));

  CompressionType(byte indicator) {
    this.indicator = indicator;
  }

  public static Optional<CompressionType> fromByteIndicator(byte indicator) {
    return Optional.ofNullable(byteIndicatorIndex.get(indicator));
  }

  /**
   * Returns the byte flag indicating the message type.
   */
  public byte byteIndicator() {
    return indicator;
  }

}
