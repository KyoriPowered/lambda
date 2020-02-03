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

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;

/* package */ final class SyncMapImpl<K, V> implements SyncMap<K, V> {
    private final Object lock = new Object();
    private volatile Map<K, SyncMap.ExpungingValue<V>> read;
    private volatile boolean readAmended;
    private Function<Integer, Map<K, SyncMap.ExpungingValue<V>>> function;
    private Map<K, SyncMap.ExpungingValue<V>> dirty;
    private int readMisses;
    private KeySet keySet;
    private ValueCollection valueCollection;
    private EntrySet entrySet;

    /* package */ SyncMapImpl(final Function<Integer, Map<K, SyncMap.ExpungingValue<V>>> function, final int initialCapacity) {
        this.function = function;
        this.read = function.apply(initialCapacity);
    }

    @Override
    public int size() {
        int size = this.read.size();
        if(this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && this.dirty != null) {
                    size = this.dirty.size();
                    this.missLocked();
                }
            }
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        boolean empty = this.read.isEmpty();
        if(this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && this.dirty != null) {
                    empty = this.dirty.isEmpty();
                    this.missLocked();
                }
            }
        }
        return empty;
    }

    @Override
    public boolean containsKey(final Object key) {
        boolean contains = this.read.containsKey(key);
        if(!contains && this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && this.dirty != null) {
                    contains = this.dirty.containsKey(key);
                    this.missLocked();
                }
            }
        }
        return contains;
    }

    @Override
    public boolean containsValue(final Object value) {
        boolean contains = this.read.containsValue(value);
        if(!contains && this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && this.dirty != null) {
                    contains = this.dirty.containsValue(value);
                    this.missLocked();
                }
            }
        }
        return contains;
    }

    @Override
    public V get(final Object key) {
        ExpungingValue<V> entry = this.read.get(key);
        boolean absent = entry == null;
        if(absent && this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && (absent = (entry = this.read.get(key)) == null) && this.dirty != null) {
                    absent = ((entry = this.dirty.get(key)) == null);
                    this.missLocked();
                }
            }
        }
        if(absent) return null;
        return this.getCached(entry);
    }

    @Override
    public V put(final K key, final V value) {
        ExpungingValue<V> entry = this.read.get(key);
        ExpungingValue<V> previous = entry;
        if(entry == null || !this.tryPut(entry, value)) {
            synchronized(this.lock) {
                if((entry = this.read.get(key)) != null) {
                    if(this.unexpungedLocked(entry) && this.dirty != null) {
                        previous = this.dirty.put(key, entry);
                    }
                    entry.setValue(value);
                } else {
                    entry = this.dirty != null ? this.dirty.get(key) : null;
                    if(entry != null) {
                        entry.setValue(value);
                    } else {
                        if(!this.readAmended) {
                            this.dirtyLocked();
                            this.readAmended = true;
                        }
                        if(this.dirty != null) {
                            previous = this.dirty.put(key, new ExpungingValueImpl<>(value, false));
                        }
                    }
                }
            }
        }
        return previous != null ? previous.getValue() : null;
    }

    @Override
    public V remove(final Object key) {
        ExpungingValue<V> entry = this.read.get(key);
        ExpungingValue<V> previous = entry;
        boolean absent = entry == null;
        if(absent && this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && (absent = (entry = this.read.get(key)) == null) && this.dirty != null) {
                    previous = this.dirty.remove(key);
                }
            }
        }
        if(!absent) this.tryDelete(entry);
        return previous != null ? previous.getValue() : null;
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        ExpungingValue<V> entry = this.read.get(key);
        boolean absent = entry == null;
        if(absent && this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && (absent = (entry = this.read.get(key)) == null) && this.dirty != null) {
                    return this.dirty.remove(key, value);
                }
            }
        }
        if(!absent) return this.tryDelete(entry, value);
        return false;
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> other) {
        for (final Map.Entry<? extends K, ? extends V> otherEntry : other.entrySet()) {
            ExpungingValue<V> entry = this.read.get(otherEntry.getKey());
            if(entry != null && this.tryPut(entry, otherEntry.getValue())) continue;
            synchronized(this.lock) {
                if((entry = this.read.get(otherEntry.getKey())) != null) {
                    if(this.unexpungedLocked(entry) && this.dirty != null) {
                        this.dirty.put(otherEntry.getKey(), entry);
                    }
                    entry.setValue(otherEntry.getValue());
                } else {
                    entry = this.dirty != null ? this.dirty.get(otherEntry.getKey()) : null;
                    if(entry != null) {
                        entry.setValue(otherEntry.getValue());
                    } else {
                        if(!this.readAmended) {
                            this.dirtyLocked();
                            this.readAmended = true;
                        }
                        if(this.dirty != null) {
                            this.dirty.put(otherEntry.getKey(), new ExpungingValueImpl<>(otherEntry.getValue(), false));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void clear() {
        this.read.clear();
        synchronized(this.lock) {
            this.dirty = null;
            this.readMisses = 0;
            this.readAmended = false;
        }
    }

    @Override
    @NonNull
    public Set<K> keySet() {
        if(this.keySet != null) return this.keySet;
        return this.keySet = new KeySet();
    }

    @Override
    @NonNull
    public Collection<V> values() {
        if(this.valueCollection != null) return this.valueCollection;
        return this.valueCollection = new ValueCollection();
    }

    @Override
    @NonNull
    public Set<Entry<K, V>> entrySet() {
        if(this.entrySet != null) return this.entrySet;
        return this.entrySet = new EntrySet();
    }

    private V getCached(final ExpungingValue<V> entry) {
        if(entry.isExpunged()) return null;
        return entry.getValue();
    }

    private void tryDelete(final ExpungingValue<V> entry) {
        if(entry.isExpunged() || entry.getValue() == null) return;
        entry.setValue(null);
    }

    private boolean tryDelete(final ExpungingValue<V> entry, final Object compare) {
        if(entry.isExpunged() || entry.getValue() == null) return false;
        if(!Objects.equals(entry.getValue(), compare)) return false;
        entry.setValue(null);
        return true;
    }

    private boolean tryPut(final ExpungingValue<V> entry, final V value) {
        if(entry.isExpunged()) return false;
        entry.setValue(value);
        return true;
    }

    private void promoteLocked() {
        if(this.dirty != null) this.read = this.dirty;
        this.dirty = null;
        this.readMisses = 0;
        this.readAmended = false;
    }

    private void missLocked() {
        this.readMisses++;
        int length = this.dirty != null ? this.dirty.size() : 0;
        if(this.readMisses > length) {
            this.promoteLocked();
        }
    }

    private void dirtyLocked() {
        if(this.dirty == null) {
            this.dirty = this.function.apply(this.read.size());
            for (final Map.Entry<K, ExpungingValue<V>> entry : this.read.entrySet()) {
                if(!this.expungeLocked(entry.getValue())) this.dirty.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private boolean expungeLocked(final ExpungingValue<V> entry) {
        if(!entry.isExpunged() && entry.getValue() == null) entry.setExpunged(true);
        return entry.isExpunged();
    }

    private boolean unexpungedLocked(final ExpungingValue<V> entry) {
        if(entry.isExpunged()) {
            entry.setValue(null);
            entry.setExpunged(false);
        }
        return false;
    }

    private static class ExpungingValueImpl<V> implements SyncMap.ExpungingValue<V> {
        private volatile V value;
        private volatile boolean expunged;

        private ExpungingValueImpl(final V value, final boolean expunged) {
            this.value = value;
            this.expunged = expunged;
        }

        @Nullable
        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public boolean isExpunged() {
            return this.expunged;
        }

        @Override
        public void setValue(final @Nullable V value) {
            this.value = value;
        }

        @Override
        public void setExpunged(final boolean expunged) {
            this.expunged = expunged;
        }
    }

    private class MapEntry implements Map.Entry<K, V> {
        private final K key;

        private MapEntry(final Map.Entry<K, ExpungingValue<V>> entry) {
            this.key = entry.getKey();
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return SyncMapImpl.this.get(this.key);
        }

        @Override
        public V setValue(V value) {
            return SyncMapImpl.this.put(this.key, value);
        }
    }

    private class KeySet extends AbstractSet<K> {
        @Override
        public int size() {
            return SyncMapImpl.this.size();
        }

        @Override
        public boolean contains(final Object key) {
            return SyncMapImpl.this.containsKey(key);
        }

        @Override
        public boolean remove(final Object key) {
            return SyncMapImpl.this.remove(key) != null;
        }

        @Override
        public boolean addAll(final @NonNull Collection<? extends K> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            SyncMapImpl.this.clear();
        }

        @NonNull
        @Override
        public Iterator<K> iterator() {
            this.promote();
            return new KeyIterator(SyncMapImpl.this.read.keySet().iterator());
        }

        @Override
        public Spliterator<K> spliterator() {
            this.promote();
            return SyncMapImpl.this.read.keySet().spliterator();
        }

        @Override
        public void forEach(final Consumer<? super K> action) {
            this.promote();
            for(final Map.Entry<K, ExpungingValue<V>> entry : SyncMapImpl.this.read.entrySet()) {
                final ExpungingValue<V> value = entry.getValue();
                if(!value.isExpunged() && value.getValue() != null) {
                    action.accept(entry.getKey());
                }
            }
        }

        private void promote() {
            if(SyncMapImpl.this.readAmended) {
                synchronized(SyncMapImpl.this.lock) {
                    if(SyncMapImpl.this.readAmended) {
                        SyncMapImpl.this.promoteLocked();
                    }
                }
            }
        }
    }

    private class ValueCollection extends AbstractCollection<V> {
        @Override
        public int size() {
            return SyncMapImpl.this.size();
        }

        @Override
        public boolean contains(final Object value) {
            return SyncMapImpl.this.containsValue(value);
        }

        @Override
        public boolean addAll(final @NonNull Collection<? extends V> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            SyncMapImpl.this.clear();
        }

        @NonNull
        @Override
        public Iterator<V> iterator() {
            this.promote();
            return new ValueIterator(SyncMapImpl.this.read.entrySet().iterator());
        }

        private void promote() {
            if(SyncMapImpl.this.readAmended) {
                synchronized(SyncMapImpl.this.lock) {
                    if(SyncMapImpl.this.readAmended) {
                        SyncMapImpl.this.promoteLocked();
                    }
                }
            }
        }
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        @Override
        public int size() {
            return SyncMapImpl.this.size();
        }

        @Override
        public boolean contains(final Object key) {
            return SyncMapImpl.this.containsKey(key);
        }

        @Override
        public boolean remove(final Object key) {
            return SyncMapImpl.this.remove(key) != null;
        }

        @Override
        public boolean addAll(final @NonNull Collection<? extends Map.Entry<K, V>> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            SyncMapImpl.this.clear();
        }

        @NonNull
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            this.promote();
            return new EntryIterator(SyncMapImpl.this.read.entrySet().iterator());
        }

        @Override
        public Spliterator<Map.Entry<K, V>> spliterator() {
            return new Spliterators.AbstractSpliterator<Map.Entry<K, V>>(this.size(), Spliterator.IMMUTABLE) {
                final Iterator<Map.Entry<K, V>> iterator = EntrySet.this.iterator();

                public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> action) {
                    if (this.iterator.hasNext()) {
                        action.accept(this.iterator.next());
                        return true;
                    } else {
                        return false;
                    }
                }
            };
        }

        @Override
        public void forEach(final Consumer<? super Map.Entry<K, V>> action) {
            this.promote();
            for(final Map.Entry<K, ExpungingValue<V>> entry : SyncMapImpl.this.read.entrySet()) {
                final ExpungingValue<V> value = entry.getValue();
                if(!value.isExpunged() && value.getValue() != null) {
                    action.accept(new MapEntry(entry));
                }
            }
        }

        private void promote() {
            if(SyncMapImpl.this.readAmended) {
                synchronized(SyncMapImpl.this.lock) {
                    if(SyncMapImpl.this.readAmended) {
                        SyncMapImpl.this.promoteLocked();
                    }
                }
            }
        }
    }

    private class KeyIterator implements Iterator<K> {
        private final Iterator<K> backingIterator;
        private K previous;

        private KeyIterator(final Iterator<K> backingIterator) {
            this.backingIterator = backingIterator;
        }

        @Override
        public boolean hasNext() {
            return this.backingIterator.hasNext();
        }

        @Override
        public K next() {
            return this.previous = this.backingIterator.next();
        }

        @Override
        public void remove() {
            if(this.previous == null) return;
            SyncMapImpl.this.remove(this.previous);
        }

        @Override
        public void forEachRemaining(final Consumer<? super K> action) {
            this.backingIterator.forEachRemaining(action);
        }
    }

    private class ValueIterator implements Iterator<V> {
        private final Iterator<Map.Entry<K, ExpungingValue<V>>> backingIterator;
        private Map.Entry<K, ExpungingValue<V>> previous;

        private ValueIterator(final Iterator<Map.Entry<K, ExpungingValue<V>>> backingIterator) {
            this.backingIterator = backingIterator;
        }

        @Override
        public boolean hasNext() {
            return this.backingIterator.hasNext();
        }

        @Override
        public V next() {
            this.previous = this.backingIterator.next();
            return this.previous.getValue().getValue();
        }

        @Override
        public void remove() {
            if(this.previous == null) return;
            SyncMapImpl.this.remove(this.previous.getKey(), this.previous.getValue());
        }

        @Override
        public void forEachRemaining(final Consumer<? super V> action) {
            this.backingIterator.forEachRemaining(entry -> action.accept(entry.getValue().getValue()));
        }
    }

    private class EntryIterator implements Iterator<Map.Entry<K, V>> {
        private final Iterator<Map.Entry<K, ExpungingValue<V>>> backingIterator;
        private Map.Entry<K, ExpungingValue<V>> previous;

        private EntryIterator(final Iterator<Map.Entry<K, ExpungingValue<V>>> backingIterator) {
            this.backingIterator = backingIterator;
        }

        @Override
        public boolean hasNext() {
            return this.backingIterator.hasNext();
        }

        @Override
        public Map.Entry<K, V> next() {
            this.previous = this.backingIterator.next();
            return new MapEntry(this.previous);
        }

        @Override
        public void remove() {
            if(this.previous == null) return;
            SyncMapImpl.this.remove(this.previous.getKey(), this.previous.getValue());
        }

        @Override
        public void forEachRemaining(final Consumer<? super Map.Entry<K, V>> action) {
            this.backingIterator.forEachRemaining(value -> action.accept(new MapEntry(value)));
        }
    }
}
