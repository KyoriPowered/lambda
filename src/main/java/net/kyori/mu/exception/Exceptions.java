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
package net.kyori.mu.exception;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A collection of methods for working with exceptions.
 */
public final class Exceptions {
  private Exceptions() {
  }

  /**
   * Re-throws an exception, sneakily.
   *
   * @param exception the exception
   * @param <E> the exception type
   * @return nothing
   * @throws E the exception
   */
  @SuppressWarnings("unchecked")
  public static <E extends Throwable> @NonNull RuntimeException rethrow(final @NonNull Throwable exception) throws E {
    throw (E) exception;
  }

  /**
   * Propagates {@code throwable} as-is if it is an instance of {@link RuntimeException} or
   * {@link Error}, otherwise wraps it in a {@code RuntimeException} and then
   * propagates.
   *
   * @param throwable the throwable
   * @return nothing
   */
  public static RuntimeException propagate(final @NonNull Throwable throwable) {
    throwIfUnchecked(throwable);
    throw new RuntimeException(throwable);
  }

  /**
   * Throws {@code throwable} if it is an instance of {@code type}.
   *
   * @param throwable the throwable
   * @param type the exception type
   * @param <E> the exception type
   * @throws E the exception
   */
  public static <E extends Throwable> void throwIfInstanceOf(final @NonNull Throwable throwable, final @NonNull Class<E> type) throws E {
    if(type.isInstance(throwable)) {
      throw type.cast(throwable);
    }
  }

  /**
   * Throws {@code throwable} if it is a {@link RuntimeException} or {@link Error}.
   *
   * @param throwable the throwable
   */
  public static void throwIfUnchecked(final @NonNull Throwable throwable) {
    if(throwable instanceof RuntimeException) {
      throw (RuntimeException) throwable;
    }
    if(throwable instanceof Error) {
      throw (Error) throwable;
    }
  }

  /**
   * Unwraps a throwable.
   *
   * @param throwable the throwable
   * @return the unwrapped throwable, or the original throwable
   */
  public static @NonNull Throwable unwrap(final @NonNull Throwable throwable) {
    if(throwable instanceof ExecutionException || throwable instanceof InvocationTargetException) {
      final /* @Nullable */ Throwable cause = throwable.getCause();
      if(cause != null) {
        return cause;
      }
    }
    return throwable;
  }
}
