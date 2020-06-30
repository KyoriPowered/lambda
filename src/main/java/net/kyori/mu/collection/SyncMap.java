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

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A concurrent map, internally backed by a non-thread-safe map but carefully
 * managed in a matter such that any changes are thread-safe. Internally, the
 * map is split into a {@code read} and a {@code dirty} map. The read map only
 * satisfies read requests, while the dirty map satisfies all other requests.
 *
 * <p>The map is optimized for two common use cases:</p>
 *
 * <ul>
 *     <li>The entry for the given map is only written once but read many
 *         times, as in a cache that only grows.</li>
 *
 *     <li>Heavy concurrent modification of entries for a disjoint set of
 *         keys.</li>
 * </ul>
 *
 * <p>In both cases, this map significantly reduces lock contention compared
 * to a traditional map paired with a read and write lock, along with maps
 * with an exclusive lock (such as with {@link Collections#synchronizedMap(Map)}.</p>
 *
 * <p>Null values are not accepted. Null keys are supported if the backing collection
 * supports them.</p>
 *
 * <p>Based on: https://golang.org/src/sync/map.go</p>
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public interface SyncMap<K, V> extends ConcurrentMap<K, V> {
  /**
   * Creates a sync map, backed by a {@link HashMap}.
   *
   * @param <K> the key type
   * @param <V> the value type
   * @return a sync map
   */
  static <K, V> @NonNull SyncMap<K, V> hashmap() {
    return of(HashMap<K, ExpungingValue<V>>::new, 16);
  }

  /**
   * Creates a sync map, backed by a {@link HashMap} with a provided initial
   * capacity.
   *
   * @param initialCapacity the initial capacity of the hash map
   * @param <K>             the key type
   * @param <V>             the value type
   * @return a sync map
   */
  static <K, V> @NonNull SyncMap<K, V> hashmap(final int initialCapacity) {
    return of(HashMap<K, ExpungingValue<V>>::new, initialCapacity);
  }

  /**
   * Creates a mutable set view of a sync map, backed by a {@link HashMap}.
   *
   * @param <K> the key type
   * @return a mutable set view of a sync map
   */
  static <K> @NonNull Set<K> hashset() {
    return setOf(HashMap<K, ExpungingValue<Boolean>>::new, 16);
  }

  /**
   * Creates a mutable set view of a sync map, backed by a {@link HashMap} with
   * a provided initial capacity.
   *
   * @param initialCapacity the initial capacity of the hash map
   * @param <K>             the key type
   * @return a mutable set view of a sync map
   */
  static <K> @NonNull Set<K> hashset(final int initialCapacity) {
    return setOf(HashMap<K, ExpungingValue<Boolean>>::new, initialCapacity);
  }

  /**
   * Creates a sync map, backed by the provided {@link Map} implementation
   * with a provided initial capacity.
   *
   * @param function        the map creation function
   * @param initialCapacity the map initial capacity
   * @param <K>             the key type
   * @param <V>             the value type
   * @return a sync map
   */
  static <K, V> @NonNull SyncMap<K, V> of(final @NonNull Function<Integer, Map<K, ExpungingValue<V>>> function, final int initialCapacity) {
    return new SyncMapImpl<>(function, initialCapacity);
  }

  /**
   * Creates a mutable set view of a sync map, backed by the provided
   * {@link Map} implementation with a provided initial capacity.
   *
   * @param function        the map creation function
   * @param initialCapacity the map initial capacity
   * @param <K>             they key type
   * @return a mutable set view of a sync map
   */
  static <K> Set<K> setOf(final @NonNull Function<Integer, Map<K, ExpungingValue<Boolean>>> function, final int initialCapacity) {
    return Collections.newSetFromMap(new SyncMapImpl<>(function, initialCapacity));
  }

  /**
   * {@inheritDoc}
   *
   * Iterations over a sync map are thread-safe, and the keys iterated over will not change for a single iteration
   * attempt, however they may not necessarily reflect the state of the map at the time the iterator was created.
   *
   * <p>Performance note: if entries have been appended to the map, iterating over the entry set will automatically
   * promote them to the read map.</p>
   */
  @Override
  Set<Entry<K, V>> entrySet();

  /**
   * {@inheritDoc}
   *
   * This implementation is {@code O(n)} in nature due to the need to check for any expunged entries. Likewise, as
   * with other concurrent collections, the value obtained by this method may be out of date by the time this method
   * returns.
   *
   * @return the size of all the mappings contained in this map
   */
  @Override
  int size();

  /**
   * {@inheritDoc}
   *
   * This method clears the map by resetting the internal state to a state similar to as if a new map had been created.
   * If there are concurrent iterations in progress, they will reflect the state of the map prior to being cleared.
   */
  @Override
  void clear();

  /**
   * The expunging value the backing map wraps for its values.
   *
   * @param <V> the backing value type
   */
  interface ExpungingValue<V> {
    /**
     * Returns the backing element, which may be {@code null} if it has been expunged.
     *
     * @return the backing element if it has not been expunged
     */
    @Nullable
    V get();

    /**
     * Attempts to place the entry in the map if it is absent.
     *
     * @param value the value to place in the map
     * @return a {@link Map.Entry} with false key and null value if the value was expunged, a true key and the
     * previous value in the map otherwise
     */
    Map.Entry<Boolean, V> putIfAbsent(@NonNull V value);

    /**
     * Returns {@code true} if this element has been expunged.
     *
     * @return whether or not this element has been expunged
     */
    boolean isExpunged();

    /**
     * Returns {@code true} if this element has a value (it is neither expunged nor {@code null}.
     *
     * @return whether or not this element has a value
     */
    boolean exists();

    /**
     * Sets the backing element.
     *
     * @param element the backing element
     * @return the previous element stored, or {@code null} if the entry had been expunged
     */
    @Nullable
    V set(final @NonNull V element);

    /**
     * Tries to replace the backing element, which can be set to {@code null}. This operation has no effect
     * if the entry was expunged.
     *
     * @param expected the element to check for
     * @param newElement the new element to be stored
     * @return {@code true} if successful, {@code false} otherwise
     */
    boolean replace(final @NonNull Object expected, final @Nullable V newElement);

    /**
     * Clears the entry stored in this value. Has no effect if {@code null} is stored in the map or
     * the entry was expunged.
     */
    @Nullable
    V clear();

    /**
     * Tries to set the backing element. If the entry is expunged, this operation
     * will fail.
     *
     * @param element the new element
     * @return {@code true} if the entry was not expunged, {@code false} otherwise
     */
    boolean trySet(final @NonNull V element);

    /**
     * Tries to mark the item as expunged, if its value is {@code null}.
     *
     * @return whether or not the item has been expunged
     */
    boolean tryMarkExpunged();

    /**
     * Tries to set the backing element, which can be set to {@code null}, if the entry was expunged.
     *
     * @param element the new element
     * @return {@code true} if the entry was unexpunged, {@code false} otherwise
     */
    boolean tryUnexpungeAndSet(final @Nullable V element);
  }
}
