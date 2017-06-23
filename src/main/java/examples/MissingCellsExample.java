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