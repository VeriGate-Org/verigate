/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

/**
 * A supplier which can throw checked exceptions.
 *
 * @param <T> The supplied result type.
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {
  T get() throws Exception;
}
