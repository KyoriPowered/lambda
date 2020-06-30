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
package net.kyori.mu.collection;

import java.util.Collection;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;

/*
 * Name is prefixed with 'Mu' to avoid conflict with java.util.Collections
 */

/**
 * A collection of utilities for working with collections.
 */
public final class MuCollections {
  private MuCollections() {
  }

  /**
   * Reduces a collection to a single element of the inhabiting type depending on the collection's size.
   *
   * <p>If the collection contains zero elements, {@code empty} will be chosen. A singleton collection will have its only
   * element selected. Otherwise, {@code reducer} is used to reduce the collection to a single element of its type.</p>
   *
   * @param collection the collection
   * @param empty the element to return if {@code collection} is empty
   * @param reducer the reducer
   * @param <E> the element type
   * @return an element
   */
  public static <E> /* @Nullable */ E reduce(final @NonNull Collection<? extends E> collection, final /* @Nullable */ E empty, final @NonNull Function<Iterable<? extends E>, ? extends E> reducer) {
    final int size = collection.size();
    if(size == 0) {
      return empty;
    } else if(size == 1) {
      return collection.iterator().next();
    }
    return reducer.apply(collection);
  }
}
