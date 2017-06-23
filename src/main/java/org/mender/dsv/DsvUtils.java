/*
MIT License

Copyright (c) 2017 Alexis Jehan

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
package org.mender.dsv;

import java.util.Arrays;

/**
 * <p>Utility class that for now only provides an optimization strategy.</p>
 *
 * @since 1.0
 */
final class DsvUtils {

	/**
	 * <p>Constructor not available</p>
	 */
	private DsvUtils() {
		throw new AssertionError();
	}
	
	/**
	 * <p>This function aims to considerably improve performances of the {@code DsvMender.fix()} method for specials
	 * cases, especially when the delimiter occurs several times consecutively and lead to nodes explosion.</p>
	 * 
	 * @param values Array of values to optimize
	 * @param delimiter DSV delimiter string
	 * @param nbColumns DSV number of columns
	 * @param nbSafeColumns An indication of possibly consecutive empty values
	 * @return Array of optimized values
	 */
	static String[] optimize(final String[] values, final String delimiter, final int nbColumns, final int nbSafeColumns) {
		String[] result = Arrays.copyOf(values, values.length);
		while (nbColumns < result.length) {
			
			// Interval of longest consecutive empty values
			int dmin = 0;
			int dmax = 0;
			int s = -1;
			for (int i = 0; i < result.length; ++i) {
				if (-1 == s && result[i].isEmpty()) {
					s = i;
					continue;
				}
				if (-1 != s && !result[i].isEmpty()) {
					if (dmax - dmin < i - s) {
						dmin = s;
						dmax = i;
					}
					s = -1;
				}
			}
			if (-1 != s && dmax - dmin < result.length - s) {
				dmin = s;
				dmax = result.length;
			}
			
			if (dmax - dmin > 2 * nbSafeColumns + 1) {
				
				// Merging consecutive empty values while keeping a safe amount on each side
				for (int i = dmin + nbSafeColumns + 1; i < dmax - nbSafeColumns; ++i) {
					result = remove(result, dmin + nbSafeColumns + 1);
					result[dmin + nbSafeColumns] = result[dmin + nbSafeColumns] + delimiter;
				}
			} else {
				break;
			}
		}
		return result;
	}
	
	/**
	 * <p>Remove an element from an array at a specific index.</p>
	 * 
	 * @param array The array to remove the element from
	 * @param index Index of the element to remove in the array
	 * @return A new array without the removed element
	 */
	private static String[] remove(final String[] array, final int index) {
		final String[] result = new String[array.length - 1];
		if (0 < index) {
			System.arraycopy(array, 0, result, 0, index);
		}
		if (index < array.length - 1) {
			System.arraycopy(array, index + 1, result, index, array.length - index - 1);
		}
		return result;
	}
}