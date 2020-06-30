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

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A tuple3.
 *
 * @param <A> the a type
 * @param <B> the b type
 * @param <C> the c type
 */
public class Tuple3<A, B, C> extends Tuple2<A, B> implements Examinable {
  protected final C c;

  /**
   * Creates a new tuple3.
   *
   * @param a the a value
   * @param b the b value
   * @param c the c value
   * @param <A> the a type
   * @param <B> the b type
   * @param <C> the c type
   * @return the tuple3
   */
  public static <A, B, C> @NonNull Tuple3<A, B, C> tuple3(final @Nullable A a, final @Nullable B b, final @Nullable C c) {
    return new Tuple3<>(a, b, c);
  }

  /**
   * Creates a new tuple3 with an a value.
   *
   * @param a the a value
   * @param <A> the a type
   * @param <B> the b type
   * @param <C> the c type
   * @return the tuple3
   */
  public static <A, B, C> @NonNull Tuple3<A, B, C> tuple3a(final @Nullable A a) {
    return new Tuple3<>(a, null, null);
  }

  /**
   * Creates a new tuple3 with a b value.
   *
   * @param b the b value
   * @param <A> the a type
   * @param <B> the b type
   * @param <C> the c type
   * @return the tuple3
   */
  public static <A, B, C> @NonNull Tuple3<A, B, C> tuple3b(final @Nullable B b) {
    return new Tuple3<>(null, b, null);
  }

  /**
   * Creates a new tuple3 with a c value.
   *
   * @param c the c value
   * @param <A> the a type
   * @param <B> the b type
   * @param <C> the c type
   * @return the tuple3
   */
  public static <A, B, C> @NonNull Tuple3<A, B, C> tuple3c(final @Nullable C c) {
    return new Tuple3<>(null, null, c);
  }

  public Tuple3(final @Nullable A a, final @Nullable B b, final @Nullable C c) {
    super(a, b);
    this.c = c;
  }

  /**
   * Gets the c value.
   *
   * @return the c value
   */
  public /* @Nullable */ C c() {
    return this.c;
  }

  @Override
  public @NonNull <NA, NB> Tuple3<NA, NB, C> map(final @NonNull Function<? super A, ? extends NA> a, final @NonNull Function<? super B, ? extends NB> b) {
    return new Tuple3<>(a.apply(this.a), b.apply(this.b), this.c);
  }

  /**
   * Map the a, b, and c values of this tuple3.
   *
   * @param a the a value function
   * @param b the b value function
   * @param c the c value function
   * @param <NA> the a type
   * @param <NB> the b type
   * @param <NC> the c type
   * @return a new tuple3
   */
  public <NA, NB, NC> @NonNull Tuple3<NA, NB, NC> map(final @NonNull Function<? super A, ? extends NA> a, final @NonNull Function<? super B, ? extends NB> b, final @NonNull Function<? super C, ? extends NC> c) {
    return new Tuple3<>(a.apply(this.a), b.apply(this.b), c.apply(this.c));
  }

  /**
   * Map the a value of this tuple3.
   *
   * @param function the function
   * @param <NA> the a type
   * @return a new tuple3
   */
  @Override
  public <NA> @NonNull Tuple3<NA, B, C> mapA(final @NonNull Function<? super A, ? extends NA> function) {
    return new Tuple3<>(function.apply(this.a), this.b, this.c);
  }

  /**
   * Map the b value of this tuple3.
   *
   * @param function the function
   * @param <NB> the b type
   * @return a new tuple3
   */
  @Override
  public <NB> @NonNull Tuple3<A, NB, C> mapB(final @NonNull Function<? super B, ? extends NB> function) {
    return new Tuple3<>(this.a, function.apply(this.b), this.c);
  }

  /**
   * Map the c value of this tuple3.
   *
   * @param function the function
   * @param <NC> the c type
   * @return a new tuple3
   */
  public <NC> @NonNull Tuple3<A, B, NC> mapC(final @NonNull Function<? super C, ? extends NC> function) {
    return new Tuple3<>(this.a, this.b, function.apply(this.c));
  }

  @Override
  public @NonNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
      super.examinableProperties(),
      Stream.of(
        ExaminableProperty.of("c", this.c)
      )
    );
  }

  @Override
  public String toString() {
    return "Tuple3{a=" + this.a + ", b=" + this.b + ", c=" + this.c + '}';
  }

  @Override
  public boolean equals(final Object other) {
    if(this == other) return true;
    if(other == null || this.getClass() != other.getClass()) return false;
    if(!super.equals(other)) return false;
    final Tuple3<?, ?, ?> that = (Tuple3<?, ?, ?>) other;
    return Objects.equals(this.c, that.c);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = (31 * result) + Objects.hashCode(this.c);
    return result;
  }
}
