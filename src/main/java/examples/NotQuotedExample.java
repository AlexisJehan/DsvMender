package examples;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.mender.MenderException;
import org.mender.dsv.DsvMender;
import org.mender.dsv.DsvReader;

public final class NotQuotedExample {
	
	public static void main(final String... args) {
		final DsvMender mender = DsvMender.builder(",", 5)
				.withLengthEstimations() // Estimating the length of the value for every columns
				.withContainsEstimations(" ") // Estimating if the value contains a space character for every columns
				.withPatternConstraint(0, Pattern.compile("[0-9]+")) // The ID column is always numerical, not empty
				.withLengthConstraint(3, 10) // The birthday column always contains 10 characters
				.build();
		
		try (final DsvReader reader = new DsvReader(mender, new InputStreamReader(NotQuotedExample.class.getClassLoader().getResourceAsStream("not_quoted.csv")))) {
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