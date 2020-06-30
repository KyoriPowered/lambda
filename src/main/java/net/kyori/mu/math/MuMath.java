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
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 2 plus 2 equals 5... right?
 */
public final class MuMath {
  private MuMath() {
  }

  /**
   * Clamps {@code value} between {@code min} and {@code max}.
   *
   * @param value the value
   * @param min the min value
   * @param max the max value
   * @return the clamped value
   */
  @SuppressWarnings("ManualMinMaxCalculation")
  public static double clamp(final double value, final double min, final double max) {
    if(value < min) return min;
    if(value > max) return max;
    return value;
  }

  /**
   * Clamps {@code value} between {@code min} and {@code max}.
   *
   * @param value the value
   * @param min the min value
   * @param max the max value
   * @return the clamped value
   */
  @SuppressWarnings("ManualMinMaxCalculation")
  public static float clamp(final float value, final float min, final float max) {
    if(value < min) return min;
    if(value > max) return max;
    return value;
  }

  /**
   * Clamps {@code value} between {@code min} and {@code max}.
   *
   * @param value the value
   * @param min the min value
   * @param max the max value
   * @return the clamped value
   */
  @SuppressWarnings("ManualMinMaxCalculation")
  public static int clamp(final int value, final int min, final int max) {
    if(value < min) return min;
    if(value > max) return max;
    return value;
  }

  /**
   * Clamps {@code value} between {@code min} and {@code max}.
   *
   * @param value the value
   * @param min the min value
   * @param max the max value
   * @return the clamped value
   */
  @SuppressWarnings("ManualMinMaxCalculation")
  public static long clamp(final long value, final long min, final long max) {
    if(value < min) return min;
    if(value > max) return max;
    return value;
  }

  /**
   * Checks if {@code value} is greater than or equal to {@code min} and less than or equal to {@code max}.
   *
   * @param value the value
   * @param min the min value, inclusive
   * @param max the max value, inclusive
   * @return {@code true} if between {@code min} and {@code max}, {@code false} otherwise
   */
  public static boolean between(final double value, final double min, final double max) {
    return value >= min && value <= max;
  }

  /**
   * Checks if {@code value} is greater than or equal to {@code min} and less than or equal to {@code max}.
   *
   * @param value the value
   * @param min the min value, inclusive
   * @param max the max value, inclusive
   * @return {@code true} if between {@code min} and {@code max}, {@code false} otherwise
   */
  public static boolean between(final float value, final float min, final float max) {
    return value >= min && value <= max;
  }

  /**
   * Checks if {@code value} is greater than or equal to {@code min} and less than or equal to {@code max}.
   *
   * @param value the value
   * @param min the min value, inclusive
   * @param max the max value, inclusive
   * @return {@code true} if between {@code min} and {@code max}, {@code false} otherwise
   */
  public static boolean between(final int value, final int min, final int max) {
    return value >= min && value <= max;
  }

  /**
   * Checks if {@code value} is greater than or equal to {@code min} and less than or equal to {@code max}.
   *
   * @param value the value
   * @param min the min value, inclusive
   * @param max the max value, inclusive
   * @return {@code true} if between {@code min} and {@code max}, {@code false} otherwise
   */
  public static boolean between(final long value, final long min, final long max) {
    return value >= min && value <= max;
  }

  /**
   * Linearly interpolates between {@code a} and {@code b} by {@code t}.
   *
   * @param t interpolation value
   * @param a lower bound
   * @param b upper bound
   * @return the interpolated value
   */
  public static double lerp(final double t, final double a, final double b) {
    return a + t * (b - a);
  }

  /**
   * Linearly interpolates between {@code a} and {@code b} by {@code t}.
   *
   * @param t interpolation value
   * @param a lower bound
   * @param b upper bound
   * @return the interpolated value
   */
  public static float lerp(final float t, final float a, final float b) {
    return a + t * (b - a);
  }

