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
package net.kyori.mu;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.mu.collection.MuIterators;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Something that may or may not contain a value.
 *
 * @param <T> the value type
 */
public interface Maybe<T> extends Examinable, Iterable<T> {
  /**
   * A collector that accumulates zero or one elements into a {@code Maybe}.
   *
   * @see #collector()
   */
  Collector<?, ?, Maybe<?>> COLLECTOR = Collectors.collectingAndThen(
    Collectors.reducing((a, b) -> {
      throw new AmbiguousElementException();
    }),
    Maybe::from
  );

  /**
   * Returns a {@code Maybe} containing nothing.
   *
   * @param <T> the type
   * @return a {@code Maybe} containing nothing.
   */
  @SuppressWarnings("unchecked")
  static <T> @NonNull Maybe<T> nothing() {
    return (Maybe<T>) Nothing.NOTHING;
  }

  /**
   * Returns a {@code Maybe} containing {@code value}.
   *
   * @param value the value
   * @param <T> the type
   * @return a {@code Maybe} with a value
   */
  static <T> @NonNull Maybe<T> just(final @NonNull T value) {
    return new Just<>(requireNonNull(value));
  }

  /**
   * Returns a {@code Maybe} containing {@code value} if it is non-{@code null}, otherwise returns an empty {@code Maybe}.
   *
   * @param value the value
   * @param <T> the value type
   * @return a {@code Maybe} with a value
   */
  static <T> @NonNull Maybe<T> maybe(final @Nullable T value) {
    return value == null ? nothing() : just(value);
  }

  /**
   * Converts an {@code Optional} into a {@code Maybe}.
   *
   * @param optional the optional
   * @param <T> the value type
   * @return a {@code Maybe}
   */
  @SuppressWarnings("OptionalIsPresent")
  static <T> @NonNull Maybe<T> from(final @NonNull Optional<T> optional) {
    return optional.isPresent() ? just(optional.get()) : nothing();
  }

  /**
   * Returns {@code true} if this {@code Maybe} is empty.
   *
   * @return {@code true} if this {@code Maybe} is empty
   */
  boolean isNothing();

  /**
   * Returns {@code true} if this {@code Maybe} has a value.
   *
   * @return {@code true} if this {@code Maybe} has a value
   */
  boolean isJust();

  /**
   * Gets the value if present, otherwise returns {@code defaultValue}.
   *
   * @param defaultValue the default value
   * @return the value if present, otherwise {@code defaultValue}
   */
  T orDefault(final @Nullable T defaultValue);

  /**
   * Gets the value if present, otherwise returns a value supplied by {@code other}.
   *
   * @param other the other value supplier
   * @return the value if present, otherwise a value supplied by {@code other}
   */
  T orGet(final @NonNull Supplier<? extends T> other);

  /**
   * Gets the value if present, otherwise throws a {@link NoSuchElementException}.
   *
   * @return the value
   * @throws NoSuchElementException if no value is present
   */
  default @NonNull T orThrow() throws NoSuchElementException {
    return this.orThrow(NoSuchElementException::new);
  }

  /**
   * Gets the value if present, otherwise throws an exception of type {@code X} supplied by {@code supplier} if not.
   *
   * @param supplier the exception supplier
   * @param <X> the exception type
   * @return the value
   * @throws X if no value is present
   */
  <X extends Throwable> @NonNull T orThrow(final @NonNull Supplier<X> supplier) throws X;

  /**
   * Returns a {@code Maybe} containing the value if it is present, otherwise returns {@code that}.
   *
   * @param that the other maybe
   * @return a {@code Maybe} containing the value if it is present, otherwise returns {@code that}
   */
  @NonNull Maybe<T> or(final @NonNull Maybe<? extends T> that);

  /**
   * Returns a {@code Maybe} containing the value if it is present, otherwise returns a {@code Maybe} supplied by {@code that}.
   *
   * @param that the other maybe supplier
   * @return a {@code Maybe} containing the value if it is present, otherwise returns a {@code Maybe} supplied by {@code that}
   */
  @NonNull Maybe<T> or(final @NonNull Supplier<? extends Maybe<? extends T>> that);

  /**
   * Returns a {@code Maybe} containing the value if it is present and matches the given predicate, otherwise returns an empty {@code Maybe}.
   *
   * @param predicate the predicate to apply to the value if it is present
   * @return a {@code Maybe} containing the value if the value is present and the value matches the given predicate, otherwise an empty {@code Maybe}
   */
  @NonNull Maybe<T> filter(final @NonNull Predicate<? super T> predicate);

  /**
   * Returns a {@code Maybe} containing the result of applying the given function to the value if it is present, otherwise returns an empty {@code Maybe}.
   *
   * @param function the function to apply to the value if it is present
   * @param <U> the new value type
   * @return a {@code Maybe} containing the result of applying the given function to the value if it is present, otherwise an empty {@code Maybe}
   */
  <U> @NonNull Maybe<U> map(final @NonNull Function<? super T, ? extends U> function);

