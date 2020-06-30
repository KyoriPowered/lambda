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

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;

/*
 * Name is prefixed with 'Mu' to avoid conflict with com.google.common.collect.Maps
 */

/**
 * A collection of utilities for working with maps.
 */
public final class MuMaps {
  private MuMaps() {
  }

  /**
   * Computes the value of {@code key} via {@code function} if {@code map} does not have a mapping.
   *
   * <p>This avoids using {@link Map#computeIfAbsent(Object, Function)} directly to allow using {@code map} inside of {@code function} - some map
   * implementations will throw a {@link ConcurrentModificationException} when modifying the map within {@code function}.</p>
   *
   * @param map the map
   * @param key the key
   * @param function the value supplier
   * @param <K> the key type
   * @param <V> the value type
   * @return the value
   * @see Map#computeIfAbsent(Object, Function)
   */
  public static <K, V> /* @Nullable */ V computeIfAbsent(final @NonNull Map<K, V> map, final /* @Nullable */ K key, final @NonNull Function<K, V> function) {
    V value = map.get(key);
    if(value == null) {
      value = function.apply(key);
      if(value != null) {
        map.put(key, value);
        return value;
      }
    }
    return value;
  }

  /**
   * Computes the value of {@code key} via {@code supplier} if {@code map} does not have a mapping.
   *
   * <p>This avoids using {@link Map#computeIfAbsent(Object, Function)} to allow using {@code map} inside of {@code function} - some map
   * implementations will throw a {@link ConcurrentModificationException} when modifying the map within {@code function}.</p>
   *
   * @param map the map
   * @param key the key
   * @param supplier the value supplier
   * @param <K> the key type
   * @param <V> the value type
   * @return the value
   * @see Map#computeIfAbsent(Object, Function)
   */
  public static <K, V> /* @Nullable */ V supplyIfAbsent(final @NonNull Map<K, V> map, final /* @Nullable */ K key, final @NonNull Supplier<V> supplier) {
    V value = map.get(key);
    if(value == null) {
      value = supplier.get();
      if(value != null) {
        map.put(key, value);
        return value;
      }
    }
    return value;
  }
}
