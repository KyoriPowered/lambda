/*
 * This file is part of mu, licensed under the MIT License.
 *
 * Copyright (c) 2018-2020 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.mu.function;

import java.util.function.Function;
import net.kyori.mu.exception.Exceptions;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A {@link Function} that allows for throwing checked exceptions.
 *
 * @param <T> the input type
 * @param <R> the output type
 * @param <E> the exception type
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> extends Function<T, R> {
  /**
   * Returns the same throwing function.
   *
   * @param function the function
   * @param <T> the input type
   * @param <R> the output type
   * @param <E> the exception type
   * @return the function
   */
  static <T, R, E extends Throwable> @NonNull ThrowingFunction<T, R, E> of(final @NonNull ThrowingFunction<T, R, E> function) {
    return function;
  }

  /**
   * Gets the result of applying this function to {@code input}.
   *
   * @param input the input
   * @return the output
   * @throws E potential exception
   */
  R throwingApply(final T input) throws E;

  /**
   * Gets the result of applying this function to {@code input}.
   *
   * @param input the input
   * @return the output
   */
  @Override
  default R apply(final T input) {
    try {
      return this.throwingApply(input);
    } catch(final Throwable t) {
      throw Exceptions.rethrow(t);
    }
  }
}
