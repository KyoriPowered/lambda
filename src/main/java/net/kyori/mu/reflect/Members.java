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
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A collection of utilities for working with class members.
 */
public final class Members {
  /**
   * The access modifiers that can be applied to a member.
   */
  private static final int ACCESS_MODIFIERS = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;

  private Members() {
  }

  /**
   * Checks if {@code member} is public.
   *
   * @param member the member
   * @return {@code true} if public, {@code false} otherwise
   */
  public static boolean isPublic(final @NonNull Member member) {
    return Modifier.isPublic(member.getModifiers());
  }

  /**
   * Checks if {@code member} is protected.
   *
   * @param member the member
   * @return {@code true} if protected, {@code false} otherwise
   */
  public static boolean isProtected(final @NonNull Member member) {
    return Modifier.isProtected(member.getModifiers());
  }

  /**
   * Checks if {@code member} is package-private.
   *
   * @param member the member
   * @return {@code true} if package-private, {@code false} otherwise
   */
  public static boolean isPackagePrivate(final @NonNull Member member) {
    return (member.getModifiers() & ACCESS_MODIFIERS) == 0;
  }

  /**
   * Checks if {@code member} is private.
   *
   * @param member the member
   * @return {@code true} if private, {@code false} otherwise
   */
  public static boolean isPrivate(final @NonNull Member member) {
    return Modifier.isPrivate(member.getModifiers());
  }

  /**
   * Checks if {@code member} is abstract.
   *
   * @param member the member
   * @return {@code true} if abstract, {@code false} otherwise
   */
  public static boolean isAbstract(final @NonNull Member member) {
    return Modifier.isAbstract(member.getModifiers());
  }

  /**
   * Checks if {@code member} is final.
   *
   * @param member the member
   * @return {@code true} if final, {@code false} otherwise
   */
  public static boolean isFinal(final @NonNull Member member) {
    return Modifier.isFinal(member.getModifiers());
  }

  /**
   * Checks if {@code member} is static.
   *
   * @param member the member
   * @return {@code true} if static, {@code false} otherwise
   */
  public static boolean isStatic(final @NonNull Member member) {
    return Modifier.isStatic(member.getModifiers());
  }
}
