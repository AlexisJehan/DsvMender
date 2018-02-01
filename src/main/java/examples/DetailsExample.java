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
		mender.fit("11\t22\t33");
		mender.fit("1111\t2222\t3333");
		try {
			printFixDetails(mender, "1111\t1111\t22222222\t33333333");
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