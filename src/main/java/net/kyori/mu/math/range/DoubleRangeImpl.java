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

import net.kyori.mu.math.MuMath;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class DoubleRangeImpl {
  static boolean equals(final double a, final double b) {
    return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
  }

  static final class AboveImpl extends AbstractSingle implements DoubleRange.Above {
    AboveImpl(final double value) {
      super(value);
    }

    @Override
    public boolean test(final double value) {
      return value > this.value;
    }

    @Override
    public @NonNull String toString() {
      return "DoubleRange.above(" + this.value + ")";
    }
  }

  static final class AtLeastImpl extends AbstractSingle implements DoubleRange.AtLeast {
    AtLeastImpl(final double value) {
      super(value);
    }

    @Override
    public boolean test(final double value) {
      return value >= this.value;
    }

    @Override
    public @NonNull String toString() {
      return "DoubleRange.atLeast(" + this.value + ")";
    }
  }

  static final class BetweenImpl implements DoubleRange.Between {
    private final double min;
    private final double max;

    protected BetweenImpl(final double min, final double max) {
      this.min = min;
      this.max = max;
    }

    @Override
    public double min() {
      return this.min;
    }

    @Override
    public double max() {
      return this.max;
    }

    @Override
    public boolean test(final double value) {
      return MuMath.between(value, this.min, this.max);
    }

    @Override
    public @NonNull String toString() {
      return "DoubleRange.between(" + this.min + ", " + this.max + ")";
    }

    @Override
    public boolean equals(final @Nullable Object other) {
      if(this == other) return true;
      if(other == null || this.getClass() != other.getClass()) return false;
      final BetweenImpl that = (BetweenImpl) other;
      return DoubleRangeImpl.equals(this.min, that.min) && DoubleRangeImpl.equals(this.max, that.max);
    }

    @Override
    public int hashCode() {
      int result = Double.hashCode(this.min);
      result = (31 * result) + Double.hashCode(this.max);
      return result;
    }
  }

  static final class ConstantImpl extends AbstractSingle implements DoubleRange.Constant {
    ConstantImpl(final double value) {
      super(value);
    }

    @Override
    public boolean test(final double value) {
      return DoubleRangeImpl.equals(this.value, value);
    }

    @Override
    public @NonNull String toString() {
      return "DoubleRange.constant(" + this.value + ")";
    }
  }

  static final class AtMostImpl extends AbstractSingle implements DoubleRange.AtMost {
    AtMostImpl(final double value) {
      super(value);
    }

    @Override
    public boolean test(final double value) {
      return value <= this.value;
    }

    @Override
    public @NonNull String toString() {
      return "DoubleRange.atMost(" + this.value + ")";
    }
  }

  static final class BelowImpl extends AbstractSingle implements DoubleRange.Below {
    BelowImpl(final double value) {
      super(value);
    }

    @Override
    public boolean test(final double value) {
      return value < this.value;
    }

    @Override
    public @NonNull String toString() {
      return "DoubleRange.below(" + this.value + ")";
    }
  }

  static abstract class AbstractSingle implements DoubleRange.Single {
    final double value;

    AbstractSingle(final double value) {
      this.value = value;
    }

    @Override
    public final double value() {
      return this.value;
    }

    @Override
    public abstract @NonNull String toString();

    @Override
    public final boolean equals(final @Nullable Object other) {
      if(this == other) return true;
      if(other == null || this.getClass() != other.getClass()) return false;
      final AbstractSingle that = (AbstractSingle) other;
      return DoubleRangeImpl.equals(this.value, that.value);
    }

    @Override
    public final int hashCode() {
      return Double.hashCode(this.value);
    }
  }
}