  /**
   * Attempts to cast from {@code T} to {@code X} if the value contained is an instance of {@code type}.
   *
   * @param type the type
   * @param <X> the type
   * @return a {@code Maybe}
   */
  <X> @NonNull Maybe<X> cast(final @NonNull Class<X> type);

  /**
   * Returns the result of applying the given function to the value if it is present, otherwise returns an empty {@code Maybe}.
   *
   * @param function the function to apply to the value if it is present
   * @param <U> the new value type
   * @return the result of applying the given function to the value if it is present, otherwise an empty {@code Maybe}
   */
  <U> @NonNull Maybe<U> flatMap(final @NonNull Function<? super T, ? extends Maybe<? extends U>> function);

  /**
   * Runs {@code nothing} if there is no value present.
   *
   * @param nothing the action to run if nothing is present
   * @return this maybe
   */
  @NonNull Maybe<T> ifNothing(final @NonNull Runnable nothing);

  /**
   * Provides {@code just} with the value if it is present.
   *
   * @param just the action to run if something is present
   * @return this maybe
   */
  @NonNull Maybe<T> ifJust(final @NonNull Consumer<? super T> just);

  /**
   * Returns a {@code Stream} containing the value if it is present, otherwise an empty {@code Stream}.
   *
   * @return a {@code Stream} containing the value if it is present, otherwise an empty {@code Stream}
   */
  @NonNull Stream<T> stream();

  /**
   * Converts this {@code Maybe} into an {@code Optional}.
   *
   * @return an optional
   */
  @NonNull Optional<T> optional();

  /**
   * Returns a collector that accumulates zero or one elements into a {@code Maybe}.
   *
   * @param <T> the value type
   * @return a collector that accumulates zero or one elements into a {@code Maybe}
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  static <T> @NonNull Collector<T, ?, Maybe<T>> collector() {
    return (Collector) COLLECTOR;
  }

  /**
   * Casts {@code maybe} to an maybe of type {@code type} if the value held by {@code maybe} is an instance of {@code type}
   *
   * @param maybe the maybe
   * @param type the type
   * @param <T> the type
   * @return a maybe
   */
  static <T> @NonNull Maybe<T> cast(final @NonNull Maybe<?> maybe, final @NonNull Class<T> type) {
    return maybe.cast(type);
  }

  /**
   * Gets a maybe with just {@code object} if {@code object} is an instance of {@code type}, or a maybe with nothing.
   *
   * @param object the object
   * @param type the type
   * @param <T> the type
   * @return a maybe
   */
  @SuppressWarnings("unchecked")
  static <T> @NonNull Maybe<T> cast(final @Nullable Object object, final @NonNull Class<T> type) {
    return type.isInstance(object) ? just((T) object) : nothing();
  }

  /**
   * Returns the first {@link #isJust() populated} {@code Maybe}, or an empty {@code Maybe}.
   *
   * @param maybes the possible options
   * @param <T> the value type
   * @return the first populated {@code Maybe}, or an empty {@code Maybe}
   */
  @SafeVarargs
  @SuppressWarnings("unchecked")
  static <T> @NonNull Maybe<T> first(final @NonNull Maybe<? extends T>... maybes) {
    for(int i = 0, length = maybes.length; i < length; i++) {
      final Maybe<? extends T> maybe = maybes[i];
      if(maybe.isJust()) {
        return (Maybe<T>) maybe;
      }
    }
    return nothing();
  }

  /**
   * Returns the first {@link #isJust() populated} {@code Maybe}, or an empty {@code Maybe}.
   *
   * @param maybes the possible options
   * @param <T> the value type
   * @return the first populated {@code Maybe}, or an empty {@code Maybe}
   */
  @SuppressWarnings("unchecked")
  static <T> @NonNull Maybe<T> first(final @NonNull Iterable<Maybe<? extends T>> maybes) {
    for(final Maybe<? extends T> maybe : maybes) {
      if(maybe.isJust()) {
        return (Maybe<T>) maybe;
      }
    }
    return nothing();
  }

  /**
   * Nothing.
   *
   * @param <T> the value type
   */
  final class Nothing<T> implements Maybe<T> {
    /* package */ static final Maybe<Object> NOTHING = new Nothing<>();

    /* package */ Nothing() {
    }

    @Override
    public boolean isNothing() {
      return true;
    }

    @Override
    public boolean isJust() {
      return false;
    }

    @Override
    public T orDefault(final @Nullable T defaultValue) {
      return defaultValue;
    }

    @Override
    public T orGet(final @NonNull Supplier<? extends T> other) {
      return other.get();
    }

    @Override
    public @NonNull T orThrow() throws NoSuchElementException {
      throw new NoSuchElementException();
    }

