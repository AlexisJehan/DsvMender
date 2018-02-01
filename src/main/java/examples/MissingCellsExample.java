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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.mender.MenderException;
import org.mender.dsv.DsvMender;
import org.mender.dsv.DsvReader;

public final class MissingCellsExample {

	public static void main(final String... args) {
		final DsvMender mender = DsvMender.builder("\t", 5)
				.withPatternConstraint(0, Pattern.compile("[0-9]+")) // The year column is always numerical
				.withMaxLengthConstraint(1, 6) // The make column can't be more than 10 characters
				.build();
		try (final DsvReader reader = new DsvReader(mender, new InputStreamReader(MissingCellsExample.class.getClassLoader().getResourceAsStream("missing_cells.tsv")))) {
			try {
				printValues(reader.readHeader());
				String[] row;
				while (null != (row = reader.readRow())) {
					printValues(row);
				}
			} catch (final MenderException e) {
				e.printStackTrace();
			}
		} catch (final IOException e) {
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