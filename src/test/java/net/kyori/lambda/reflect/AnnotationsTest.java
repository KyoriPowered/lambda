/*
 * This file is part of lambda, licensed under the MIT License.
 *
 * Copyright (c) 2018 KyoriPowered
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
package net.kyori.lambda.reflect;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnnotationsTest {
  @Test
  void testFindClass() {
    assertEquals("abc", Annotations.find(A.class, Foo.class).value());
    assertEquals("abc", Annotations.find(B.class, Foo.class).value());
    assertEquals("ghi", Annotations.find(C.class, Foo.class).value());
  }

  @Test
  void testFindMethod() throws NoSuchMethodException {
    assertEquals("A.a", Annotations.find(A.class.getDeclaredMethod("a"), Foo.class).value());
    assertEquals("B.a", Annotations.find(B.class.getDeclaredMethod("a"), Foo.class).value());
    assertEquals("B.a", Annotations.find(C.class.getDeclaredMethod("a"), Foo.class).value());
  }

  @Foo("abc")
  private static class A {
    @Foo("A.a") public void a() {}
  }

  private static class B extends A {
    @Foo("B.a") @Override public void a() {}
  }

  @Foo("ghi")
  private static class C extends B {
    @Override public void a() {}
  }

  @Retention(RetentionPolicy.RUNTIME) private @interface Foo { String value(); }
}