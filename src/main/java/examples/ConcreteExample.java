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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class ConcreteExample {

	private static final String DELIMITER = ",";
	private static final int LENGTH = 3;

	// Source: https://en.wikipedia.org/wiki/Java_version_history
	private static final String DATA = String.join(
			System.lineSeparator(),
			String.join(DELIMITER, "Release", "Release date", "Highlights"),
			String.join(DELIMITER, "Java SE 9", "2017-09-21", "Initial release"),
			String.join(DELIMITER, "Java SE 9.0.1", "2017-10-17", "October 2017 security fixes and critical bug fixes"),
			String.join(DELIMITER, "Java SE 9.0.4", "2018-01-16", "Final release for JDK 9; January 2018 security fixes and critical bug fixes"),
			String.join(DELIMITER, "Java SE 10", "2018-03-20", "Initial release"),
			String.join(DELIMITER, "Java SE 10.0.1", "2018-04-17", "Security fixes, 5 bug fixes"), // One value contains the delimiter
			String.join(DELIMITER, "Java SE 11", "2018-09-25", "Initial release"),
			String.join(DELIMITER, "Java SE 11.0.1", "2018-10-16", "Security & bug fixes"),
			String.join(DELIMITER, "Java SE 11.0.2", "2019-01-15", "Security & bug fixes"),
			String.join(DELIMITER, "Java SE 12", "Initial release") // Missing the release date value
	);

	private ConcreteExample() {
		// Not available
	}

	public static void main(final String... args) throws IOException {
		final var mender = DsvMender.builder()
				.withDelimiter(DELIMITER)
				.withLength(LENGTH)
				.withConstraint(value -> value.startsWith("Java SE"), 0) // values[0] must start with "Java SE"
				.withConstraint(value -> value.isEmpty() || 10 == value.length(), 1)// values[1] must be empty or have a length of 10
				.build();
		try (final var reader = new BufferedReader(new StringReader(DATA))) {
			printValues(Strings.split(mender.getDelimiter(), reader.readLine()).toArray(String[]::new)); // Header
			String row;
			while (null != (row = reader.readLine())) {
				printValues(mender.mend(row));
			}
		}
	}

	private static void printValues(final String[] values) {
		System.out.println(
				Arrays.stream(values)
						.map(Strings::quote)
						.collect(Collectors.joining(", "))
		);
	}
}