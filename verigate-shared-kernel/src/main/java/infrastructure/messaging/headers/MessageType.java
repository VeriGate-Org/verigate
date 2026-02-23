/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.messaging.headers;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Identifies the message type (e.g., Command, Event).
 * Informational.
 */
public enum MessageType {

  UNDEFINED((byte) 0x00),
  COMMAND((byte) 0x01),
  EVENT((byte) 0x02);

  private static final Map<Byte, MessageType> byteIndicatorIndex = Arrays.stream(
          MessageType.values())
      .collect(Collectors.toMap(MessageType::byteIndicator, Function.identity()));

  private final byte indicator;

  MessageType(byte indicator) {
    this.indicator = indicator;
  }

  public static Optional<MessageType> fromByteIndicator(byte indicator) {
    return Optional.ofNullable(byteIndicatorIndex.get(indicator));
  }

  /**
   * Returns the byte flag indicating the message type.
   */
  public byte byteIndicator() {
    return indicator;
  }

}
