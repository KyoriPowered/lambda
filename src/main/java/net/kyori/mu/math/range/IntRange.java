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

import java.util.Random;
import java.util.function.IntPredicate;
import net.kyori.mu.math.MuMath;
import net.kyori.mu.random.RandomIntSource;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An int range.
 */
public interface IntRange extends IntPredicate {
  /**
   * Creates an int range.
   *
   * @param value the minimum value, exclusive
   * @return an int range
   */
  static @NonNull Above above(final int value) {
    return new IntRangeImpl.AboveImpl(value);
  }

  /**
   * Creates an int range.
   *
   * @param value the minimum value, inclusive
   * @return an int range
   */
  static @NonNull AtLeast atLeast(final int value) {
    return new IntRangeImpl.AtLeastImpl(value);
  }

  /**
   * Creates an int range.
   *
   * @param min the minimum value, inclusive
   * @param max the maximum value, inclusive
   * @return an int range
   */
  static @NonNull Between between(final int min, final int max) {
    return new IntRangeImpl.BetweenImpl(min, max);
  }

  /**
   * Creates an int range.
   *
   * @param value the value
   * @return an int range
   */
  static @NonNull Constant constant(final int value) {
    return new IntRangeImpl.ConstantImpl(value);
  }

  /**
   * Creates an int range.
   *
   * @param value the maximum value, inclusive
   * @return an int range
   */
  static @NonNull AtMost atMost(final int value) {
    return new IntRangeImpl.AtMostImpl(value);
  }

  /**
   * Creates an int range.
   *
   * @param value the maximum value, exclusive
   * @return an int range
   */
  static @NonNull Below below(final int value) {
    return new IntRangeImpl.BelowImpl(value);
  }

  /**
   * Greater-than {@code value}.
   */
  interface Above extends Single {
  }

  /**
   * Greater-than-or-equal-to {@code value}.
   */
  interface AtLeast extends Single {
  }

  /**
   * Between {@code min} and {@code max}.
   */
  interface Between extends IntRange, RandomIntSource {
    /**
     * Gets the min value.
     *
     * @return the min value
     */
    int min();

    /**
     * Gets the max value.
     *
     * @return the max value
     */
    int max();

    /**
     * Gets a random value between {@link #min()} and {@link #max()}.
     *
     * @param random the random
     * @return a random value
     */
    @Override
    default int randomInt(final @NonNull Random random) {
      return MuMath.random(random, this.min(), this.max());
    }
  }

  /**
   * Exactly {@code value}.
   */
  interface Constant extends RandomIntSource, Single {
    // This value is not random - we want to support providing a constant value.
    @Override
    default int randomInt(final @NonNull Random random) {
      return this.value();
    }
  }

  /**
   * Less-than-or-equal-to {@code value}.
   */
  interface AtMost extends Single {
  }

  /**
   * Less-than {@code value}.
   */
  interface Below extends Single {
  }

  /**
   * A range with a single value.
   */
  interface Single extends IntRange {
    /**
     * Gets the value.
     *
     * @return the value
     */
    int value();
  }
}
