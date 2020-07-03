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
package net.kyori.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public final class Testing {
  @SuppressWarnings("SpellCheckingInspection") // "d" at end is intentional
  public static void assetAllMatchd(final DoublePredicate predicate, final double... values) {
    assertTrue(DoubleStream.of(values).allMatch(predicate));
  }

  @SuppressWarnings("SpellCheckingInspection") // "d" at end is intentional
  public static void assertNoneMatchd(final DoublePredicate predicate, final double... values) {
    assertTrue(DoubleStream.of(values).noneMatch(predicate));
  }

  @SuppressWarnings("SpellCheckingInspection") // "i" at end is intentional
  public static void assetAllMatchi(final IntPredicate predicate, final int... values) {
    assertTrue(IntStream.of(values).allMatch(predicate));
  }

  @SuppressWarnings("SpellCheckingInspection") // "i" at end is intentional
  public static void assertNoneMatchi(final IntPredicate predicate, final int... values) {
    assertTrue(IntStream.of(values).noneMatch(predicate));
  }

  // From https://github.com/junit-team/junit4/wiki/Multithreaded-code-and-concurrency
  public static void assertConcurrent(final String message, final List<? extends Runnable> tasks, final int maximumTimeout) throws InterruptedException {
    final int numThreads = tasks.size();
    final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());
    final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
    try {
      final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numThreads);
      final CountDownLatch afterInitBlocker = new CountDownLatch(1);
      final CountDownLatch allDone = new CountDownLatch(numThreads);
      for(final Runnable submittedTestRunnable : tasks) {
        threadPool.submit(() -> {
          allExecutorThreadsReady.countDown();
          try {
            afterInitBlocker.await();
            submittedTestRunnable.run();
          } catch(final Throwable e) {
            exceptions.add(e);
          } finally {
            allDone.countDown();
          }
        });
      }
      assertTrue(allExecutorThreadsReady.await(tasks.size() * 10, TimeUnit.MILLISECONDS), "Timeout initializing threads! Perform long lasting initializations before passing runnables to assertConcurrent");
      afterInitBlocker.countDown();
      assertTrue(allDone.await(maximumTimeout, TimeUnit.SECONDS), message + " timeout! More than" + maximumTimeout + "seconds");
    } finally {
      threadPool.shutdownNow();
    }
    assertTrue(exceptions.isEmpty(), message + "failed with exception(s)" + exceptions);
  }
}
