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

import com.google.common.truth.ThrowableSubject;
import com.google.common.truth.Truth;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import net.kyori.mu.TestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionsTest {
  @Test
  void testPropagate() {
    final TestException te = new TestException();
    try {
      throw Exceptions.propagate(te);
    } catch(final RuntimeException e) {
      assertSame(te, e.getCause());
    }
  }

  @Test
  void testThrowIfInstanceOf() {
    assertDoesNotThrow(() -> Exceptions.<IllegalArgumentException>throwIfInstanceOf(new IllegalStateException(""), IllegalArgumentException.class));
    assertThrows(IllegalArgumentException.class, () -> Exceptions.throwIfInstanceOf(new IllegalArgumentException(""), IllegalArgumentException.class));
  }

  @Test
  void testThrowIfUnchecked_checked() {
    assertDoesNotThrow(() -> Exceptions.throwIfUnchecked(new Exception("should not be thrown")));
  }

  @Test
  void testThrowIfUnchecked_runtimeException() {
    final RuntimeException exception = new RuntimeException("should be thrown");
    assertSame(exception, assertThrows(RuntimeException.class, () -> Exceptions.throwIfUnchecked(exception)));
  }

  @Test
  void testThrowIfUnchecked_error() {
    final Error error = new Error("should be thrown");
    assertSame(error, assertThrows(Error.class, () -> Exceptions.throwIfUnchecked(error)));
  }

  @Test
  void testUnwrap() {
    final TestException te = new TestException();
    assertSame(te, Exceptions.unwrap(new ExecutionException(te)));
    final ThrowableSubject ts = Truth.assertThat(Exceptions.unwrap(new IllegalArgumentException(te)));
    ts.isInstanceOf(IllegalArgumentException.class);
    ts.hasCauseThat().isSameAs(te);
    assertSame(te, Exceptions.unwrap(new InvocationTargetException(te)));
  }
}
