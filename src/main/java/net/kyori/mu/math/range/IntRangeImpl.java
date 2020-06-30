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

final class IntRangeImpl {
  static final class AboveImpl extends AbstractSingle implements IntRange.Above {
    AboveImpl(final int value) {
      super(value);
    }

    @Override
    public boolean test(final int value) {
      return value > this.value;
    }

    @Override
    public @NonNull String toString() {
      return "IntRange.above(" + this.value + ")";
    }
  }

  static final class AtLeastImpl extends AbstractSingle implements IntRange.AtLeast {
    AtLeastImpl(final int value) {
      super(value);
    }

    @Override
    public boolean test(final int value) {
      return value >= this.value;
    }

    @Override
    public @NonNull String toString() {
      return "IntRange.atLeast(" + this.value + ")";
    }
  }

  static final class BetweenImpl implements IntRange.Between {
    private final int min;
    private final int max;

    protected BetweenImpl(final int min, final int max) {
      this.min = min;
      this.max = max;
    }

    @Override
    public int min() {
      return this.min;
    }

    @Override
    public int max() {
      return this.max;
    }

    @Override
    public boolean test(final int value) {
      return MuMath.between(value, this.min, this.max);
    }

    @Override
    public @NonNull String toString() {
      return "IntRange.between(" + this.min + ", " + this.max + ")";
    }

    @Override
    public boolean equals(final @Nullable Object other) {
      if(this == other) return true;
      if(other == null || this.getClass() != other.getClass()) return false;
      final BetweenImpl that = (BetweenImpl) other;
      return this.min == that.min && this.max == that.max;
    }

    @Override
    public int hashCode() {
      int result = this.min;
      result = (31 * result) + this.max;
      return result;
    }
  }

  static final class ConstantImpl extends AbstractSingle implements IntRange.Constant {
    ConstantImpl(final int value) {
      super(value);
    }

    @Override
    public boolean test(final int value) {
      return value == this.value;
    }

    @Override
    public @NonNull String toString() {
      return "IntRange.constant(" + this.value + ")";
    }
  }

  static final class AtMostImpl extends AbstractSingle implements IntRange.AtMost {
    AtMostImpl(final int value) {
      super(value);
    }

    @Override
    public boolean test(final int value) {
      return value <= this.value;
    }

    @Override
    public @NonNull String toString() {
      return "IntRange.atMost(" + this.value + ")";
    }
  }

  static final class BelowImpl extends AbstractSingle implements IntRange.Below {
    BelowImpl(final int value) {
      super(value);
    }

    @Override
    public boolean test(final int value) {
      return value < this.value;
    }

    @Override
    public @NonNull String toString() {
      return "IntRange.below(" + this.value + ")";
    }
  }

  static abstract class AbstractSingle implements IntRange.Single {
    final int value;

    AbstractSingle(final int value) {
      this.value = value;
    }

    @Override
    public final int value() {
      return this.value;
    }

    @Override
    public abstract @NonNull String toString();

    @Override
    public final boolean equals(final @Nullable Object other) {
      if(this == other) return true;
      if(other == null || this.getClass() != other.getClass()) return false;
      final AbstractSingle that = (AbstractSingle) other;
      return this.value == that.value;
    }

    @Override
    public final int hashCode() {
      return this.value;
    }
  }
}
