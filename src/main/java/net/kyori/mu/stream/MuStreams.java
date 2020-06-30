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
package net.kyori.mu.stream;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.checkerframework.checker.nullness.qual.NonNull;

/*
 * Name is prefixed with 'Mu' to avoid conflict with com.google.common.collect.Streams
 */

/**
 * A collection of utilities for working with streams.
 */
public final class MuStreams {
  private MuStreams() {
  }

  /**
   * Creates a stream whose elements are all the elements of the first stream followed by all the elements of the second stream, and so on.
   *
   * @param streams the streams
   * @param <T> the element type
   * @return the concatenated stream
   * @see Stream#concat(Stream, Stream)
   */
  @SafeVarargs
  @SuppressWarnings({"unchecked", "varargs"})
  public static <T> @NonNull Stream<T> concat(final Stream<? extends T>... streams) {
    return (Stream<T>) Stream.of(streams).reduce(Stream.empty(), Stream::concat);
  }

  /**
   * Creates a stream.
   *
   * @param iterable the iterable
   * @param <E> the element type
   * @return a stream
   */
  public static <E> @NonNull Stream<E> of(final @NonNull Iterable<E> iterable) {
    if(iterable instanceof Collection<?>) {
      return ((Collection<E>) iterable).stream();
    }
    return StreamSupport.stream(iterable.spliterator(), false);
  }

  /**
   * Creates a stream from an optional.
   *
   * @param optional the optional
   * @param <T> the type
   * @return a stream
   */
  public static <T> @NonNull Stream<T> of(final @NonNull Optional<T> optional) {
    return optional.map(Stream::of).orElse(Stream.empty());
  }

  /**
   * Filters and maps {@code stream} to only contain elements of type {@code type}.
   *
   * @param stream the stream
   * @param type the type
   * @param <T> the type
   * @return a stream
   */
  public static <T> @NonNull Stream<T> cast(final @NonNull Stream<?> stream, final @NonNull Class<T> type) {
    return stream.filter(type::isInstance).map(type::cast);
  }
}
