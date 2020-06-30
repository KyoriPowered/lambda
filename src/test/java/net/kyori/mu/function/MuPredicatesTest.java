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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MuPredicatesTest {
  @Test
  void testNot() {
    assertFalse(MuPredicates.not(MuPredicates.alwaysTrue()).test("foo"));
    assertTrue(MuPredicates.not(MuPredicates.alwaysFalse()).test("foo"));
  }

  @Test
  void testAlwaysFalse() {
    assertFalse(MuPredicates.alwaysFalse().test(null));
    assertFalse(MuPredicates.alwaysFalse().test("foo"));
  }

  @Test
  void testAlwaysFalse_and() {
    final Predicate<String> predicate = string -> string.contains("meow");
    assertSame(MuPredicates.alwaysFalse(), MuPredicates.<String>alwaysFalse().and(predicate));
  }

  @Test
  void testAlwaysFalse_negate() {
    assertTrue(MuPredicates.alwaysFalse().negate().test(null));
    assertTrue(MuPredicates.alwaysFalse().negate().test("foo"));
  }

  @Test
  void testAlwaysFalse_or() {
    final Predicate<String> predicate = string -> string.contains("meow");
    assertSame(predicate, MuPredicates.<String>alwaysFalse().or(predicate));
  }

  @Test
  void testAlwaysTrue() {
    assertTrue(MuPredicates.alwaysTrue().test(null));
    assertTrue(MuPredicates.alwaysTrue().test("foo"));
  }

  @Test
  void testAlwaysTrue_and() {
    final Predicate<String> predicate = string -> string.contains("meow");
    assertSame(predicate, MuPredicates.<String>alwaysTrue().and(predicate));
  }

  @Test
  void testAlwaysTrue_negate() {
    assertFalse(MuPredicates.alwaysTrue().negate().test(null));
    assertFalse(MuPredicates.alwaysTrue().negate().test("foo"));
  }

  @Test
  void testAlwaysTrue_or() {
    final Predicate<String> predicate = string -> string.contains("meow");
    assertNotSame(predicate, MuPredicates.<String>alwaysTrue().or(predicate));
  }

  @Test
  void testIsNull() {
    assertTrue(MuPredicates.isNull().test(null));
    assertFalse(MuPredicates.isNull().test("foo"));
  }

  @Test
  void testIsNull_negate() {
    assertFalse(MuPredicates.isNull().negate().test(null));
    assertTrue(MuPredicates.isNull().negate().test("foo"));
  }

  @Test
  void testNonNull() {
    assertFalse(MuPredicates.nonNull().test(null));
    assertTrue(MuPredicates.nonNull().test("foo"));
  }

  @Test
  void testNonNull_negate() {
    assertTrue(MuPredicates.nonNull().negate().test(null));
    assertFalse(MuPredicates.nonNull().negate().test("foo"));
  }

  @Test
  void testInstanceOf() {
    assertTrue(MuPredicates.instanceOf(String.class).test("foo"));
    assertFalse(MuPredicates.instanceOf(String.class).test(1));
    assertTrue(MuPredicates.instanceOf(Integer.class).test(1));
  }
}
