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