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
import net.kyori.examination.ExaminableProperty;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Tuple3Test {
  @Test
  void testOfA() {
    final Tuple3<String, ?, ?> tuple = Tuple3.tuple3a("a");
    assertThat(tuple.a()).isEqualTo("a");
    assertThat(tuple.b()).isNull();
    assertThat(tuple.c()).isNull();
  }

  @Test
  void testOfB() {
    final Tuple3<?, String, ?> tuple = Tuple3.tuple3b("b");
    assertThat(tuple.a()).isNull();
    assertThat(tuple.b()).isEqualTo("b");
    assertThat(tuple.c()).isNull();
  }

  @Test
  void testOfC() {
    final Tuple3<?, ?, String> tuple = Tuple3.tuple3c("c");
    assertThat(tuple.a()).isNull();
    assertThat(tuple.b()).isNull();
    assertThat(tuple.c()).isEqualTo("c");
  }

  @Test
  void testOf() {
    final Tuple3<String, String, String> tuple = Tuple3.tuple3("a", "b", "c");
    assertThat(tuple.a()).isEqualTo("a");
    assertThat(tuple.b()).isEqualTo("b");
    assertThat(tuple.c()).isEqualTo("c");
  }

  @Test
  void testTuple3() {
    final Tuple3<String, String, String> tuple = Tuple3.tuple3("a", "b", "c");
    assertThat(tuple.a()).isEqualTo("a");
    assertThat(tuple.b()).isEqualTo("b");
    assertThat(tuple.c()).isEqualTo("c");
  }

  @Test
  void testMap2() {
    Tuple3<String, String, String> tuple = Tuple3.tuple3("a", "b", "c");
    assertEquals("a", tuple.a());
    assertEquals("b", tuple.b());
    assertEquals("c", tuple.c());
    tuple = tuple.map(a -> "x", b -> "y");
    assertEquals("x", tuple.a());
    assertEquals("y", tuple.b());
    assertEquals("c", tuple.c());
  }

  @Test
  void testMap3() {
    Tuple3<String, String, String> tuple = Tuple3.tuple3("a", "b", "c");
    assertEquals("a", tuple.a());
    assertEquals("b", tuple.b());
    assertEquals("c", tuple.c());
    tuple = tuple.map(a -> "x", b -> "y", c -> "z");
    assertEquals("x", tuple.a());
    assertEquals("y", tuple.b());
    assertEquals("z", tuple.c());
  }

  @Test
  void testMapA() {
    Tuple3<String, String, String> tuple = Tuple3.tuple3("a", "b", "c");
    assertEquals("a", tuple.a());
    assertEquals("b", tuple.b());
    assertEquals("c", tuple.c());
    tuple = tuple.mapA(a -> "x");
    assertEquals("x", tuple.a());
    assertEquals("b", tuple.b());
    assertEquals("c", tuple.c());
  }

  @Test
  void testMapB() {
    Tuple3<String, String, String> tuple = Tuple3.tuple3("a", "b", "c");
    assertEquals("a", tuple.a());
    assertEquals("b", tuple.b());
    assertEquals("c", tuple.c());
    tuple = tuple.mapB(b -> "y");
    assertEquals("a", tuple.a());
    assertEquals("y", tuple.b());
    assertEquals("c", tuple.c());
  }

  @Test
  void testMapC() {
    Tuple3<String, String, String> tuple = Tuple3.tuple3("a", "b", "c");
    assertEquals("a", tuple.a());
    assertEquals("b", tuple.b());
    assertEquals("c", tuple.c());
    tuple = tuple.mapC(c -> "z");
    assertEquals("a", tuple.a());
    assertEquals("b", tuple.b());
    assertEquals("z", tuple.c());
  }

  @Test
  void testExaminableProperties() {
    assertThat(Tuple3.tuple3("abc", "def", "ghi").examinableProperties().map(ExaminableProperty::name)).containsExactly("a", "b", "c").inOrder();
  }

  @Test
  void testToString() {
    final StringSubject ss = assertThat(Tuple3.tuple3("abc", "def", "ghi").toString());
    ss.contains("a=abc");
    ss.contains("b=def");
    ss.contains("c=ghi");
  }

  @Test
  void testEquals() {
    assertThat(Tuple3.tuple3("a", "b", "c")).isEqualTo(Tuple3.tuple3("a", "b", "c"));
    assertThat(Tuple3.tuple3("tfel", "b", "c")).isNotEqualTo(Tuple3.tuple3("a", "b", "c"));
    assertThat(Tuple3.tuple3("a", "elddim", "c")).isNotEqualTo(Tuple3.tuple3("a", "b", "c"));
    assertThat(Tuple3.tuple3("a", "b", "thgir")).isNotEqualTo(Tuple3.tuple3("a", "b", "c"));
  }

  @Test
  void testHashCode() {
    assertThat(Tuple3.tuple3("a", "b", "c").hashCode()).isEqualTo(Tuple3.tuple3("a", "b", "c").hashCode());
    assertThat(Tuple3.tuple3("tfel", "b", "c").hashCode()).isNotEqualTo(Tuple3.tuple3("a", "b", "c").hashCode());
    assertThat(Tuple3.tuple3("a", "elddim", "c").hashCode()).isNotEqualTo(Tuple3.tuple3("a", "b", "c").hashCode());
    assertThat(Tuple3.tuple3("a", "b", "thgir").hashCode()).isNotEqualTo(Tuple3.tuple3("a", "b", "c").hashCode());
  }
}
