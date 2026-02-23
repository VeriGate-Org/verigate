/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;

/**
 * A supplier which a) decorates a {@link ThrowingSupplier} by adding logging for any exceptions,
 * and b) transforms any exception into a {@link RuntimeException} to allow this supplier to be
 * used as a native {@link Supplier}.
 *
 * @param <T> The supplied type.
 */
public class ExceptionLoggingSupplier<T> implements Supplier<T> {

  private final Logger logger;
  private final Function<Exception, ? extends RuntimeException> exceptionMapper;
  private final ThrowingSupplier<T> decoratedSupplier;

  /**
   * Default constructor.
   *
   * @param logger The logger to use for exception log messages.
   * @param decoratedSupplier The decorated supplier.
   * @param exceptionMapper The mapper to convert any exception from the decorated supplier into a
   *                        RuntimeException.
   */
  public ExceptionLoggingSupplier(Logger logger,
      ThrowingSupplier<T> decoratedSupplier,
      Function<Exception, ? extends RuntimeException> exceptionMapper) {
    this.logger = Objects.requireNonNull(logger);
    this.exceptionMapper = Objects.requireNonNull(exceptionMapper);
    this.decoratedSupplier = Objects.requireNonNull(decoratedSupplier);
  }

  @Override
  public T get() {
    try {
      return decoratedSupplier.get();
    } catch (Exception e) {
      final RuntimeException runtimeException = exceptionMapper.apply(e);
      logger.error(runtimeException.getMessage(), e);
      throw runtimeException;
    }
  }

}
