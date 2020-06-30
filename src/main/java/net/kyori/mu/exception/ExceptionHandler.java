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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An exception handler.
 */
@FunctionalInterface
public interface ExceptionHandler {
  /**
   * Gets an exception handler that does nothing.
   *
   * <p>It is <b>highly</b> recommended that you <b>not</b> use the NOOP handler, and to actually do something with exceptions.</p>
   *
   * @return an exception handler that does nothing
   */
  static @NonNull ExceptionHandler noop() {
    return NopeExceptionHandler.INSTANCE;
  }

  /**
   * Handles an exception.
   *
   * @param throwable the exception
   */
  default void handleException(final @NonNull Throwable throwable) {
    this.handleException(throwable, null);
  }

  /**
   * Handles an exception.
   *
   * @param throwable the exception
   * @param source the source
   */
  void handleException(final @NonNull Throwable throwable, final @Nullable Object source);
}
