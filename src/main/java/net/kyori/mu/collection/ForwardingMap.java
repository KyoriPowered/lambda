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
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A map which forwards all its method calls to another map.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public interface ForwardingMap<K, V> extends Map<K, V> {
  /**
   * Gets the forwarded map that methods are forwarded to.
   *
   * @return the forwarded map
   */
  @NonNull Map<K, V> map();

  @Override
  default int size() {
    return this.map().size();
  }

  @Override
  default boolean isEmpty() {
    return this.map().isEmpty();
  }

  @Override
  default boolean containsKey(final Object key) {
    return this.map().containsKey(key);
  }

  @Override
  default boolean containsValue(final Object value) {
    return this.map().containsValue(value);
  }

  @Override
  default V get(final Object key) {
    return this.map().get(key);
  }

  @Override
  default V put(final K key, final V value) {
    return this.map().put(key, value);
  }

  @Override
  default V remove(final Object key) {
    return this.map().remove(key);
  }

  @Override
  default void putAll(final @NonNull Map<? extends K, ? extends V> that) {
    this.map().putAll(that);
  }

  @Override
  default void clear() {
    this.map().clear();
  }

  @Override
  default @NonNull Set<K> keySet() {
    return this.map().keySet();
  }

  @Override
  default @NonNull Collection<V> values() {
    return this.map().values();
  }

  @Override
  default @NonNull Set<Entry<K, V>> entrySet() {
    return this.map().entrySet();
  }
}
