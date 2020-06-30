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
package net.kyori.mu.tuple;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A tuple2.
 *
 * @param <A> the a type
 * @param <B> the b type
 */
public class Tuple2<A, B> implements Examinable {
  protected final A a;
  protected final B b;

  /**
   * Creates a new tuple2.
   *
   * @param a the a value
   * @param b the b value
   * @param <A> the a type
   * @param <B> the b type
   * @return the tuple2
   */
  public static <A, B> @NonNull Tuple2<A, B> tuple2(final @Nullable A a, final @Nullable B b) {
    return new Tuple2<>(a, b);
  }

  /**
   * Creates a new tuple2 with a a value.
   *
   * @param a the a value
   * @param <A> the a type
   * @param <B> the b type
   * @return the tuple2
   */
  public static <A, B> @NonNull Tuple2<A, B> tuple2a(final @Nullable A a) {
    return new Tuple2<>(a, null);
  }

  /**
   * Creates a new tuple2 with a b value.
   *
   * @param b the b value
   * @param <A> the a type
   * @param <B> the b type
   * @return the tuple2
   */
  public static <A, B> @NonNull Tuple2<A, B> tuple2b(final @Nullable B b) {
    return new Tuple2<>(null, b);
  }

  /**
   * Creates a new tuple2 from a map entry.
   *
   * @param entry the entry
   * @param <A> the a type
   * @param <B> the b type
   * @return the tuple2
   */
  public static <A, B> @NonNull Tuple2<A, B> from(final Map.@NonNull Entry<A, B> entry) {
    return new Tuple2<>(entry.getKey(), entry.getValue());
  }

  public Tuple2(final @Nullable A a, final @Nullable B b) {
    this.a = a;
    this.b = b;
  }

  /**
   * Gets the a value.
   *
   * @return the a value
   */
  public /* @Nullable */ A a() {
    return this.a;
  }

  /**
   * Gets the b value.
   *
   * @return the b value
   */
  public /* @Nullable */ B b() {
    return this.b;
  }

  /**
   * Map the a and b value of this tuple2.
   *
   * @param a the a value function
   * @param b the b value function
   * @param <NA> the a type
   * @param <NB> the b type
   * @return a new tuple2
   */
  public <NA, NB> @NonNull Tuple2<NA, NB> map(final @NonNull Function<? super A, ? extends NA> a, final @NonNull Function<? super B, ? extends NB> b) {
    return new Tuple2<>(a.apply(this.a), b.apply(this.b));
  }

  /**
   * Map the a value of this tuple2.
   *
   * @param function the function
   * @param <NA> the a type
   * @return a new tuple2
   */
  public <NA> @NonNull Tuple2<NA, B> mapA(final @NonNull Function<? super A, ? extends NA> function) {
    return new Tuple2<>(function.apply(this.a), this.b);
  }

  /**
   * Map the b value of this tuple2.
   *
   * @param function the function
   * @param <NB> the b type
   * @return a new tuple2
   */
  public <NB> @NonNull Tuple2<A, NB> mapB(final @NonNull Function<? super B, ? extends NB> function) {
    return new Tuple2<>(this.a, function.apply(this.b));
  }

  @Override
  public @NonNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
      ExaminableProperty.of("a", this.a),
      ExaminableProperty.of("b", this.b)
    );
  }

  @Override
  public String toString() {
    return "Tuple2{a=" + this.a + ", b=" + this.b + '}';
  }

  @Override
  public boolean equals(final Object other) {
    if(this == other) return true;
    if(other == null || this.getClass() != other.getClass()) return false;
    final Tuple2<?, ?> that = (Tuple2<?, ?>) other;
    return Objects.equals(this.a, that.a) && Objects.equals(this.b, that.b);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.a, this.b);
  }
}
