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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;

class SyncMapTest {
    @Test
    void testInitialization() {
        final SyncMap<String, String> map = SyncMap.hashmap();
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
        assertFalse(map.containsKey("foo"));
        assertFalse(map.containsValue("bar"));
        assertNull(map.get("foo"));
        assertNull(map.remove("foo"));
        assertFalse(map.remove("foo", "bar"));
    }

    @Test
    void testMutation_put_get() {
        final SyncMap<String, String> map = SyncMap.hashmap();
        assertNull(map.put("foo", "bar"));
        assertEquals("bar", map.get("foo"));
        assertEquals("bar", map.put("foo", "baz"));
        assertEquals("baz", map.get("foo"));
        assertEquals(1, map.size());
        assertFalse(map.isEmpty());
        assertTrue(map.containsKey("foo"));
        assertTrue(map.containsValue("baz"));
    }

    @Test
    void testMutation_putAll() {
        final SyncMap<String, String> map = SyncMap.hashmap();
        final Map<String, String> test = Maps.newHashMap();
        test.put("1", "2");
        test.put("3", "4");
        test.put("5", "6");

        map.putAll(test);
        assertEquals("2", map.get("1"));
        assertEquals("4", map.get("3"));
        assertEquals("6", map.get("5"));
    }

    @Test
    void testMutation_remove() {
        final SyncMap<String, String> map = SyncMap.hashmap();
        map.put("foo", "bar");
        map.put("abc", "123");
        assertEquals("bar", map.remove("foo"));
        assertTrue(map.remove("abc", "123"));
    }

    @Test
    void testMutation_clear() {
        final SyncMap<String, String> map = SyncMap.hashmap();
        map.put("example", "random");
        map.clear();
        assertNull(map.get("example"));
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
    }

    @Test
    void testKeyMutation() {
        final SyncMap<String, String> map = SyncMap.hashmap();
        map.put("1", "2");
        map.put("3", "4");
        map.put("5", "6");

        final Set<String> keys = map.keySet();
        assertEquals(3, keys.size());
        assertFalse(keys.isEmpty());
        assertTrue(keys.contains("1"));
        assertFalse(keys.contains("2"));
        assertTrue(keys.remove("1"));
        assertFalse(keys.remove("2"));
        assertThrows(UnsupportedOperationException.class, () -> keys.add("foo"));
        assertThrows(UnsupportedOperationException.class, () -> keys.addAll(Lists.newArrayList("bar", "baz")));
        assertFalse(keys.contains("1"));
        assertEquals(2, keys.size());
    }

    @Test
    void testKeyMutation_iterator() {
        final SyncMap<String, String> map = SyncMap.of(LinkedHashMap<String, SyncMap.ExpungingValue<String>>::new, 3);
        map.put("1", "2");
        map.put("3", "4");
        map.put("5", "6");

        final Set<String> keys = map.keySet();
        final Iterator<String> keyIterator = keys.iterator();
        assertTrue(keyIterator.hasNext());
        assertEquals("1", keyIterator.next());
        keyIterator.remove();
        assertFalse(keys.contains("1"));

        final String[] expected = {"3", "5"};
        final List<String> remaining = new ArrayList<>();
        keyIterator.forEachRemaining(remaining::add);
        assertArrayEquals(expected, remaining.toArray());
    }

    @Test
    void testKeyMutation_spliterator() {
        final SyncMap<String, String> map = SyncMap.of(LinkedHashMap<String, SyncMap.ExpungingValue<String>>::new, 3);
        map.put("1", "2");
        map.put("3", "4");
        map.put("5", "6");

        final Set<String> keys = map.keySet();
        final Spliterator<String> keySpliterator = keys.spliterator();
        assertTrue(keySpliterator.tryAdvance(value -> assertEquals("1", value)));

        final String[] expected = {"3", "5"};
        final List<String> remaining = new ArrayList<>();
        keySpliterator.forEachRemaining(remaining::add);
        assertArrayEquals(expected, remaining.toArray());

        assertEquals(3, keySpliterator.estimateSize());
    }

    @Test
    void testValueMutation() {
        final SyncMap<String, String> map = SyncMap.hashmap();
        map.put("1", "2");
        map.put("3", "4");
        map.put("5", "6");

        final Collection<String> values = map.values();
        assertEquals(3, values.size());
        assertFalse(values.isEmpty());
        assertTrue(values.contains("2"));
        assertFalse(values.contains("1"));
        assertTrue(values.remove("2"));
        assertFalse(values.remove("1"));
        assertThrows(UnsupportedOperationException.class, () -> values.add("foo"));
        assertThrows(UnsupportedOperationException.class, () -> values.addAll(Lists.newArrayList("bar", "baz")));
        assertFalse(values.contains("2"));
        assertEquals(2, values.size());
    }

