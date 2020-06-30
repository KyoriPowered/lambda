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
package net.kyori.mu.math;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MuMathTest {
  @Test
  void testClamp_double() {
    assertEquals(3d, MuMath.clamp(3d, 0d, 5d));
    assertEquals(-3.1d, MuMath.clamp(-7d, -3.1d, 5d));
    assertEquals(5d, MuMath.clamp(7d, -3.1d, 5d));
    assertEquals(0.1d, MuMath.clamp(-10d, 0.1d, 100d));
    assertEquals(100d, MuMath.clamp(101d, 0.1d, 100d));
  }

  @Test
  void testClamp_float() {
    assertEquals(3f, MuMath.clamp(3f, 0f, 5f));
    assertEquals(-3.1f, MuMath.clamp(-7f, -3.1f, 5f));
    assertEquals(5f, MuMath.clamp(7f, -3.1f, 5f));
    assertEquals(0.1f, MuMath.clamp(-10f, 0.1f, 100f));
    assertEquals(100f, MuMath.clamp(101f, 0.1f, 100f));
  }

  @Test
  void testClamp_int() {
    assertEquals(3, MuMath.clamp(3, 0, 5));
    assertEquals(-3, MuMath.clamp(-7, -3, 5));
    assertEquals(5, MuMath.clamp(7, -3, 5));
    assertEquals(0, MuMath.clamp(-10, 0, 100));
    assertEquals(100, MuMath.clamp(101, 0, 100));
  }

  @Test
  void testClamp_long() {
    assertEquals(3L, MuMath.clamp(3L, 0L, 5L));
    assertEquals(-3L, MuMath.clamp(-7L, -3L, 5L));
    assertEquals(5L, MuMath.clamp(7L, -3L, 5L));
    assertEquals(0L, MuMath.clamp(-10L, 0L, 100L));
    assertEquals(100L, MuMath.clamp(101L, 0L, 100L));
  }

  @Test
  void testBetween_double() {
    assertFalse(MuMath.between(0d, 0.5d, 3.5d));
    assertFalse(MuMath.between(0d, 1d, 3d));
    assertTrue(MuMath.between(0.5d, 0.5d, 3.5d));
    assertTrue(MuMath.between(1d, 1d, 3d));
    assertTrue(MuMath.between(2d, 1d, 3d));
    assertTrue(MuMath.between(3d, 1d, 3d));
    assertTrue(MuMath.between(3.5d, 0.5d, 3.5d));
    assertFalse(MuMath.between(4d, 1d, 3d));
  }

  @Test
  void testBetween_float() {
    assertFalse(MuMath.between(0f, 0.5f, 3.5f));
    assertFalse(MuMath.between(0f, 1f, 3f));
    assertTrue(MuMath.between(0.5f, 0.5f, 3.5f));
    assertTrue(MuMath.between(1f, 1f, 3f));
    assertTrue(MuMath.between(2f, 1f, 3f));
    assertTrue(MuMath.between(3f, 1f, 3f));
    assertTrue(MuMath.between(3.5f, 0.5f, 3.5f));
    assertFalse(MuMath.between(4f, 1f, 3f));
  }

  @Test
  void testBetween_int() {
    assertFalse(MuMath.between(0, 1, 3));
    assertTrue(MuMath.between(1, 1, 3));
    assertTrue(MuMath.between(2, 1, 3));
    assertTrue(MuMath.between(3, 1, 3));
    assertFalse(MuMath.between(4, 1, 3));
  }

  @Test
  void testBetween_long() {
    assertFalse(MuMath.between(0L, 1L, 3L));
    assertTrue(MuMath.between(1L, 1L, 3L));
    assertTrue(MuMath.between(2L, 1L, 3L));
    assertTrue(MuMath.between(3L, 1L, 3L));
    assertFalse(MuMath.between(4L, 1L, 3L));
  }

  @Test
  void testLerp_double() {
    assertEquals(0d, MuMath.lerp(0d, 0d, 1d));
    assertEquals(0d, MuMath.lerp(0.25d, -10d, 30d));
    assertEquals(-0.5d, MuMath.lerp(0.5d, 0d, -1d));
    assertEquals(0.5d, MuMath.lerp(0.5d, 0d, 1d));
    assertEquals(-1d, MuMath.lerp(1d, 0d, -1d));
    assertEquals(1d, MuMath.lerp(1d, 0d, 1d));
    assertEquals(5d, MuMath.lerp(0.5d, 0d, 10d));
    assertEquals(17d, MuMath.lerp(0.7d, 10d, 20d));
    assertEquals(-10d, MuMath.lerp(0d, -10d, 30d));
    assertEquals(10d, MuMath.lerp(0.50d, -10d, 30d));
    assertEquals(20d, MuMath.lerp(0.75d, -10d, 30d));
    assertEquals(30d, MuMath.lerp(1d, -10d, 30d));
    assertEquals(50d, MuMath.lerp(0.5d, 0d, 100d));
  }

  @Test
  void testLerp_float() {
    assertEquals(0f, MuMath.lerp(0f, 0f, 1f));
    assertEquals(0f, MuMath.lerp(0.25f, -10f, 30f));
    assertEquals(-0.5f, MuMath.lerp(0.5f, 0f, -1f));
    assertEquals(0.5f, MuMath.lerp(0.5f, 0f, 1f));
    assertEquals(-1f, MuMath.lerp(1f, 0f, -1f));
    assertEquals(1f, MuMath.lerp(1f, 0f, 1f));
    assertEquals(5f, MuMath.lerp(0.5f, 0f, 10f));
    assertEquals(17f, MuMath.lerp(0.7f, 10f, 20f));
    assertEquals(-10f, MuMath.lerp(0f, -10f, 30f));
    assertEquals(10f, MuMath.lerp(0.50f, -10f, 30f));
    assertEquals(20f, MuMath.lerp(0.75f, -10f, 30f));
    assertEquals(30f, MuMath.lerp(1f, -10f, 30f));
    assertEquals(50f, MuMath.lerp(0.5f, 0f, 100f));
  }

  @Test
  void testMin_double_0() {
    assertEquals(Double.POSITIVE_INFINITY, MuMath.min(new double[0]));
  }

  @Test
  void testMin_double_1() {
    assertEquals(-1d, MuMath.min(-1d));
    assertEquals(1d, MuMath.min(1d));
  }

  @Test
  void testMin_double_n() {
    assertEquals(-1.73d, MuMath.min(3.2d, 2.2d, 4.5d, -1.73d, 2.3d));
    assertEquals(2.2d, MuMath.min(3.2d, 2.2d, 4.5d, 2.3d));
  }

  @Test
  @SuppressWarnings("RedundantArrayCreation")
  void testMin_float_0() {
    assertEquals(Float.POSITIVE_INFINITY, MuMath.min(new float[0]));
  }

  @Test
  void testMin_float_1() {
    assertEquals(-1f, MuMath.min(-1f));
    assertEquals(1f, MuMath.min(1f));
  }

  @Test
  void testMin_float_n() {
    assertEquals(-1.73f, MuMath.min(3.2f, 2.2f, 4.5f, -1.73f, 2.3f));
    assertEquals(2.2f, MuMath.min(3.2f, 2.2f, 4.5f, 2.3f));
  }

  @Test
  void testMax_double_0() {
    assertEquals(Double.NEGATIVE_INFINITY, MuMath.max(new double[0]));
  }

  @Test
  void testMax_double_1() {
    assertEquals(-1d, MuMath.max(-1d));
    assertEquals(1d, MuMath.max(1d));
  }

  @Test
  @SuppressWarnings("RedundantArrayCreation")
  void testMax_float_0() {
    assertEquals(Float.NEGATIVE_INFINITY, MuMath.max(new float[0]));
  }

  @Test
  void testMax_double_n() {
    assertEquals(-1.8d, MuMath.max(-3.2d, -2.2d, -1.8d, -2.3d));
    assertEquals(4.5d, MuMath.max(3.2d, 2.2d, 4.5d, 2.3d));
  }

  @Test
  void testMax_float_1() {
    assertEquals(-1f, MuMath.max(-1f));
    assertEquals(1f, MuMath.max(1f));
  }

  @Test
  void testMax_float_n() {
    assertEquals(-1.8f, MuMath.max(-3.2f, -2.2f, -1.8f, -2.3f));
    assertEquals(4.5f, MuMath.max(3.2f, 2.2f, 4.5f, 2.3f));
  }

  @Test
  void testRandom_double() {
    final Random random = ThreadLocalRandom.current();
    final float min = 1.3f;
    final float max = 4.5f;
    for(int i = 0; i < 10; i++) {
      final float value = MuMath.random(random, min, max);
      assertTrue(value >= min);
      assertTrue(value <= max);
    }
  }

  @Test
  void testRandom_float() {
    final Random random = ThreadLocalRandom.current();
    final double min = 1.3d;
    final double max = 4.5d;
    for(int i = 0; i < 10; i++) {
      final double value = MuMath.random(random, min, max);
      assertTrue(value >= min);
      assertTrue(value <= max);
    }
  }

  @Test
  void testRandom_int() {
    final Random random = ThreadLocalRandom.current();
    final int min = 1;
    final int max = 4;
    for(int i = 0; i < 10; i++) {
      final int value = MuMath.random(random, min, max);
      assertTrue(value >= min);
      assertTrue(value <= max);
    }
  }

  @Test
  void testSum_double() {
    assertEquals(4d, MuMath.sum(1d, 2d, 1d));
    assertEquals(6.9d, MuMath.sum(0.6d, 1d, 5.3d));
  }

  @Test
  void testSum_float() {
    assertEquals(4f, MuMath.sum(1f, 2f, 1f));
    assertEquals(6.9f, MuMath.sum(0.6f, 1f, 5.3f));
  }

  @Test
  void testSum_int() {
    assertEquals(4, MuMath.sum(1, 2, 1));
  }

  @Test
  void testSum_long() {
    assertEquals(4L, MuMath.sum(1L, 2L, 1L));
  }
}