  /**
   * Gets the minimum value from an array of values.
   *
   * @param values the values
   * @return the minimum value
   */
  @SuppressWarnings("ForLoopReplaceableByForEach")
  public static double min(final double... values) {
    double value = Double.POSITIVE_INFINITY;
    for(int i = 0, length = values.length; i < length; i++) {
      value = Math.min(value, values[i]);
    }
    return value;
  }

  /**
   * Gets the minimum value from an array of values.
   *
   * @param values the values
   * @return the minimum value
   */
  @SuppressWarnings("ForLoopReplaceableByForEach")
  public static float min(final float... values) {
    float value = Float.POSITIVE_INFINITY;
    for(int i = 0, length = values.length; i < length; i++) {
      value = Math.min(value, values[i]);
    }
    return value;
  }

  /**
   * Gets the maximum value from an array of values.
   *
   * @param values the values
   * @return the maximum value
   */
  @SuppressWarnings("ForLoopReplaceableByForEach")
  public static double max(final double... values) {
    double value = Double.NEGATIVE_INFINITY;
    for(int i = 0, length = values.length; i < length; i++) {
      value = Math.max(value, values[i]);
    }
    return value;
  }

  /**
   * Gets the maximum value from an array of values.
   *
   * @param values the values
   * @return the maximum value
   */
  @SuppressWarnings("ForLoopReplaceableByForEach")
  public static double max(final float... values) {
    float value = Float.NEGATIVE_INFINITY;
    for(int i = 0, length = values.length; i < length; i++) {
      value = Math.max(value, values[i]);
    }
    return value;
  }

  /**
   * Gets a random value between {@code min} and {@code max}.
   *
   * @param random the random source
   * @param min the minimum value
   * @param max the maximum value
   * @return a value
   */
  public static double random(final @NonNull Random random, final double min, final double max) {
    return (random.nextDouble() * (max - min)) + min;
  }

  /**
   * Gets a random value between {@code min} and {@code max}.
   *
   * @param random the random source
   * @param min the minimum value
   * @param max the maximum value
   * @return a value
   */
  public static float random(final @NonNull Random random, final float min, final float max) {
    return (random.nextFloat() * (max - min)) + min;
  }

  /**
   * Gets a random value between {@code min} and {@code max}.
   *
   * @param random the random source
   * @param min the minimum value
   * @param max the maximum value
   * @return a value
   */
  public static int random(final @NonNull Random random, final int min, final int max) {
    return random.nextInt((max - min) + 1) + min;
  }

  /**
   * Sums up {@code values}.
   *
   * @param values the values
   * @return the sum
   */
  @SuppressWarnings("ForLoopReplaceableByForEach")
  public static double sum(final double... values) {
    double sum = 0d;
    for(int i = 0, length = values.length; i < length; i++) {
      sum += values[i];
    }
    return sum;
  }

  /**
   * Sums up {@code values}.
   *
   * @param values the values
   * @return the sum
   */
  @SuppressWarnings("ForLoopReplaceableByForEach")
  public static float sum(final float... values) {
    float sum = 0f;
    for(int i = 0, length = values.length; i < length; i++) {
      sum += values[i];
    }
    return sum;
  }

  /**
   * Sums up {@code values}.
   *
   * @param values the values
   * @return the sum
   */
  @SuppressWarnings("ForLoopReplaceableByForEach")
  public static int sum(final int... values) {
    int sum = 0;
    for(int i = 0, length = values.length; i < length; i++) {
      sum += values[i];
    }
    return sum;
  }

  /**
   * Sums up {@code values}.
   *
   * @param values the values
   * @return the sum
   */
  @SuppressWarnings("ForLoopReplaceableByForEach")
  public static long sum(final long... values) {
    long sum = 0;
    for(int i = 0, length = values.length; i < length; i++) {
      sum += values[i];
    }
    return sum;
  }
}
