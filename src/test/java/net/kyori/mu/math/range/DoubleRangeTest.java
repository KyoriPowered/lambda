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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoubleRangeTest {
  @Test
  void testAbove() {
    final DoubleRange.Above r0 = DoubleRange.above(2d);
    assertEquals(2d, r0.value());
    Testing.assertNoneMatchd(r0, 0d, 1d, 2d);
    Testing.assetAllMatchd(r0, 3d, 4d);
  }

  @Test
  void testAtLeast() {
    final DoubleRange.AtLeast r0 = DoubleRange.atLeast(2d);
    assertEquals(2d, r0.value());
    Testing.assertNoneMatchd(r0, 0d, 1d);
    Testing.assetAllMatchd(r0, 2d, 3d);
  }

  @Test
  void testBetween() {
    final DoubleRange.Between r0 = DoubleRange.between(2.1d, 4.1d);
    assertEquals(2.1d, r0.min());
    assertEquals(4.1d, r0.max());
    Testing.assertNoneMatchd(r0, 1d, 2d, 4.2d, 5d);
    Testing.assetAllMatchd(r0, 2.2d, 3d, 4.1d);
  }

  @Test
  void testBetweenRandom() {
    final DoubleRange.Between r0 = DoubleRange.between(1.2d, 9.3d);
    final Random random = ThreadLocalRandom.current();
    for(int i = 0; i < 100; i++) {
      assertTrue(MuMath.between(r0.randomDouble(random), 1.2d, 9.3d));
    }
  }

  @Test
  void testConstant() {
    final DoubleRange.Constant r0 = DoubleRange.constant(2d);
    assertEquals(2d, r0.value());
    Testing.assetAllMatchd(r0, 2d);
    Testing.assertNoneMatchd(r0, 1d, 3d);
  }

  @Test
  void testConstantRandom() {
    final DoubleRange.Constant r0 = DoubleRange.constant(2d);
    final Random random = ThreadLocalRandom.current();
    for(int i = 0; i < 100; i++) {
      assertEquals(2d, r0.randomDouble(random));
    }
  }

  @Test
  void testAtMost() {
    final DoubleRange.AtMost r0 = DoubleRange.atMost(2d);
    assertEquals(2d, r0.value());
    Testing.assetAllMatchd(r0, 0d, 1d, 2d);
    Testing.assertNoneMatchd(r0, 3d);
  }

  @Test
  void testBelow() {
    final DoubleRange.Below r0 = DoubleRange.below(2d);
    assertEquals(2d, r0.value());
    Testing.assetAllMatchd(r0, 0d, 1d);
    Testing.assertNoneMatchd(r0, 2d, 3d);
  }

  @Test
  void testEquality() {
    new EqualsTester()
      .addEqualityGroup(
        DoubleRange.above(1d),
        DoubleRange.above(1d)
      )
      .addEqualityGroup(
        DoubleRange.atLeast(1d),
        DoubleRange.atLeast(1d)
      )
      .addEqualityGroup(
        DoubleRange.between(0d, 10d),
        DoubleRange.between(0d, 10d)
      )
      .addEqualityGroup(
        DoubleRange.constant(6d),
        DoubleRange.constant(6d)
      )
      .addEqualityGroup(
        DoubleRange.atMost(1d),
        DoubleRange.atMost(1d)
      )
      .addEqualityGroup(
        DoubleRange.below(1d),
        DoubleRange.below(1d)
      )
      .testEquals();
  }
}
