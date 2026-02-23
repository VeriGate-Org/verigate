/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.mapping;

import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * A pass-through mapper implementation that simply casts between model objects and transport DTOs.
 */
public final class PassthroughMapper implements Mapper {

  /**
   * Converts a transport DTO to a model object by casting.
   *
   * @param dto The transport DTO to convert. Must not be null.
   * @return The converted model object. Will not be null.
   * @throws NullPointerException if dto is null.
   * @throws ClassCastException if the dto cannot be cast to ModelT.
   */
  @SuppressWarnings("unchecked")
  @Override
  public <ModelT, TransportDtoT> @NotNull ModelT toModel(@NotNull TransportDtoT dto) {
    Objects.requireNonNull(dto, "The transport DTO must not be null");
    return (ModelT) dto;
  }

  /**
   * Converts a model object to a transport DTO by casting.
   *
   * @param model The model object to convert. Must not be null.
   * @return The converted transport DTO. Will not be null.
   * @throws NullPointerException if model is null.
   * @throws ClassCastException if the model cannot be cast to TransportDtoT.
   */
  @SuppressWarnings("unchecked")
  @Override
  public <TransportDtoT, ModelT> @NotNull TransportDtoT toDto(@NotNull ModelT model) {
    Objects.requireNonNull(model, "The model object must not be null");
    return (TransportDtoT) model;
  }
}
