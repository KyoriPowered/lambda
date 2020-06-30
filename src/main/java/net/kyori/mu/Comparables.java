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
package net.kyori.mu;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A collection of utilities for working with comparables.
 */
public final class Comparables {
  private Comparables() {
  }

  /**
   * Checks if {@code left} is less than {@code right}.
   *
   * @param left the left object
   * @param right the right object
   * @param <C> the object type
   * @return {@code true} if {@code left} is less than {@code right}, {@code false} otherwise
   */
  public static <C extends Comparable<C>> boolean lessThan(final @NonNull C left, final @NonNull C right) {
    return left.compareTo(right) < 0;
  }

  /**
   * Checks if {@code left} is less than or equal to {@code right}.
   *
   * @param left the left object
   * @param right the right object
   * @param <C> the object type
   * @return {@code true} if {@code left} is less than or equal to {@code right}, {@code false} otherwise
   */
  public static <C extends Comparable<C>> boolean lessThanOrEqual(final @NonNull C left, final @NonNull C right) {
    return left.compareTo(right) <= 0;
  }

  /**
   * Checks if {@code left} is greater than {@code right}.
   *
   * @param left the left object
   * @param right the right object
   * @param <C> the object type
   * @return {@code true} if {@code left} is greater than {@code right}, {@code false} otherwise
   */
  public static <C extends Comparable<C>> boolean greaterThan(final @NonNull C left, final @NonNull C right) {
    return left.compareTo(right) > 0;
  }

  /**
   * Checks if {@code left} is greater than or equal to {@code right}.
   *
   * @param left the left object
   * @param right the right object
   * @param <C> the object type
   * @return {@code true} if {@code left} is greater than or equal to {@code right}, {@code false} otherwise
   */
  public static <C extends Comparable<C>> boolean greaterThanOrEqual(final @NonNull C left, final @NonNull C right) {
    return left.compareTo(right) >= 0;
  }

  /**
   * Gets the minimum value.
   *
   * @param left the left object
   * @param right the right object
   * @param <C> the object type
   * @return the minimum value
   */
  public static <C extends Comparable<C>> @NonNull C min(final @NonNull C left, final @NonNull C right) {
    return lessThanOrEqual(left, right) ? left : right;
  }

  /**
   * Gets the maximum value.
   *
   * @param left the left object
   * @param right the right object
   * @param <C> the object type
   * @return the maximum value
   */
  public static <C extends Comparable<C>> @NonNull C max(final @NonNull C left, final @NonNull C right) {
    return greaterThanOrEqual(left, right) ? left : right;
  }
}
