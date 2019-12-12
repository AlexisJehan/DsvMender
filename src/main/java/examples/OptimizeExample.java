/*
 * MIT License
 *
 * Copyright (c) 2017-2019 Alexis Jehan
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
package examples;

import com.github.alexisjehan.javanilla.lang.Strings;
import com.github.alexisjehan.mender.dsv.DsvMender;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class OptimizeExample {

	private static final char DELIMITER = ',';
	private static final int LENGTH = 3;

	private OptimizeExample() {
		// Not available
	}

	public static void main(final String... args) {
		final var mender = DsvMender.builder()
				.withDelimiter(DELIMITER)
				.withLength(LENGTH)
				.withMaxDepth(Integer.MAX_VALUE)
				.withConstraint("foo"::equals, 0) // values[0] must be "foo"
				.withConstraint("bar"::equals, 2) // values[2] must be "bar"
				.build();

		final var row = "foo,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,bar";

		// Not optimized, out of memory
		//printValues(mender.mend(row));

		// Optimized, can be computed
		final var threshold = 1;
		printValues(mender.mend(mender.optimize(threshold, row)));
	}

	private static void printValues(final String[] values) {
		System.out.println(
				Arrays.stream(values)
						.map(Strings::quote)
						.collect(Collectors.joining(", "))
		);
	}
}