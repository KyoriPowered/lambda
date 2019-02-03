/*
 * This file is part of lambda, licensed under the MIT License.
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
package net.kyori.lambda.reflect.proxy;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * An abstract implementation of an invocation handler that uses {@link MethodHandle method handles}.
 */
public abstract class MethodHandleInvocationHandler implements InvocationHandler {
  protected final MethodHandles.Lookup lookup;

  /**
   * Creates a method handle invocation handler.
   *
   * @param lookup the lookup
   */
  protected MethodHandleInvocationHandler(final MethodHandles.@NonNull Lookup lookup) {
    this.lookup = lookup;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    return this.handle(proxy, method).invokeWithArguments(args);
  }

  protected @NonNull MethodHandle handle(final @NonNull Object proxy, final @NonNull Method method) throws IllegalAccessException {
    return this.lookup.unreflect(method).bindTo(this.target(method));
  }

  protected abstract @NonNull Object target(final @NonNull Method method);
}
