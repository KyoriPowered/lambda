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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComparablesTest {
  @Test
  void testLessThan() {
    Assertions.assertTrue(Comparables.lessThan(1, 2));
    assertFalse(Comparables.lessThan(2, 2));
    assertFalse(Comparables.lessThan(3, 2));
  }

  @Test
  void testLessThanOrEqual() {
    assertTrue(Comparables.lessThanOrEqual(0, 1));
    assertTrue(Comparables.lessThanOrEqual(1, 1));
    assertFalse(Comparables.lessThanOrEqual(2, 1));
  }

  @Test
  void testGreaterThan() {
    assertFalse(Comparables.greaterThan(1, 2));
    assertFalse(Comparables.greaterThan(2, 2));
    assertTrue(Comparables.greaterThan(3, 2));
  }

  @Test
  void testGreaterThanOrEqual() {
    assertFalse(Comparables.greaterThanOrEqual(1, 2));
    assertTrue(Comparables.greaterThanOrEqual(2, 2));
    assertTrue(Comparables.greaterThanOrEqual(3, 2));
  }

  @Test
  void testMin() {
    assertEquals(5, (int) Comparables.min(5, 10));
    assertEquals(10, (int) Comparables.min(15, 10));
  }

  @Test
  void testMax() {
    assertEquals(10, (int) Comparables.max(5, 10));
    assertEquals(15, (int) Comparables.max(15, 10));
  }
}
