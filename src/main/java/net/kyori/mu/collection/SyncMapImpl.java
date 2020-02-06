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
import java.util.NoSuchElementException;
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
        int size = 0;
        if(this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && this.dirty != null) {
                    this.promoteLocked();
                }
            }
        }
        for(final Map.Entry<? extends K, ? extends ExpungingValue<V>> otherEntry : this.read.entrySet()) {
            final ExpungingValue<V> next = otherEntry.getValue();
            if (next.isExpunged() || next.getValue() == null) continue;
            size++;
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean containsKey(final Object key) {
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
        if(absent) return false;
        return !entry.isExpunged() && entry.getValue() != null;
    }

    @Override
    public boolean containsValue(final Object value) {
        if(this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && this.dirty != null) {
                    this.promoteLocked();
                }
            }
        }
        for(final Map.Entry<? extends K, ? extends ExpungingValue<V>> entry : this.read.entrySet()) {
            final ExpungingValue<V> next = entry.getValue();
            if(next.isExpunged() || next.getValue() == null) continue;
            if(Objects.equals(next.getValue(), value)) {
                return true;
            }
        }
        return false;
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
        V previous = entry != null ? entry.getValue() : null;
        if(entry == null || !this.tryPut(entry, value)) {
            synchronized(this.lock) {
                if((entry = this.read.get(key)) != null) {
                    if(this.unexpungedLocked(entry) && this.dirty != null) {
                        final ExpungingValue<V> old = this.dirty.put(key, entry);
                        if(old != null) previous = old.getValue();
                    } else {
                        previous = entry.getValue();
                    }
                    entry.setValue(value);
                } else {
                    entry = this.dirty != null ? this.dirty.get(key) : null;
                    if(entry != null) {
                        previous = entry.getValue();
                        entry.setValue(value);
                    } else {
                        if(!this.readAmended) {
                            this.dirtyLocked();
                            this.readAmended = true;
                        }
                        if(this.dirty != null) {
                            final ExpungingValue<V> old = this.dirty.put(key, new ExpungingValueImpl<>(value, false));
                            if(old != null) previous = old.getValue();
                        }
                    }
                }
            }
        }
        return previous;
    }

    @Override
    public V remove(final Object key) {
        ExpungingValue<V> entry = this.read.get(key);
        V previous = entry != null ? entry.getValue() : null;
        boolean absent = entry == null;
        if(absent && this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && (absent = (entry = this.read.get(key)) == null) && this.dirty != null) {
                    final ExpungingValue<V> old = this.dirty.remove(key);
                    if(old != null) previous = old.getValue();
                }
            }
        }
        if(!absent) this.tryDelete(entry);
        return previous;
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        ExpungingValue<V> entry = this.read.get(key);
        boolean absent = entry == null;
        if(absent && this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && (absent = (entry = this.read.get(key)) == null) && this.dirty != null) {
                    absent = (entry = this.dirty.get(key)) == null;
                    if(!absent && !entry.isExpunged() && entry.getValue() != null && Objects.equals(entry.getValue(), value)) {
                        this.dirty.remove(key);
                        return true;
                    }
                }
            }
        }
        if(!absent) return this.tryDelete(entry, value);
        return false;
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> other) {
        for(final Map.Entry<? extends K, ? extends V> otherEntry : other.entrySet()) {
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

    private boolean removeValue(final Object value) {
        if(this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && this.dirty != null) {
                    this.promoteLocked();
                }
            }
        }
        for(final Map.Entry<? extends K, ? extends ExpungingValue<V>> otherEntry : this.read.entrySet()) {
            final ExpungingValue<V> next = otherEntry.getValue();
            if(next.isExpunged() || next.getValue() == null) continue;
            if(Objects.equals(next.getValue(), value)) {
                return this.remove(otherEntry.getKey(), value);
            }
        }
        return false;
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
        public V setValue(final V value) {
            return SyncMapImpl.this.put(this.key, value);
        }

        @Override
        public String toString() {
            return "SyncMapImpl.MapEntry{key=" + this.getKey() + ", value=" + this.getValue() + "}";
        }

        @Override
        public boolean equals(final Object other) {
           if(this == other) return true;
           if(!(other instanceof Map.Entry)) return false;
           final Map.Entry<?, ?> that = (Map.Entry<?, ?>) other;
           return Objects.equals(this.getKey(), that.getKey())
                   && Objects.equals(this.getValue(), that.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.getKey(), this.getValue());
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
            return new KeyIterator(SyncMapImpl.this.read.entrySet().iterator());
        }

        @Override
        public Spliterator<K> spliterator() {
            return new Spliterators.AbstractSpliterator<K>(this.size(), Spliterator.IMMUTABLE) {
                final Iterator<K> iterator = KeySet.this.iterator();

                public boolean tryAdvance(final Consumer<? super K> action) {
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
        public void forEach(final Consumer<? super K> action) {
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
                    if(SyncMapImpl.this.readAmended && SyncMapImpl.this.dirty != null) {
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
        public boolean remove(final Object value) {
            return SyncMapImpl.this.removeValue(value);
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

        @Override
        public Spliterator<V> spliterator() {
            return new Spliterators.AbstractSpliterator<V>(this.size(), Spliterator.IMMUTABLE) {
                final Iterator<V> iterator = ValueCollection.this.iterator();

                public boolean tryAdvance(final Consumer<? super V> action) {
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
        public void forEach(final Consumer<? super V> action) {
            for(final Map.Entry<K, ExpungingValue<V>> entry : SyncMapImpl.this.read.entrySet()) {
                final ExpungingValue<V> value = entry.getValue();
                if(!value.isExpunged() && value.getValue() != null) {
                    action.accept(value.getValue());
                }
            }
        }

        private void promote() {
            if(SyncMapImpl.this.readAmended) {
                synchronized(SyncMapImpl.this.lock) {
                    if(SyncMapImpl.this.readAmended && SyncMapImpl.this.dirty != null) {
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
        public boolean contains(final Object entry) {
            if (!(entry instanceof Map.Entry)) return false;
            final Map.Entry<?, ?> mapEntry = (Entry<?, ?>) entry;
            final V value = SyncMapImpl.this.get(mapEntry.getKey());
            return value != null && Objects.equals(mapEntry.getValue(), value);
        }

        @Override
        public boolean remove(final Object entry) {
            if (!(entry instanceof Map.Entry)) return false;
            final Map.Entry<?, ?> mapEntry = (Entry<?, ?>) entry;
            return SyncMapImpl.this.remove(mapEntry.getKey()) != null;
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

                public boolean tryAdvance(final Consumer<? super Map.Entry<K, V>> action) {
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
                    if(SyncMapImpl.this.readAmended && SyncMapImpl.this.dirty != null) {
                        SyncMapImpl.this.promoteLocked();
                    }
                }
            }
        }
    }

    private class KeyIterator implements Iterator<K> {
        private final Iterator<Map.Entry<K, ExpungingValue<V>>> backingIterator;
        private Map.Entry<K, ExpungingValue<V>> next;
        private Map.Entry<K, ExpungingValue<V>> current;

        private KeyIterator(final Iterator<Map.Entry<K, ExpungingValue<V>>> backingIterator) {
            this.backingIterator = backingIterator;
            this.current = this.next = null;
            if(this.backingIterator.hasNext()) {
                while(this.next == null && this.backingIterator.hasNext()) {
                    final Map.Entry<K, ExpungingValue<V>> entry = this.backingIterator.next();
                    final ExpungingValue<V> expungingValue = entry.getValue();
                    if(expungingValue.isExpunged() || expungingValue.getValue() == null) continue;
                    this.next = entry;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public K next() {
            final Map.Entry<K, ExpungingValue<V>> entry = this.next;
            this.current = this.next = null;
            if(entry == null) throw new NoSuchElementException();
            this.current = entry;
            while(this.next == null && this.backingIterator.hasNext()) {
                this.next = this.backingIterator.next();
                final ExpungingValue<V> expungingValue = this.next.getValue();
                if(expungingValue.isExpunged() || expungingValue.getValue() == null) {
                    this.next = null;
                }
            }
            return entry.getKey();
        }

        @Override
        public void remove() {
            if(this.current == null) return;
            SyncMapImpl.this.remove(this.current.getKey());
        }

        @Override
        public void forEachRemaining(final Consumer<? super K> action) {
            if(this.next != null) action.accept(this.next.getKey());
            this.backingIterator.forEachRemaining(entry -> {
                final ExpungingValue<V> expungingValue = entry.getValue();
                if(!expungingValue.isExpunged() && expungingValue.getValue() != null) {
                    action.accept(entry.getKey());
                }
            });
        }
    }

    private class ValueIterator implements Iterator<V> {
        private final Iterator<Map.Entry<K, ExpungingValue<V>>> backingIterator;
        private Map.Entry<K, ExpungingValue<V>> next;
        private Map.Entry<K, ExpungingValue<V>> current;

        private ValueIterator(final Iterator<Map.Entry<K, ExpungingValue<V>>> backingIterator) {
            this.backingIterator = backingIterator;
            this.current = this.next = null;
            if(this.backingIterator.hasNext()) {
                while(this.next == null && this.backingIterator.hasNext()) {
                    final Map.Entry<K, ExpungingValue<V>> entry = this.backingIterator.next();
                    final ExpungingValue<V> expungingValue = entry.getValue();
                    if(expungingValue.isExpunged() || expungingValue.getValue() == null) continue;
                    this.next = entry;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public V next() {
            final Map.Entry<K, ExpungingValue<V>> entry = this.next;
            this.current = this.next = null;
            if(entry == null) throw new NoSuchElementException();
            final ExpungingValue<V> value = entry.getValue();
            this.current = entry;
            while(this.next == null && this.backingIterator.hasNext()) {
                this.next = this.backingIterator.next();
                final ExpungingValue<V> expungingValue = this.next.getValue();
                if(expungingValue.isExpunged() || expungingValue.getValue() == null) {
                    this.next = null;
                }
            }
            return value.getValue();
        }

        @Override
        public void remove() {
            if(this.current == null) return;
            SyncMapImpl.this.remove(this.current.getKey());
        }

        @Override
        public void forEachRemaining(final Consumer<? super V> action) {
            if(this.next != null) action.accept(this.next.getValue().getValue());
            this.backingIterator.forEachRemaining(entry -> {
                final ExpungingValue<V> expungingValue = entry.getValue();
                if(!expungingValue.isExpunged() && expungingValue.getValue() != null) {
                    action.accept(expungingValue.getValue());
                }
            });
        }
    }

    private class EntryIterator implements Iterator<Map.Entry<K, V>> {
        private final Iterator<Map.Entry<K, ExpungingValue<V>>> backingIterator;
        private Map.Entry<K, ExpungingValue<V>> next;
        private Map.Entry<K, ExpungingValue<V>> current;

        private EntryIterator(final Iterator<Map.Entry<K, ExpungingValue<V>>> backingIterator) {
            this.backingIterator = backingIterator;
            this.current = this.next = null;
            if(this.backingIterator.hasNext()) {
                while(this.next == null && this.backingIterator.hasNext()) {
                    final Map.Entry<K, ExpungingValue<V>> entry = this.backingIterator.next();
                    final ExpungingValue<V> expungingValue = entry.getValue();
                    if(expungingValue.isExpunged() || expungingValue.getValue() == null) continue;
                    this.next = entry;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public Map.Entry<K, V> next() {
            final Map.Entry<K, ExpungingValue<V>> entry = this.next;
            this.current = this.next = null;
            if(entry == null) throw new NoSuchElementException();
            this.current = entry;
            while(this.next == null && this.backingIterator.hasNext()) {
                this.next = this.backingIterator.next();
                final ExpungingValue<V> expungingValue = this.next.getValue();
                if(expungingValue.isExpunged() || expungingValue.getValue() == null) {
                    this.next = null;
                }
            }
            return new MapEntry(entry);
        }

        @Override
        public void remove() {
            if(this.current == null) return;
            SyncMapImpl.this.remove(this.current.getKey());
        }

        @Override
        public void forEachRemaining(final Consumer<? super Map.Entry<K, V>> action) {
            if(this.next != null) action.accept(new MapEntry(this.next));
            this.backingIterator.forEachRemaining(entry -> {
                final ExpungingValue<V> expungingValue = entry.getValue();
                if(!expungingValue.isExpunged() && expungingValue.getValue() != null) {
                    action.accept(new MapEntry(entry));
                }
            });
        }
    }
}
