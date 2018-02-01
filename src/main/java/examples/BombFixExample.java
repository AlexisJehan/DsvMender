/*
MIT License

Copyright (c) 2018 Alexis Jehan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package examples;

import java.util.regex.Pattern;

import org.mender.MenderException;
import org.mender.dsv.DsvMender;

public final class BombFixExample {

	public static void main(final String... args) {
		final DsvMender mender = DsvMender.builder(",", 4, 30) // Node depth of 30 to increase default value
				.withNonEmptyConstraints()
				.withPatternConstraint(0, Pattern.compile("\\d+"))
				.withStartsWithConstraint(2, "\"")
				.withEndsWithConstraint(2, "\"")
				.build();
		try {
			// Takes so many memory and may fail because of nodes bomb
			//printValues(mender.fix("123,Thug-man,\"Deal with it,,,,,,,,,,,,,,,,,,,,,,,,\",2001-02-03"));

			// Optimized for repeated delimiter chars
			printValues(mender.optimizedFix("123,Thug-man,\"Deal with it,,,,,,,,,,,,,,,,,,,,,,,,\",2001-02-03", 1));
		} catch (final MenderException e) {
			e.printStackTrace();
		}
	}

	private static void printValues(final String[] values) {
		for (final String value : values) {
			System.out.print("\"" + value.replace("\"", "\\\"") + "\"\t");
		}
		System.out.println();
	}
}