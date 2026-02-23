/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.mapping;

/** Factory class for creating instances of {@link Mapper}. This class cannot be instantiated. */
public final class MapperFactory {

  private MapperFactory() {}

  /**
   * Creates a new instance of {@link Mapper}.
   *
   * @param <T> The type parameter for the Mapper.
   * @return A new instance of {@link Mapper}. Will not be null.
   */
  public <T> Mapper create() {
    return new PassthroughMapper();
  }
}
