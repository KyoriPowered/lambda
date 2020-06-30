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
import java.util.function.DoublePredicate;
import net.kyori.mu.math.MuMath;
import net.kyori.mu.random.RandomDoubleSource;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A double range.
 */
public interface DoubleRange extends DoublePredicate {
  /**
   * Creates a double range.
   *
   * @param value the minimum value, exclusive
   * @return a double range
   */
  static @NonNull Above above(final double value) {
    return new DoubleRangeImpl.AboveImpl(value);
  }

  /**
   * Creates a double range.
   *
   * @param value the minimum value, inclusive
   * @return a double range
   */
  static @NonNull AtLeast atLeast(final double value) {
    return new DoubleRangeImpl.AtLeastImpl(value);
  }

  /**
   * Creates a double range.
   *
   * @param min the minimum value, inclusive
   * @param max the maximum value, inclusive
   * @return a double range
   */
  static @NonNull Between between(final double min, final double max) {
    return new DoubleRangeImpl.BetweenImpl(min, max);
  }

  /**
   * Creates a double range.
   *
   * @param value the value
   * @return a double range
   */
  static @NonNull Constant constant(final double value) {
    return new DoubleRangeImpl.ConstantImpl(value);
  }

  /**
   * Creates a double range.
   *
   * @param value the maximum value, inclusive
   * @return a double range
   */
  static @NonNull AtMost atMost(final double value) {
    return new DoubleRangeImpl.AtMostImpl(value);
  }

  /**
   * Creates a double range.
   *
   * @param value the maximum value, exclusive
   * @return a double range
   */
  static @NonNull Below below(final double value) {
    return new DoubleRangeImpl.BelowImpl(value);
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
  interface Between extends DoubleRange, RandomDoubleSource {
    /**
     * Gets the min value.
     *
     * @return the min value
     */
    double min();

    /**
     * Gets the max value.
     *
     * @return the max value
     */
    double max();

    /**
     * Gets a random value between {@link #min()} and {@link #max()}.
     *
     * @param random the random
     * @return a random value
     */
    @Override
    default double randomDouble(final @NonNull Random random) {
      return MuMath.random(random, this.min(), this.max());
    }
  }

  /**
   * Exactly {@code value}.
   */
  interface Constant extends RandomDoubleSource, Single {
    // This value is not random - we want to support providing a constant value.
    @Override
    default double randomDouble(final @NonNull Random random) {
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
  interface Single extends DoubleRange {
    /**
     * Gets the value.
     *
     * @return the value
     */
    double value();
  }
}
