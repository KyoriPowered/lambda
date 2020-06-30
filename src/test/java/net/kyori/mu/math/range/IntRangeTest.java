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
package net.kyori.mu.math.range;

import com.google.common.testing.EqualsTester;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.mu.math.MuMath;
import net.kyori.test.Testing;
import org.junit.jupiter.api.Test;

import static net.kyori.test.Testing.assertNoneMatchi;
import static net.kyori.test.Testing.assetAllMatchi;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntRangeTest {
  @Test
  void testAbove() {
    final IntRange.Above r0 = IntRange.above(2);
    assertEquals(2, r0.value());
    assertNoneMatchi(r0, 0, 1, 2);
    assetAllMatchi(r0, 3, 4);
  }

  @Test
  void testAtLeast() {
    final IntRange.AtLeast r0 = IntRange.atLeast(2);
    assertEquals(2, r0.value());
    assertNoneMatchi(r0, 0, 1);
    assetAllMatchi(r0, 2, 3);
  }

  @Test
  void testBetween() {
    final IntRange.Between r0 = IntRange.between(2, 4);
    assertEquals(2, r0.min());
    assertEquals(4, r0.max());
    assertNoneMatchi(r0, 1, 5);
    assetAllMatchi(r0, 2, 3, 4);
  }

  @Test
  void testBetweenRandom() {
    final IntRange.Between r0 = IntRange.between(1, 9);
    final Random random = ThreadLocalRandom.current();
    for(int i = 0; i < 100; i++) {
      assertTrue(MuMath.between(r0.randomInt(random), 1, 9));
    }
  }

  @Test
  void testConstant() {
    final IntRange.Constant r0 = IntRange.constant(2);
    assertEquals(2, r0.value());
    Testing.assetAllMatchi(r0, 2);
    Testing.assertNoneMatchi(r0, 1, 3);
  }

  @Test
  void testConstantRandom() {
    final IntRange.Constant r0 = IntRange.constant(2);
    final Random random = ThreadLocalRandom.current();
    for(int i = 0; i < 100; i++) {
      assertEquals(2, r0.randomInt(random));
    }
  }

  @Test
  void testAtMost() {
    final IntRange.AtMost r0 = IntRange.atMost(2);
    assertEquals(2, r0.value());
    assetAllMatchi(r0, 0, 1, 2);
    assertNoneMatchi(r0, 3);
  }

  @Test
  void testBelow() {
    final IntRange.Below r0 = IntRange.below(2);
    assertEquals(2, r0.value());
    assetAllMatchi(r0, 0, 1);
    assertNoneMatchi(r0, 2, 3);
  }

  @Test
  void testEquality() {
    new EqualsTester()
      .addEqualityGroup(
        IntRange.above(1),
        IntRange.above(1)
      )
      .addEqualityGroup(
        IntRange.atLeast(1),
        IntRange.atLeast(1)
      )
      .addEqualityGroup(
        IntRange.between(0, 10),
        IntRange.between(0, 10)
      )
      .addEqualityGroup(
        IntRange.constant(6),
        IntRange.constant(6)
      )
      .addEqualityGroup(
        IntRange.atMost(1),
        IntRange.atMost(1)
      )
      .addEqualityGroup(
        IntRange.below(1),
        IntRange.below(1)
      )
      .testEquals();
  }
}
