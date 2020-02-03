/*
 * This file is part of mu, licensed under the MIT License.
 *
 * Copyright (c) 2018-2019 KyoriPowered
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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A sync map backed by another map.
 *
 * The map is optimized for two common use cases:
 *
 * <ul>
 *     <li>The entry for the given map is only written once but read many
 *         times, as in a cache that only grows.</li>
 *
 *     <li>Heavy concurrent modification of entries for a disjoint set of
 *         keys.</li>
 * </ul>
 *
 * In both cases, this map significantly reduces lock contention compared
 * to a traditional map paired with a read and write lock.
 *
 * <p>Based on: https://golang.org/src/sync/map.go</p>
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public interface SyncMap<K, V> extends Map<K, V> {
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
     * Creates a sync map, backed by a {@link HashMap} with a provided initial capacity.
     *
     * @param initialCapacity the initial capacity of the hash map
     * @param <K> the key type
     * @param <V> the value type
     * @return a sync map
     */
    static <K, V> @NonNull SyncMap<K, V> hashmap(final int initialCapacity) {
        return of(HashMap<K, ExpungingValue<V>>::new, initialCapacity);
    }

    /**
     * Creates a sync map, backed by the provided {@link Map} implementation with a provided initial capacity.
     *
     * @param function the map creation function
     * @param initialCapacity the map initial capacity
     * @param <K> the key type
     * @param <V> the value type
     * @return a sync map
     */
    static <K, V> @NonNull SyncMap<K, V> of(final @NonNull Function<Integer, Map<K, ExpungingValue<V>>> function, final int initialCapacity) {
        return new SyncMapImpl<>(function, initialCapacity);
    }

    /**
     * The expunging value the backing map wraps for its values.
     *
     * @param <V> the backing value type
     */
    interface ExpungingValue<V> {
        /**
         * Returns the backing value, which may be {@code null} if it has been expunged.
         *
         * @return the backing value if it has not been expunged
         */
        @Nullable
        V getValue();

        /**
         * Returns {@code true} if this value has been expunged.
         *
         * @return whether or not this value has been expunged
         */
        boolean isExpunged();

        /**
         * Sets the backing value, which can be set {@code null}.
         *
         * @param value the backing value
         */
        void setValue(final @Nullable V value);

        /**
         * Sets whether or not the value is expunged.
         *
         * @param expunged whether or not the value is expunged
         */
        void setExpunged(final boolean expunged);
    }
}