    @Test
    void testValueMutation_iterator() {
        final SyncMap<String, String> map = SyncMap.of(LinkedHashMap<String, SyncMap.ExpungingValue<String>>::new, 3);
        map.put("1", "2");
        map.put("3", "4");
        map.put("5", "6");

        final Collection<String> values = map.values();
        final Iterator<String> valueIterator = values.iterator();
        assertTrue(valueIterator.hasNext());
        assertEquals("2", valueIterator.next());
        valueIterator.remove();
        assertFalse(values.contains("2"));

        final String[] expected = {"4", "6"};
        final List<String> remaining = new ArrayList<>();
        valueIterator.forEachRemaining(remaining::add);
        assertArrayEquals(expected, remaining.toArray());
    }

    @Test
    void testValueMutation_spliterator() {
        final SyncMap<String, String> map = SyncMap.of(LinkedHashMap<String, SyncMap.ExpungingValue<String>>::new, 3);
        map.put("1", "2");
        map.put("3", "4");
        map.put("5", "6");

        final Collection<String> values = map.values();
        final Spliterator<String> valueSpliterator = values.spliterator();
        assertTrue(valueSpliterator.tryAdvance(value -> assertEquals("2", value)));

        final String[] expected = {"4", "6"};
        final List<String> remaining = new ArrayList<>();
        valueSpliterator.forEachRemaining(remaining::add);
        assertArrayEquals(expected, remaining.toArray());

        assertEquals(3, valueSpliterator.estimateSize());
    }

    @Test
    void testEntryMutation() {
        final SyncMap<String, String> map = SyncMap.hashmap();
        map.put("1", "2");
        map.put("3", "4");
        map.put("5", "6");

        final Map.Entry<String, String> goodEntry = this.exampleEntry("1", "2");
        final Map.Entry<String, String> badEntry = this.exampleEntry("abc", "123");

        final Set<Map.Entry<String, String>> entries = map.entrySet();
        assertEquals(3, entries.size());
        assertFalse(entries.isEmpty());
        assertTrue(entries.contains(goodEntry));
        assertFalse(entries.contains(badEntry));
        assertTrue(entries.remove(goodEntry));
        assertFalse(entries.remove(badEntry));
        assertThrows(UnsupportedOperationException.class, () -> entries.add(badEntry));
        assertThrows(UnsupportedOperationException.class, () -> entries.addAll(Lists.newArrayList(badEntry, goodEntry)));
        assertFalse(entries.contains(goodEntry));
        assertEquals(2, entries.size());
    }

    @Test
    void testEntryMutation_iterator() {
        final SyncMap<String, String> map = SyncMap.of(LinkedHashMap<String, SyncMap.ExpungingValue<String>>::new, 3);
        map.put("1", "2");
        map.put("3", "4");
        map.put("5", "6");

        final Map.Entry<String, String> firstEntry = this.exampleEntry("1", "2");
        final Map.Entry<String, String> secondEntry = this.exampleEntry("3", "4");
        final Map.Entry<String, String> thirdEntry = this.exampleEntry("5", "6");

        final Set<Map.Entry<String, String>> entries = map.entrySet();
        final Iterator<Map.Entry<String, String>> entryIterator = entries.iterator();
        assertTrue(entryIterator.hasNext());
        assertEquals(entryIterator.next(), firstEntry);
        entryIterator.remove();
        assertFalse(entries.contains(firstEntry));

        final List<Map.Entry<String, String>> remaining = new ArrayList<>();
        entryIterator.forEachRemaining(remaining::add);
        assertIterableEquals(Lists.newArrayList(secondEntry, thirdEntry), remaining);
    }

    @Test
    void testEntryMutation_spliterator() {
        final SyncMap<String, String> map = SyncMap.of(LinkedHashMap<String, SyncMap.ExpungingValue<String>>::new, 3);
        map.put("1", "2");
        map.put("3", "4");
        map.put("5", "6");

        final Map.Entry<String, String> firstEntry = this.exampleEntry("1", "2");
        final Map.Entry<String, String> secondEntry = this.exampleEntry("3", "4");
        final Map.Entry<String, String> thirdEntry = this.exampleEntry("5", "6");

        final Set<Map.Entry<String, String>> entries = map.entrySet();
        final Spliterator<Map.Entry<String, String>> entrySpliterator = entries.spliterator();
        assertTrue(entrySpliterator.tryAdvance(value -> assertEquals(firstEntry, value)));

        final List<Map.Entry<String, String>> remaining = new ArrayList<>();
        entrySpliterator.forEachRemaining(remaining::add);
        assertIterableEquals(Lists.newArrayList(secondEntry, thirdEntry), remaining);

        assertEquals(3, entrySpliterator.estimateSize());
    }

    private Map.Entry<String, String> exampleEntry(final String key, final String value) {
        return new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public String getValue() {
                return value;
            }

            @Override
            public String setValue(String value) {
                return value;
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
        };
    }
}
