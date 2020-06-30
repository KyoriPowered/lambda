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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("unchecked")
class TypesTest {
  @Test
  void testFind_sameAsInput() {
    assertEquals(A.class, Types.find(A.class, Function.identity()));
  }

  @Test
  void testFind_directFirstParent() {
    assertEquals(Object.class, Types.find(A.class, skipIf(A.class)));
    assertEquals(A.class, Types.find(B.class, skipIf(B.class)));
    assertEquals(D.class, Types.find(F.class, skipIf(F.class, Object.class)));
  }

  @Test
  void testFind_firstParentOfObject() {
    assertNull(Types.find(Object.class, skipIf(Object.class)));
  }

  @Test
  void testFind_firstParentOfInterface() {
    assertNull(Types.find(C.class, skipIf(C.class)));
    assertEquals(C.class, Types.find(D.class, skipIf(D.class)));
  }

  @Test
  void testFind_firstParentChoosesClassBeforeInterface() {
    assertEquals(B.class, Types.find(E.class, skipIf(E.class)));
  }

  @Test
  void testInSamePackage() {
    assertTrue(Types.inSamePackage(List.class, Map.class));
    assertFalse(Types.inSamePackage(List.class, Stream.class));
  }

  private static <C> Function<Class<? super C>, Class<? extends C>> skipIf(final Class<? super C>... exclusions) {
    return type -> {
      for(final Class<? super C> exclusion : exclusions) {
        if(type == exclusion) {
          return null;
        }
      }
      return (Class<? extends C>) type;
    };
  }

  private class A {}
  private class B extends A {}
  private interface C {}
  private interface D extends C {}
  private class E extends B implements D {}
  private class F implements D {}
}