    @Override
    public <X extends Throwable> @NonNull T orThrow(final @NonNull Supplier<X> supplier) throws X {
      throw supplier.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull Maybe<T> or(final @NonNull Maybe<? extends T> that) {
      return (Maybe<T>) that;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull Maybe<T> or(final @NonNull Supplier<? extends Maybe<? extends T>> that) {
      return (Maybe<T>) that.get();
    }

    @Override
    public @NonNull Maybe<T> filter(final @NonNull Predicate<? super T> predicate) {
      return nothing();
    }

    @Override
    public <U> @NonNull Maybe<U> map(final @NonNull Function<? super T, ? extends U> function) {
      return nothing();
    }

    @Override
    public <X> @NonNull Maybe<X> cast(final @NonNull Class<X> type) {
      return nothing();
    }

    @Override
    public <U> @NonNull Maybe<U> flatMap(final @NonNull Function<? super T, ? extends Maybe<? extends U>> function) {
      return nothing();
    }

    @Override
    public @NonNull Maybe<T> ifNothing(final @NonNull Runnable nothing) {
      nothing.run();
      return this;
    }

    @Override
    public @NonNull Maybe<T> ifJust(final @NonNull Consumer<? super T> just) {
      return this;
    }

    @Override
    public void forEach(final @NonNull Consumer<? super T> action) {
      // noop
    }

    @Override
    public @NonNull Stream<T> stream() {
      return Stream.empty();
    }

    @Override
    public @NonNull Optional<T> optional() {
      return Optional.empty();
    }

    @Override
    public @NonNull Iterator<T> iterator() {
      return Collections.emptyIterator();
    }

    @Override
    public @NonNull Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.empty();
    }

    @Override
    public @NonNull String toString() {
      return "Maybe.nothing()";
    }

    @Override
    public boolean equals(final @Nullable Object other) {
      return other instanceof Nothing<?>;
    }

    @Override
    public int hashCode() {
      return 0;
    }
  }

  /**
   * A defined value.
   *
   * @param <T> the value type
   */
  final class Just<T> implements Maybe<T> {
    private final T value;

    /* package */ Just(final T value) {
      this.value = value;
    }

    @Override
    public boolean isNothing() {
      return false;
    }

    @Override
    public boolean isJust() {
      return true;
    }

    @Override
    public T orDefault(final @Nullable T defaultValue) {
      return this.value;
    }

    @Override
    public T orGet(final @NonNull Supplier<? extends T> other) {
      return this.value;
    }

    @Override
    public @NonNull T orThrow() throws NoSuchElementException {
      return this.value;
    }

    @Override
    public <X extends Throwable> @NonNull T orThrow(final @NonNull Supplier<X> supplier) {
      return this.value;
    }

    @Override
    public @NonNull Maybe<T> or(final @NonNull Maybe<? extends T> that) {
      return this;
    }

    @Override
    public @NonNull Maybe<T> or(final @NonNull Supplier<? extends Maybe<? extends T>> that) {
      return this;
    }

    @Override
    public @NonNull Maybe<T> filter(final @NonNull Predicate<? super T> predicate) {
      return predicate.test(this.value) ? this : nothing();
    }

    @Override
    public <U> @NonNull Maybe<U> map(final @NonNull Function<? super T, ? extends U> function) {
      return just(function.apply(this.value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> @NonNull Maybe<X> cast(final @NonNull Class<X> type) {
      return type.isInstance(this.value)
        // not necessary to re-wrap, we can just cast
        ? (Maybe<X>) this
        : nothing();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> @NonNull Maybe<U> flatMap(final @NonNull Function<? super T, ? extends Maybe<? extends U>> function) {
      return (Maybe<U>) function.apply(this.value);
    }

    @Override
    public @NonNull Maybe<T> ifNothing(final @NonNull Runnable nothing) {
      return this;
    }

    @Override
    public @NonNull Maybe<T> ifJust(final @NonNull Consumer<? super T> just) {
      just.accept(this.value);
      return this;
    }

    @Override
    public void forEach(final @NonNull Consumer<? super T> action) {
      action.accept(this.value);
    }

    @Override
    public @NonNull Stream<T> stream() {
      return Stream.of(this.value);
    }

    @Override
    public @NonNull Optional<T> optional() {
      return Optional.ofNullable(this.value);
    }

    @Override
    public @NonNull Iterator<T> iterator() {
      return MuIterators.singleton(this.value);
    }

    @Override
    public @NonNull Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(ExaminableProperty.of("value", this.value));
    }

    @Override
    public @NonNull String toString() {
      return "Maybe.just(" + this.value + ')';
    }

    @Override
    public boolean equals(final @Nullable Object other) {
      if(this == other) return true;
      if(other == null || this.getClass() != other.getClass()) return false;
      final Just<?> that = (Just<?>) other;
      return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.value);
    }
  }
}
