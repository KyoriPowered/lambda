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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A collection of utilities for working with annotations.
 */
public final class Annotations {
  private Annotations() {
  }

  /**
   * Finds an annotation by searching the class hierarchy.
   *
   * @param element the annotated element
   * @param annotationType the annotation type
   * @param <A> the annotation type
   * @return the annotation
   */
  public static <A extends Annotation> @Nullable A findDeclared(final @NonNull AnnotatedElement element, final @NonNull Class<A> annotationType) {
    // try getting it on the element directly before trying to get more fancy
    final /* @Nullable */ A annotation = element.getDeclaredAnnotation(annotationType);
    if(annotation != null) {
      return annotation;
    }
    if(element instanceof Class<?>) {
      return findDeclared((Class<?>) element, annotationType);
    } else if(element instanceof Method) {
      return findDeclared((Method) element, annotationType);
    }
    return null;
  }

  /**
   * Finds an annotation by searching the hierarchy of {@code klass}.
   *
   * @param klass the klass
   * @param annotationType the annotation type
   * @param <A> the annotation type
   * @return the annotation
   */
  public static <A extends Annotation> @Nullable A findDeclared(final @NonNull Class<?> klass, final @NonNull Class<A> annotationType) {
    final /* @Nullable */ A annotation = klass.getDeclaredAnnotation(annotationType);
    if(annotation != null) {
      return annotation;
    }
    return Types.find(klass, type -> type.getDeclaredAnnotation(annotationType));
  }

  /**
   * Finds an annotation by searching the class hierarchy of {@code method}.
   *
   * @param method the method
   * @param annotationType the annotation type
   * @param <A> the annotation type
   * @return the annotation
   */
  public static <A extends Annotation> @Nullable A findDeclared(final @NonNull Method method, final @NonNull Class<A> annotationType) {
    // try getting it on the method directly before trying to search through the hierarchy
    final /* @Nullable */ A annotation = method.getDeclaredAnnotation(annotationType);
    if(annotation != null) {
      return annotation;
    }
    // private and static methods cannot be an override
    if(Members.isPrivate(method) || Members.isStatic(method)) return null;
    final Class<?> declaringClass = method.getDeclaringClass();
    return Types.find(declaringClass, type -> {
      // cannot search same class
      if(type == declaringClass) return null;
      final Method source = Methods.findDeclaredIn(type, method.getName(), method.getParameterTypes());
      if(source == null) return null;
      // source cannot be overridden if final
      if(Members.isFinal(source)) return null;
      // private and static methods cannot be an override
      if(Members.isPrivate(source) || Members.isStatic(source)) return null;
      // package-private methods can only be overridden from the same package
      if(Members.isPackagePrivate(method) && !Types.inSamePackage(method.getDeclaringClass(), source.getDeclaringClass())) return null;
      return source.getDeclaredAnnotation(annotationType);
    });
  }
}
