/*
 * MIT License
 *
 * Copyright (c) 2017-2023 Alexis Jehan
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

public final class SimpleExample {

	private static final String DELIMITER = "\t";
	private static final int LENGTH = 3;

	private SimpleExample() {}

	public static void main(final String... args) {
		final var mender = DsvMender.builder()
				.withDelimiter(DELIMITER)
				.withLength(LENGTH)
				.withEstimation(value -> value.contains("1")) // Estimation on all values containing "1"
				.withEstimation(value -> value.contains("2")) // Estimation on all values containing "2"
				.withEstimation(value -> value.contains("3")) // Estimation on all values containing "3"
				.build();

		// Valid
		mender.mend("11", "22", "33");
		mender.mend("111", "222", "333");

		// Invalid
		mender.mend("11", "2", "2", "33");

		// ["11", "2", "2\t33"] -> 0.9 (Because the frequency of values[2] containing "2" is 0.0)
		// ["11\t2", "2", "33"] -> 0.9 (Because the frequency of values[0] containing "2" is 0.0)
		// ["11", "2\t2", "33"] -> 1.0 (Because the frequency of values[1] containing "2" is 1.0)
		printResult(mender);
	}

	private static void printResult(final DsvMender mender) {
		final var result = mender.getLastResult().orElseThrow();
		System.out.println("Candidates:");
		for (final var candidate : result.getCandidates()) {
			System.out.println(
					Arrays.stream(candidate.getValue())
							.map(Strings::quote)
							.collect(Collectors.joining(", "))
							+ " -> "
							+ candidate.getScore()
			);
		}
		System.out.println();
		System.out.println("Best candidate:");
		final var bestCandidate = result.getBestCandidate();
		System.out.println(
				Arrays.stream(bestCandidate.getValue())
						.map(Strings::quote)
						.collect(Collectors.joining(", "))
						+ " -> "
						+ bestCandidate.getScore()
		);
	}
}