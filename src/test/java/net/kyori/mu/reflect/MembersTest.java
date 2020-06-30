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
package net.kyori.mu.reflect;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.kyori.mu.stream.MuStreams;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MembersTest {
  @Test
  void testIsPublic() {
    assertEach(allDeclaredMembers(A.class), Modifier::isPublic, Members::isPublic);
  }

  @Test
  void testIsProtected() {
    assertEach(allDeclaredMembers(A.class), Modifier::isProtected, Members::isProtected);
  }

  @Test
  void testIsPackagePrivate_constructor() throws NoSuchMethodException {
    assertFalse(Members.isPackagePrivate(A.class.getDeclaredConstructor(int.class)));
    assertFalse(Members.isPackagePrivate(A.class.getDeclaredConstructor(int.class, int.class)));
    assertTrue(Members.isPackagePrivate(A.class.getDeclaredConstructor(int.class, int.class, int.class)));
    assertFalse(Members.isPackagePrivate(A.class.getDeclaredConstructor(int.class, int.class, int.class, int.class)));
  }

  @Test
  void testIsPackagePrivate_field() throws NoSuchFieldException {
    assertFalse(Members.isPackagePrivate(A.class.getDeclaredField("a")));
    assertFalse(Members.isPackagePrivate(A.class.getDeclaredField("b")));
    assertTrue(Members.isPackagePrivate(A.class.getDeclaredField("c")));
    assertFalse(Members.isPackagePrivate(A.class.getDeclaredField("d")));
  }

  @Test
  void testIsPackagePrivate_method() throws NoSuchMethodException {
    assertFalse(Members.isPackagePrivate(A.class.getDeclaredMethod("a")));
    assertFalse(Members.isPackagePrivate(A.class.getDeclaredMethod("b")));
    assertTrue(Members.isPackagePrivate(A.class.getDeclaredMethod("c")));
    assertFalse(Members.isPackagePrivate(A.class.getDeclaredMethod("d")));
  }

  @Test
  void testIsPrivate() {
    assertEach(allDeclaredMembers(A.class), Modifier::isPrivate, Members::isPrivate);
  }

  @Test
  void testIsAbstract() {
    assertEach(allDeclaredMembers(A.class), Modifier::isAbstract, Members::isAbstract);
  }

  @Test
  void testIsFinal() {
    assertEach(allDeclaredMembers(A.class), Modifier::isFinal, Members::isFinal);
  }

  @Test
  void testIsStatic() {
    assertEach(allDeclaredMembers(A.class), Modifier::isStatic, Members::isStatic);
  }

  private static Stream<Member> allDeclaredMembers(final Class<?> type) {
    return MuStreams.concat(
      Stream.of(type.getDeclaredFields()),
      Stream.of(type.getDeclaredConstructors()),
      Stream.of(type.getDeclaredMethods())
    );
  }

  private static <A extends Member> void assertEach(final Stream<A> objects, final IntPredicate theirs, final Predicate<A> ours) {
    objects.forEach(object -> assertEquals(theirs.test(object.getModifiers()), ours.test(object)));
  }

  @SuppressWarnings("unused")
  private static abstract class A {
    public int a;
    protected int b;
    /* package */ int c;
    private int d;
    private static int e;
    private static final int F = 0;

    public A(final int a) {}
    protected A(final int a, final int b) {}
    /* package */ A(final int a, final int b, final int c) {}
    private A(final int a, final int b, final int c, final int d) {}

    public void a() {}
    protected void b() {}
    /* package */ void c() {}
    private void d() {}
    private static void e() {}
    public final void f() {}
    public abstract void g();
  }
}
