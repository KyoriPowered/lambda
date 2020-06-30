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

import java.util.function.UnaryOperator;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A {@link UnaryOperator} that allows for throwing checked exceptions.
 *
 * @param <T> the input type
 * @param <E> the exception type
 */
@FunctionalInterface
public interface ThrowingUnaryOperator<T, E extends Throwable> extends ThrowingFunction<T, T, E>, UnaryOperator<T> {
  /**
   * Returns the same throwing unary operator.
   *
   * @param operator the unary operator
   * @param <T> the input type
   * @param <E> the exception type
   * @return the unary operator
   */
  static <T, E extends Throwable> @NonNull ThrowingUnaryOperator<T, E> of(final @NonNull ThrowingUnaryOperator<T, E> operator) {
    return operator;
  }
}
