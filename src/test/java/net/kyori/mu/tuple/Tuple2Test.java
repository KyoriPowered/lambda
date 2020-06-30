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
package net.kyori.mu.tuple;

import com.google.common.truth.StringSubject;
import java.util.AbstractMap;
import java.util.Map;
import net.kyori.examination.ExaminableProperty;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Tuple2Test {
  @Test
  void testOfLeft() {
    final Tuple2<String, ?> tuple = Tuple2.tuple2a("a");
    assertThat(tuple.a()).isEqualTo("a");
    assertThat(tuple.b()).isNull();
  }

  @Test
  void testOfRight() {
    final Tuple2<?, String> tuple = Tuple2.tuple2b("b");
    assertThat(tuple.a()).isNull();
    assertThat(tuple.b()).isEqualTo("b");
  }

  @Test
  void testFromMapEntry() {
    final Map.Entry<String, String> entry = new AbstractMap.SimpleImmutableEntry<>("a", "b");
    final Tuple2<String, String> tuple = Tuple2.from(entry);
    assertThat(tuple.a()).isEqualTo("a");
    assertThat(tuple.b()).isEqualTo("b");
  }

  @Test
  void testOf() {
    final Tuple2<String, String> tuple = Tuple2.tuple2("a", "b");
    assertThat(tuple.a()).isEqualTo("a");
    assertThat(tuple.b()).isEqualTo("b");
  }

  @Test
  void testTuple2() {
    final Tuple2<String, String> tuple = Tuple2.tuple2("a", "b");
    assertThat(tuple.a()).isEqualTo("a");
    assertThat(tuple.b()).isEqualTo("b");
  }

  @Test
  void testMap() {
    Tuple2<String, String> tuple = Tuple2.tuple2("a", "b");
    assertEquals("a", tuple.a());
    assertEquals("b", tuple.b());
    tuple = tuple.map(a -> "y", b -> "z");
    assertEquals("y", tuple.a());
    assertEquals("z", tuple.b());
  }

  @Test
  void testMapA() {
    Tuple2<String, String> tuple = Tuple2.tuple2("a", "b");
    assertEquals("a", tuple.a());
    assertEquals("b", tuple.b());
    tuple = tuple.mapA(a -> "y");
    assertEquals("y", tuple.a());
    assertEquals("b", tuple.b());
  }

  @Test
  void testMapB() {
    Tuple2<String, String> tuple = Tuple2.tuple2("a", "b");
    assertEquals("a", tuple.a());
    assertEquals("b", tuple.b());
    tuple = tuple.mapB(b -> "z");
    assertEquals("a", tuple.a());
    assertEquals("z", tuple.b());
  }

  @Test
  void testExaminableProperties() {
    assertThat(Tuple2.tuple2("abc", "def").examinableProperties().map(ExaminableProperty::name)).containsExactly("a", "b").inOrder();
  }

  @Test
  void testToString() {
    final StringSubject ss = assertThat(Tuple2.tuple2("abc", "def").toString());
    ss.contains("a=abc");
    ss.contains("b=def");
  }

  @Test
  void testEquals() {
    assertThat(Tuple2.tuple2("a", "b")).isEqualTo(Tuple2.tuple2("a", "b"));
    assertThat(Tuple2.tuple2("tfel", "b")).isNotEqualTo(Tuple2.tuple2("a", "b"));
    assertThat(Tuple2.tuple2("a", "thgir")).isNotEqualTo(Tuple2.tuple2("a", "b"));
  }

  @Test
  void testHashCode() {
    assertThat(Tuple2.tuple2("a", "b").hashCode()).isEqualTo(Tuple2.tuple2("a", "b").hashCode());
    assertThat(Tuple2.tuple2("tfel", "b").hashCode()).isNotEqualTo(Tuple2.tuple2("a", "b").hashCode());
    assertThat(Tuple2.tuple2("a", "thgir").hashCode()).isNotEqualTo(Tuple2.tuple2("a", "b").hashCode());
  }
}
