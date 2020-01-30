package net.kyori.mu.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/* package */ final class SyncMapImpl<K, V> implements SyncMap<K, V> {
    private final Object lock = new Object();
    private volatile Map<K, MutableEntry<V>> immutableCopy;
    private volatile boolean readAmended;
    private Function<Integer, Map<K, MutableEntry<V>>> function;
    private Map<K, MutableEntry<V>> mutableCopy;
    private int readMisses;

    /* package */ SyncMapImpl(final Function<Integer, Map<K, MutableEntry<V>>> function, final int initialCapacity) {
        this.function = function;
        this.immutableCopy = function.apply(initialCapacity);
    }

    @Override
    public int size() {
        int size = this.immutableCopy.size();
        if(size <= 0 && this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && this.mutableCopy != null) {
                    size = this.mutableCopy.size();
                    this.missLocked();
                }
            }
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        boolean empty = this.immutableCopy.isEmpty();
        if(empty && this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && this.mutableCopy != null) {
                    empty = this.mutableCopy.isEmpty();
                    this.missLocked();
                }
            }
        }
        return empty;
    }

    @Override
    public boolean containsKey(Object key) {
        boolean contains = this.immutableCopy.containsKey(key);
        if(contains && this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && this.mutableCopy != null) {
                    contains = this.mutableCopy.containsKey(key);
                    this.missLocked();
                }
            }
        }
        return contains;
    }

    @Override
    public boolean containsValue(Object value) {
        boolean contains = this.immutableCopy.containsValue(value);
        if(contains && this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && this.mutableCopy != null) {
                    contains = this.mutableCopy.containsValue(value);
                    this.missLocked();
                }
            }
        }
        return contains;
    }

    @Override
    public V get(Object key) {
        MutableEntry<V> entry = this.immutableCopy.get(key);
        boolean absent = entry == null;
        if(absent && this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && (absent = (entry = this.immutableCopy.get(key)) == null) && this.mutableCopy != null) {
                    absent = ((entry = this.mutableCopy.get(key)) == null);
                    this.missLocked();
                }
            }
        }
        if(absent) return null;
        return this.getCached(entry);
    }

    @Override
    public V put(K key, V value) {
        MutableEntry<V> entry = this.immutableCopy.get(key);
        MutableEntry<V> previous = entry;
        if(entry == null || !this.tryPut(entry, value)) {
            synchronized(this.lock) {
                if((entry = this.immutableCopy.get(key)) != null) {
                    if(this.unexpungedLocked(entry) && this.mutableCopy != null) previous = this.mutableCopy.put(key, entry);
                    entry.value = value;
                } else {
                    entry = this.mutableCopy != null ? this.mutableCopy.get(key) : null;
                    if(entry != null) {
                        entry.value = value;
                    } else {
                        if(!this.readAmended) {
                            this.dirtyLocked();
                            this.readAmended = true;
                        }
                        if(this.mutableCopy != null) previous = this.mutableCopy.put(key, new MutableEntry<>(value, false));
                    }
                }
            }
        }
        return previous != null ? previous.value : null;
    }

    @Override
    public V remove(Object key) {
        MutableEntry<V> entry = this.immutableCopy.get(key);
        MutableEntry<V> previous = entry;
        boolean absent = entry == null;
        if(absent && this.readAmended) {
            synchronized(this.lock) {
                if(this.readAmended && (absent = (entry = this.immutableCopy.get(key)) == null) && this.mutableCopy != null) previous = this.mutableCopy.remove(key);
            }
        }
        if(!absent) this.tryDelete(entry);
        return previous != null ? previous.value : null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> other) {

    }

    @Override
    public void clear() {
        this.immutableCopy.clear();
        synchronized(this.lock) {
            this.mutableCopy = null;
            this.readMisses = 0;
            this.readAmended = false;
        }
    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    private V getCached(final MutableEntry<V> entry) {
        if(entry.expunged) return null;
        return entry.value;
    }

    private void tryDelete(final MutableEntry<V> entry) {
        if(entry.expunged || entry.value == null) return;
        entry.value = null;
    }

    private boolean tryPut(final MutableEntry<V> entry, final V value) {
        if(entry.expunged) return false;
        entry.value = value;
        return true;
    }

    private void missLocked() {
        this.readMisses++;
        int length = this.mutableCopy != null ? this.mutableCopy.size() : 0;
        if(this.readMisses > length) {
            if(this.mutableCopy != null) this.immutableCopy = this.mutableCopy;
            this.mutableCopy = null;
            this.readMisses = 0;
            this.readAmended = false;
        }
    }

    private void dirtyLocked() {
        if(this.mutableCopy == null) {
            this.mutableCopy = this.function.apply(this.immutableCopy.size());
            for (final Map.Entry<K, MutableEntry<V>> entry : this.immutableCopy.entrySet()) {
                if(!this.expungeLocked(entry.getValue())) this.mutableCopy.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private boolean expungeLocked(final MutableEntry<V> entry) {
        if(!entry.expunged && entry.value == null) entry.expunged = true;
        return entry.expunged;
    }

    private boolean unexpungedLocked(final MutableEntry<V> entry) {
        if(entry.expunged) {
            entry.value = null;
            entry.expunged = false;
        }
        return false;
    }

    private static class MutableEntry<V> {
        private volatile V value;
        private volatile boolean expunged;

        private MutableEntry(final V value, final boolean expunged) {
            this.value = value;
            this.expunged = expunged;
        }
    }
}
