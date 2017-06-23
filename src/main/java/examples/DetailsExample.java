package examples;

import java.util.Arrays;
import java.util.Map;

import org.mender.MenderException;
import org.mender.dsv.DsvMender;

public class DetailsExample {

	public static void main(final String... args) {
		final DsvMender mender = DsvMender.builder("\t", 3)
				.withContainsEstimations("1")
				.withContainsEstimations("2")
				.withContainsEstimations("3")
				.build();
		mender.fit("11	22	33");
		mender.fit("1111	2222	3333");
		
		try {
			printFixDetails(mender, "1111	1111	22222222	33333333");
		} catch (final MenderException e) {
			e.printStackTrace();
		}
	}
	
	private static void printFixDetails(final DsvMender mender, final String toFix) throws MenderException {
		final String[] best = mender.fix(toFix);
		System.out.println("Computed nodes:");
		final Map<String[], Double> nodes = mender.getNodeScores();
		for (final Map.Entry<String[], Double> node : nodes.entrySet()) {
			System.out.println(Arrays.toString(node.getKey()) + " (score=" + node.getValue() + ")");
		}
		System.out.println();
		System.out.println("Best node: " + Arrays.toString(best) + " (score=" + mender.getScore() + ")");
		System.out.println();
	}
}