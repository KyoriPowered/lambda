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

import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionalsTest {
  @Test
  void testObjectCast() {
    assertFalse(Optionals.cast(new C(), A.class).isPresent());
    assertTrue(Optionals.cast(new B(), A.class).isPresent());
  }

  @Test
  void testOptionalCast() {
    assertFalse(Optionals.cast(Optional.of(new C()), A.class).isPresent());
    assertFalse(Optionals.cast(Optional.empty(), A.class).isPresent());
    assertTrue(Optionals.cast(Optional.of(new B()), A.class).isPresent());
  }

  @Test
  void testFirst() {
    final Optional<String> expected = Optional.of("meow");
    assertEquals(expected, Optionals.first(
      expected,
      Optional.empty(),
      Optional.empty()
    ));
    assertEquals(expected, Optionals.first(
      Optional.empty(),
      expected,
      Optional.empty()
    ));
    assertEquals(expected, Optionals.first(
      Optional.empty(),
      Optional.empty(),
      expected
    ));
  }

  @Test
  void testIsInstance() {
    assertFalse(Optionals.isInstance(Optional.empty(), A.class));
    assertTrue(Optionals.isInstance(Optional.of(new B()), A.class));
    assertFalse(Optionals.isInstance(Optional.of(new C()), A.class));
  }

  @Test
  void testOr() {
    final Optional<String> empty = Optional.empty();
    final Optional<String> wanted = Optional.of("meow");
    final Optional<String> unwanted = Optional.of("hiss");
    assertEquals(wanted, Optionals.or(empty, () -> wanted));
    assertEquals(wanted, Optionals.or(wanted, () -> unwanted));
  }

  private interface A {}
  private static class B implements A {}
  private static class C {}
}
