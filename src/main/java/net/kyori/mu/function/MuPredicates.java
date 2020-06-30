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

import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;

/*
 * Name is prefixed with 'Mu' to avoid conflict with com.google.common.base.Predicates
 */

/**
 * A collection of utilities for working with {@link Predicate}.
 */
public final class MuPredicates {
  private MuPredicates() {
  }

  /**
   * Negates the given predicate.
   *
   * @param predicate the predicate to negate
   * @param <T> the type
   * @return the negated predicate
   */
  public static <T> @NonNull Predicate<T> not(final @NonNull Predicate<T> predicate) {
    return predicate.negate();
  }

  /**
   * Returns a predicate that always returns {@code false}.
   *
   * @param <T> the type
   * @return a predicate
   */
  @SuppressWarnings("unchecked")
  public static <T> @NonNull Predicate<T> alwaysFalse() {
    return (Predicate<T>) Impl.ALWAYS_FALSE;
  }

  /**
   * Returns a predicate that always returns {@code true}.
   *
   * @param <T> the type
   * @return a predicate
   */
  @SuppressWarnings("unchecked")
  public static <T> @NonNull Predicate<T> alwaysTrue() {
    return (Predicate<T>) Impl.ALWAYS_TRUE;
  }

  /**
   * Returns a predicate that always returns {@code true} for {@code null} inputs.
   *
   * @param <T> the type
   * @return a predicate
   */
  @SuppressWarnings("unchecked")
  public static <T> @NonNull Predicate<T> isNull() {
    return (Predicate<T>) Impl.IS_NULL;
  }

  /**
   * Returns a predicate that always returns {@code true} for non-{@code null} inputs.
   *
   * @param <T> the type
   * @return a predicate
   */
  @SuppressWarnings("unchecked")
  public static <T> @NonNull Predicate<T> nonNull() {
    return (Predicate<T>) Impl.NON_NULL;
  }

  /**
   * Creates a predicate that checks if the input is an instance of {@code type}.
   *
   * @param type the type
   * @param <T> the returned predicate type
   * @param <U> the type to check instance of
   * @return a predicate
   */
  public static <T, U> @NonNull Predicate<T> instanceOf(final @NonNull Class<U> type) {
    return type::isInstance;
  }

  private enum Impl implements Predicate<Object> {
    ALWAYS_FALSE {
      @Override public boolean test(final Object object) { return false; }
      @Override public @NonNull Predicate<Object> and(final @NonNull Predicate<? super Object> other) { return this; }
      @Override public @NonNull Predicate<Object> negate() { return ALWAYS_TRUE; }
      @Override public @NonNull Predicate<Object> or(final @NonNull Predicate<? super Object> other) { return other; }
    },
    ALWAYS_TRUE {
      @Override public boolean test(final Object object) { return true; }
      @Override public @NonNull Predicate<Object> and(final @NonNull Predicate<? super Object> other) { return other; }
      @Override public @NonNull Predicate<Object> negate() { return ALWAYS_FALSE; }
      @Override public @NonNull Predicate<Object> or(final @NonNull Predicate<? super Object> other) { return this; }
    },
    IS_NULL {
      @Override public boolean test(final Object object) { return object == null; }
      @Override public @NonNull Predicate<Object> negate() { return NON_NULL; }
    },
    NON_NULL {
      @Override public boolean test(final Object object) { return object != null; }
      @Override public @NonNull Predicate<Object> negate() { return IS_NULL; }
    };
  }
}
