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
package net.kyori.mu.collection;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class MuIterablesTest {
  @Test
  void testLFold() {
    assertThat(MuIterables.lfold(Arrays.asList(1, 2, 3, 4, 5), 0, (a, b) -> a + b)).isEqualTo(15);
    assertThat(MuIterables.lfold(Arrays.asList(1, 2, 3, 4, 5), 1, (a, b) -> a + b)).isEqualTo(16);
  }

  @Test
  void testReduce() {
    final Function<Iterable<? extends String>, String> reducer = strings -> String.join("-", strings);

    assertThat(
      MuIterables.reduce(
        () -> Collections.<String>emptyList().iterator(),
        "",
        reducer
      )
    ).isEmpty();
    // forwards to MuCollections.reduce
    assertThat(
      MuIterables.reduce(
        Collections.emptyList(),
        "",
        reducer
      )
    ).isEmpty();

    assertThat(
      MuIterables.reduce(
        () -> Collections.singletonList("abc").iterator(),
        "",
        reducer
      )
    ).isEqualTo("abc");

    assertThat(
      MuIterables.reduce(
        () -> Arrays.asList("abc", "def", "ghi").iterator(),
        "",
        reducer
      )
    ).isEqualTo("abc-def-ghi");
  }
}
