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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A collection of utilities for composing objects.
 */
public final class Composer {
  private Composer() {
  }

  /**
   * Gets a {@code T} from {@code supplier}.
   *
   * @param supplier the supplier
   * @param <T> the value type
   * @return the value
   */
  public static <T> /* @Nullable */ T get(final @NonNull Supplier<T> supplier) {
    return supplier.get();
  }

  /**
   * Applies {@code consumer} to {@code value}.
   *
   * @param value the value
   * @param consumer the consumer
   * @param <T> the value type
   * @return the value
   */
  public static <T> /* @Nullable */ T accept(final @NonNull T value, final @NonNull Consumer<T> consumer) {
    consumer.accept(value);
    return value;
  }

  /**
   * Applies {@code function} to {@code value}, and returns the transformed value.
   *
   * @param value the value
   * @param function the function
   * @param <I> the input type
   * @param <O> the output type
   * @return the value
   */
  public static <I, O> /* @Nullable */ O apply(final @NonNull I value, final @NonNull Function<I, O> function) {
    return function.apply(value);
  }

  /**
   * Configures {@code value} using {@code consumer} and then transforms it using {@code function}.
   *
   * @param value the value
   * @param consumer the consumer
   * @param function the function
   * @param <I> the input type
   * @param <O> the output type
   * @return the value
   */
  public static <I, O> /* @Nullable */ O make(final @NonNull I value, final @NonNull Consumer<I> consumer, final @NonNull Function<I, O> function) {
    return function.apply(accept(value, consumer));
  }
}
