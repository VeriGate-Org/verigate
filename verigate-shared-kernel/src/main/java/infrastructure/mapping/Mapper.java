/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.mapping;

import javax.validation.constraints.NotNull;

/** A generic mapper interface for converting between model objects and transport DTOs. */
public interface Mapper {

  /**
   * Converts a transport DTO to a model object.
   *
   * @param dto The transport DTO to convert. Must not be null.
   * @return The converted model object. Will not be null.
   * @param <ModelT> The type of the model object.
   * @param <TransportDtoT> The type of the transport DTO.
   */
  <ModelT, TransportDtoT> @NotNull ModelT toModel(@NotNull TransportDtoT dto);

  /**
   * Converts a model object to a transport DTO.
   *
   * @param model The model object to convert. Must not be null.
   * @return The converted transport DTO. Will not be null.
   * @param <TransportDtoT> The type of the transport DTO.
   * @param <ModelT> The type of the model object.
   */
  <TransportDtoT, ModelT> @NotNull TransportDtoT toDto(@NotNull ModelT model);
}
