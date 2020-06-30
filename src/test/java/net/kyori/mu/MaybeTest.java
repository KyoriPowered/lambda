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
package net.kyori.mu;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import net.kyori.examination.ExaminableProperty;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaybeTest {
  @Test
  void testNothing() {
    assertEquals(Maybe.nothing(), Maybe.nothing());
  }

  @Test
  void testJust() {
    assertEquals(Maybe.just("foo"), Maybe.just("foo"));
  }

  @Test
  void testMaybe() {
    assertEquals(Maybe.nothing(), Maybe.maybe(null));
    assertEquals(Maybe.just("foo"), Maybe.maybe("foo"));
  }

  @Test
  void testFrom() {
    assertEquals(Maybe.nothing(), Maybe.from(Optional.empty()));
    assertEquals(Maybe.just("foo"), Maybe.from(Optional.of("foo")));
  }

  @Test
  void testIsNothing() {
    assertTrue(Maybe.nothing().isNothing());
    assertFalse(Maybe.just("foo").isNothing());
  }

  @Test
  void testIsJust() {
    assertFalse(Maybe.nothing().isJust());
    assertTrue(Maybe.just("foo").isJust());
  }

  @Test
  void testOrDefault() {
    assertEquals("bar", Maybe.nothing().orDefault("bar"));
    assertEquals("foo", Maybe.just("foo").orDefault("bar"));
  }

  @Test
  void testOrGet() {
    assertEquals("bar", Maybe.nothing().orGet(() -> "bar"));
    assertEquals("foo", Maybe.just("foo").orGet(() -> "bar"));
  }

  @Test
  void testOrThrow() {
    assertThrows(NoSuchElementException.class, () -> Maybe.nothing().orThrow());
    assertEquals("foo", Maybe.just("foo").orThrow());
    assertThrows(Expected.class, () -> Maybe.nothing().orThrow(Expected::new));
    assertEquals("foo", Maybe.just("foo").orThrow(Expected::new));
  }

  @Test
  void testOr() {
    assertEquals(Maybe.nothing(), Maybe.nothing().or(Maybe.nothing()));
    assertEquals(Maybe.just("foo"), Maybe.just("foo").or(Maybe.just("bar")));
    assertEquals(Maybe.just("foo"), Maybe.nothing().or(Maybe.just("foo")));

    assertEquals(Maybe.nothing(), Maybe.nothing().or(Maybe::nothing));
    assertEquals(Maybe.just("foo"), Maybe.just("foo").or(() -> Maybe.just("bar")));
    assertEquals(Maybe.just("foo"), Maybe.nothing().or(() -> Maybe.just("foo")));
  }

  @Test
  void testFilter() {
    assertTrue(Maybe.<String>nothing().filter(String::isEmpty).isNothing());
    assertTrue(Maybe.just("foo").filter(String::isEmpty).isNothing());
  }

  @Test
  void testMap() {
    assertTrue(Maybe.nothing().map(nothing -> "foo").isNothing());
    assertEquals("!foo!", Maybe.just("foo").map(string -> '!' + string + '!').orDefault(null));
  }

  @Test
  void testFlatMap() {
    assertTrue(Maybe.nothing().flatMap(nothing -> Maybe.just("foo")).isNothing());
    assertEquals("!foo!", Maybe.just("foo").flatMap(string -> Maybe.just('!' + string + '!')).orDefault(null));
  }

  @Test
  void testIfNothing() {
    final AtomicInteger nothing = new AtomicInteger();
    Maybe.nothing().ifNothing(nothing::incrementAndGet);
    assertEquals(1, nothing.get());
    Maybe.just("foo").ifNothing(nothing::incrementAndGet);
    assertEquals(1, nothing.get());
  }

  @Test
  void testIfJust() {
    final AtomicInteger just = new AtomicInteger();
    Maybe.nothing().ifJust(value -> just.incrementAndGet());
    assertEquals(0, just.get());
    Maybe.just("foo").ifJust(value -> just.incrementAndGet());
    assertEquals(1, just.get());
  }

  @Test
  void testForEach() {
    final AtomicInteger nothing = new AtomicInteger();
    Maybe.nothing().forEach(v -> nothing.incrementAndGet());
    assertEquals(0, nothing.get());
    final AtomicInteger just = new AtomicInteger();
    Maybe.just("foo").forEach(v -> just.incrementAndGet());
    assertEquals(1, just.get());
  }

  @Test
  void testStream() {
    assertThat(Maybe.nothing().stream()).isEmpty();
    assertThat(Maybe.just("foo").stream()).containsExactly("foo");
  }

  @Test
  void testOptional() {
    assertEquals(Maybe.nothing().optional(), Maybe.nothing().optional());
    assertEquals(Maybe.just("foo").optional(), Maybe.just("foo").optional());
  }

  @Test
  void testIterator() {
    assertThat(Maybe.nothing()).isEmpty();
    assertThat(Maybe.just("foo")).containsExactly("foo");
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Test
  void testCollector() {
    assertEquals(Maybe.nothing(), Stream.empty().collect(Maybe.collector()));
    assertEquals(Maybe.just("foo"), Stream.of("foo").collect(Maybe.collector()));
    assertThrows(AmbiguousElementException.class, () -> Stream.of("foo", "bar").collect(Maybe.collector()));
  }

  @Test
  void testCast() {
    final Maybe<CharSequence> mc = Maybe.just("abc");
    final Maybe<String> ms = mc.cast(String.class);
    assertTrue(ms.isJust());
    assertEquals(Maybe.just("abc"), ms);

    assertEquals(Maybe.<CharSequence>just("kitten"), Maybe.cast(Maybe.just("kitten"), CharSequence.class));

    assertEquals(Maybe.just("potato"), Maybe.cast("potato", String.class));
    assertEquals(Maybe.nothing(), Maybe.cast("potato", Integer.class)); // a potato are not a number!
  }

  @Test
  void testFirst() {
    final Maybe<String> expected = Maybe.just("meow");
    assertEquals(Maybe.nothing(), Maybe.first(
      Maybe.nothing(),
      Maybe.nothing()
    ));
    assertEquals(Maybe.nothing(), Maybe.first(Arrays.asList(
      Maybe.nothing(),
      Maybe.nothing()
    )));
    assertEquals(expected, Maybe.first(
      expected,
      Maybe.nothing(),
      Maybe.nothing()
    ));
    assertEquals(expected, Maybe.first(Arrays.asList(
      expected,
      Maybe.nothing(),
      Maybe.nothing()
    )));
    assertEquals(expected, Maybe.first(
      Maybe.nothing(),
      expected,
      Maybe.nothing()
    ));
    assertEquals(expected, Maybe.first(Arrays.asList(
      Maybe.nothing(),
      expected,
      Maybe.nothing()
    )));
    assertEquals(expected, Maybe.first(
      Maybe.nothing(),
      Maybe.nothing(),
      expected
    ));
    assertEquals(expected, Maybe.first(Arrays.asList(
      Maybe.nothing(),
      Maybe.nothing(),
      expected
    )));
  }

  @Test
  void testExaminableProperties() {
    assertThat(Maybe.nothing().examinableProperties().map(ExaminableProperty::name)).isEmpty();
    assertThat(Maybe.just("foo").examinableProperties().map(ExaminableProperty::name)).containsExactly("value").inOrder();
  }

  @Test
  void testToString() {
    assertThat(Maybe.nothing().toString()).isEqualTo("Maybe.nothing()");
    assertThat(Maybe.just("abc").toString()).isEqualTo("Maybe.just(abc)");
  }

  @Test
  void testHashCode() {
    assertThat(Maybe.nothing().hashCode()).isEqualTo(Maybe.nothing().hashCode());
    assertThat(Maybe.just("foo").hashCode()).isEqualTo(Maybe.just("foo").hashCode());
  }

  @SuppressWarnings("serial")
  private static class Expected extends RuntimeException {}
}
