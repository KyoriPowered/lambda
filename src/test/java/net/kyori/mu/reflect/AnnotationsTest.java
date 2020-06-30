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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedElement;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

class AnnotationsTest {
  @Test
  void testFindDeclared() throws NoSuchMethodException {
    assertAnnotationValue("class=a", Annotations.findDeclared((AnnotatedElement) A.class, Foo.class));
    assertAnnotationValue("class=b", Annotations.findDeclared((AnnotatedElement) B.class, Foo.class));
    assertAnnotationValue("class=b,method=a", Annotations.findDeclared((AnnotatedElement) B.class.getDeclaredMethod("a"), Foo.class));
  }

  @Test
  void testFindDeclaredClass() {
    assertAnnotationValue("class=b", Annotations.findDeclared(B.class, Foo.class));
    assertAnnotationValue("class=b", Annotations.findDeclared(C.class, Foo.class));
    assertAnnotationValue("class=d", Annotations.findDeclared(D.class, Foo.class));
  }

  @Test
  void testFindDeclaredClass_local() {
    @Foo("class=x")
    class X {}
    class Y extends B {}
    assertAnnotationValue("class=x", Annotations.findDeclared(X.class, Foo.class));
    assertAnnotationValue("class=b", Annotations.findDeclared(Y.class, Foo.class));
  }

  @Test
  void testFindDeclaredField() throws NoSuchFieldException {
    assertNull(Annotations.findDeclared(B.class.getDeclaredField("y"), Foo.class));
    assertAnnotationValue("class=b,field=z", Annotations.findDeclared(B.class.getDeclaredField("z"), Foo.class));
  }

  @Test
  void testFindDeclared_method_public() throws NoSuchMethodException {
    assertAnnotationValue("class=b,method=a", Annotations.findDeclared(B.class.getDeclaredMethod("a"), Foo.class));
    assertAnnotationValue("class=c,method=a", Annotations.findDeclared(C.class.getDeclaredMethod("a"), Foo.class));
    assertAnnotationValue("class=c,method=a", Annotations.findDeclared(D.class.getDeclaredMethod("a"), Foo.class));
  }

  @Test
  void testFindDeclared_method_protected() throws NoSuchMethodException {
    assertAnnotationValue("class=b,method=b", Annotations.findDeclared(B.class.getDeclaredMethod("b"), Foo.class));
    assertAnnotationValue("class=b,method=b", Annotations.findDeclared(C.class.getDeclaredMethod("b"), Foo.class));
    assertAnnotationValue("class=d,method=b", Annotations.findDeclared(D.class.getDeclaredMethod("b"), Foo.class));
  }

  @Test
  void testFindDeclared_method_package() throws NoSuchMethodException {
    assertAnnotationValue("class=b,method=c", Annotations.findDeclared(B.class.getDeclaredMethod("c"), Foo.class));
    assertAnnotationValue("class=b,method=c", Annotations.findDeclared(C.class.getDeclaredMethod("c"), Foo.class));
    assertAnnotationValue("class=d,method=c", Annotations.findDeclared(D.class.getDeclaredMethod("c"), Foo.class));
  }

  @Test
  void testFindDeclared_method_private() throws NoSuchMethodException {
    assertAnnotationValue("class=b,method=d", Annotations.findDeclared(B.class.getDeclaredMethod("d"), Foo.class));
    assertNull(Annotations.findDeclared(C.class.getDeclaredMethod("d"), Foo.class));
    assertAnnotationValue("class=d,method=d", Annotations.findDeclared(D.class.getDeclaredMethod("d"), Foo.class));
  }

  private static void assertAnnotationValue(final String string, final @Nullable Foo annotation) {
    if(annotation == null) {
      fail("could not find annotation");
    } else {
      assertEquals(string, annotation.value());
    }
  }

  @Foo("class=a")
  public interface A {
    @Foo("class=a,method=a") void a();
  }

  @Foo("class=b")
  private static class B implements A {
    private String y;
    private @Foo("class=b,field=z") String z;

    @Foo("class=b,method=a") @Override public void a() {}
    @Foo("class=b,method=b") protected void b() {}
    @Foo("class=b,method=c") void c() {}
    @Foo("class=b,method=d") private void d() {}
  }

  private static class C extends B {
    @Foo("class=c,method=a") @Override public void a() {}
    @Override protected void b() {}
    @Override void c() {}
    private void d() {}
  }

  @Foo("class=d")
  private static class D extends C {
    @Override public void a() {}
    @Foo("class=d,method=b") @Override protected void b() {}
    @Foo("class=d,method=c") @Override void c() {}
    @Foo("class=d,method=d") private void d() {}
  }

  @Retention(RetentionPolicy.RUNTIME) private @interface Foo { String value(); }
}
