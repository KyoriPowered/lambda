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

import net.kyori.lambda.collection.LoadingMap;
import net.kyori.lambda.function.ThrowingFunction;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * An abstract implementation of an invocation handler that uses {@link MethodHandle method handles}.
 */
public abstract class CachedMethodHandleInvocationHandler extends MethodHandleInvocationHandler {
  // A cache of unbound method handles.
  private final Map<Method, MethodHandle> unboundCache = LoadingMap.concurrent(ThrowingFunction.of(this.lookup::unreflect));
  // A cache of bound method handles.
  private final Map<Method, MethodHandle> cache = LoadingMap.concurrent(method -> {
    final Object target = this.target(method);
    return this.unboundCache.get(method).bindTo(target);
  });

  /**
   * Creates a method handle invocation handler, with an unbound cache.
   *
   * @param lookup the method handle lookup
   */
  public CachedMethodHandleInvocationHandler(final MethodHandles.@NonNull Lookup lookup) {
    super(lookup);
  }

  @Override
  protected @NonNull MethodHandle handle(final @NonNull Object proxy, final @NonNull Method method) {
    return this.cache.get(method);
  }
}
