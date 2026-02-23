package infrastructure.messaging.headers.v1;

import domain.exceptions.DeserializeException;
import infrastructure.messaging.headers.CompressionType;
import infrastructure.messaging.headers.EncodingType;
import infrastructure.messaging.headers.Header;
import infrastructure.messaging.headers.HeaderFactory;
import infrastructure.messaging.headers.MessageType;
import java.util.UUID;

/**
 * A factory to create V1 binary headers.
 * See {@link BinaryMessageHeader} for detail.
 */
public class BinaryHeaderV1Factory implements HeaderFactory {

  @Override
  public Header newHeader(MessageType messageType, UUID schemaId, EncodingType encodingType,
      CompressionType compressionType, UUID sourceId, int payloadSize) {
    return new BinaryMessageHeader(
        messageType,
        schemaId,
        encodingType,
        compressionType,
        sourceId,
        payloadSize
    );
  }

  public Header parseHeader(byte[] headerBytes) throws DeserializeException {
    return BinaryMessageHeader.fromByteArray(headerBytes);
  }

}
