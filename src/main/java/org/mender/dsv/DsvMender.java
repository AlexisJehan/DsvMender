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
package org.mender.dsv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mender.Mender;
import org.mender.MenderException;

/**
 * <p>Main component to use to repair malformed DSV data.</p>
 * 
 * @since 1.0
 */
public class DsvMender implements Mender<String[]> {

	/**
	 * <p>{@code Nodes} is an object that wraps and optimizes a collection of {@code String} arrays for
	 * {@code DsvMender} uses.</p>
	 * 
	 * <p>Optimizations:<br>
	 * - Unlike a {@link java.util.HashMap}, the hash of the String array is computed using a custom hash function
	 * that serves as the key of the map.<br>
	 * - Because of large uses of equivalents {@code String} objects, {@code String.intern()} is called for all of
	 * them.</p>
	 *
	 * @since 1.0
	 */
	private static class Nodes {

		/**
		 * <p>Delegate map with hash codes as keys and {@code String} arrays as values.</p>
		 */
		private final Map<Integer, String[]> delegate = new HashMap<>();

		/**
		 * <p>Add all entries to the {@code Nodes}.</p>
		 * 
		 * @param entries Collection of entries to add
		 */
		void addAll(final Collection<String[]> entries) {
			for (final String[] entry : entries) {
				final int hashCode = customHashCode(entry);
				if (delegate.containsKey(hashCode)) {
					continue;
				}
				for (int i = 0; i < entry.length; ++i) {
					entry[i] = entry[i].intern();
				}
				delegate.put(hashCode, entry);
			}
		}

		/**
		 * <p>Return a collection of {@code Nodes} values.</p>
		 * 
		 * @return The collection of values
		 */
		Collection<String[]> values() {
			return delegate.values();
		}

		/**
		 * <p>Empty all nodes.</p>
		 */
		void clear() {
			delegate.clear();
		}

		/**
		 * <p>Custom hash code generation of {@code String} arrays, because of a high risk of collision with standard 
		 * {@code Arrays.hashCode} function.</p>
		 * <p>Example of collision:<br>
		 * {@code Arrays.hashCode(new String[] {"o", "ooo"})} gives 114625<br>
		 * {@code Arrays.hashCode(new String[] {"oo", "oo"})} gives 114625 too</p>
		 * 
		 * @param array The array to get the hash code from
		 * @return Computed hash code
		 */
		private static int customHashCode(final String[] array) {
			int arrayHashCode = 1;
			for (int i = 0; i < array.length; ++i) {
				final String string = array[i];
				int stringHashCode = 0;
				if (null != string) {
					final int length = string.length();
					for (int j = 0; j < length; ++j) {
						stringHashCode = 257 * stringHashCode + string.charAt(j);
					}
				}
				arrayHashCode = 31 * arrayHashCode + stringHashCode;
			}
			return arrayHashCode;
		}
	}

	/**
	 * <p>Default maximum depth of the nodes tree.</p>
	 */
	static final int DEFAULT_MAX_DEPTH = 20;

	/**
	 * <p>{@code DsvEvaluator} object.</p>
	 */
	private final DsvEvaluator evaluator;

	/**
	 * <p>String delimiter of the DSV data.</p>
	 */
	private final String delimiter;

	/**
	 * <p>Number of columns of the DSV data.</p>
	 */
	private final int nbColumns;

	/**
	 * <p>Maximum depth of the nodes tree.</p>
	 */
	private final int maxDepth;

	/**
	 * <p>Map that associates nodes to their scores.</p>
	 */
	private Map<String[], Double> nodeScores;

	/**
	 * <p>Best score, of the returned node.</p>
	 */
	private double score;

	/**
	 * <p>Package-private constructor used by the {@link DsvBuilder} class.</p>
	 * 
	 * @param evaluator {@code DsvEvaluator} to use
	 * @param delimiter {@code String} delimiter of the DSV data
	 * @param nbColumns Number of columns of the DSV data
	 * @param maxDepth Maximum depth of the nodes tree
	 * @throws NullPointerException If the evaluator or the delimiter are {@code null}
	 * @throws IllegalArgumentException If the number of columns if lower than 2
	 */
	DsvMender(final DsvEvaluator evaluator, final String delimiter, final int nbColumns, final int maxDepth) {
		if (null == evaluator) {
			throw new NullPointerException("Invalid evaluator (not null expected)");
		}
		if (null == delimiter) {
			throw new NullPointerException("Invalid delimiter (not null expected)");
		}
		if (2 > nbColumns) {
			throw new IllegalArgumentException("Invalid number of columns: " + nbColumns + " (greater than or equal to 2 expected)");
		}
		this.evaluator = evaluator;
		this.delimiter = delimiter;
		this.nbColumns = nbColumns;
		this.maxDepth = maxDepth;
	}

	/**
	 * <p>Fit the evaluator with a row from the DSV data, only if it is well-formed.</p>
	 * 
	 * @param row Row of separated values
	 * @throws NullPointerException If the row is {@code null}
	 */
	public void fitIfValid(final String row) {
		if (null == row) {
			throw new NullPointerException("Invalid row (not null expected)");
		}
		fitIfValid(StringUtils.splitByWholeSeparatorPreserveAllTokens(row, delimiter));
	}

	/**
	 * <p>Fit the evaluator with a row from the DSV data, only if it is well-formed.</p>
	 * 
	 * @param values Array of separated values
	 * @throws NullPointerException If values are {@code null}
	 */
	public void fitIfValid(final String[] values) {
		if (null == values) {
			throw new NullPointerException("Invalid values (not null expected)");
		}
		if (nbColumns == values.length && evaluator.checkConstraints(values)) {
			fit(values);
		}
	}

	/**
	 * <p>Fit the evaluator with a well-formed row from the DSV data.</p>
	 * 
	 * @param row Row of well-formed separated values
	 * @throws NullPointerException If the row is {@code null}
	 */
	public void fit(final String row) {
		if (null == row) {
			throw new NullPointerException("Invalid row (not null expected)");
		}
		fit(StringUtils.splitByWholeSeparatorPreserveAllTokens(row, delimiter));
	}

	/**
	 * <p>Fit the evaluator with a well-formed row from the DSV data.</p>
	 * 
	 * @param values Array of well-formed separated values
	 * @throws NullPointerException If values are {@code null}
	 * @throws IllegalArgumentException If the number of values is different from the number of columns or if values
	 * don't pass constraints
	 */
	@Override
	public void fit(final String[] values) {
		if (null == values) {
			throw new NullPointerException("Invalid values (not null expected)");
		}
		if (nbColumns != values.length) {
			throw new IllegalArgumentException("Invalid number of values: " + values.length + " (" + nbColumns + " expected)");
		}
		if (!evaluator.checkConstraints(values)) {
			throw new IllegalArgumentException("Invalid values: " + Arrays.toString(values) + " (constraints not passed)");
		}
		evaluator.adjustEstimations(values);
	}

	/**
	 * <p>In some cases you should consider using this method instead of {@code fix()} for better performances and less
	 * memory consumption when lot of delimiters are appearing consecutively.</p>
	 * 
	 * @param row Row of malformed separated values
	 * @param nbSafeColumns The maximum number of possibly consecutive empty values in a well-formed row
	 * @return Fixed extracted values from the row
	 * @throws NullPointerException If the row is {@code null}
	 * @throws IllegalArgumentException If the number of safe columns is negative
	 * @throws MenderException In particular cases the {@code fix()} method might not work because of used criteria
	 * @see DsvUtils
	 */
	public String[] optimizedFix(final String row, final int nbSafeColumns) throws MenderException {
		if (null == row) {
			throw new NullPointerException("Invalid row (not null expected)");
		}
		return optimizedFix(StringUtils.splitByWholeSeparatorPreserveAllTokens(row, delimiter), nbSafeColumns);
	}

	/**
	 * <p>In some cases you should consider using this method instead of {@code fix()} for better performances and less
	 * memory consumption when lot of delimiters are appearing consecutively.</p>
	 * 
	 * @param values Array of malformed separated values
	 * @param nbSafeColumns The maximum number of possibly consecutive empty values in a well-formed row
	 * @return Fixed values
	 * @throws NullPointerException If values are {@code null}
	 * @throws IllegalArgumentException If the number of safe columns is negative
	 * @throws MenderException In particular cases the {@code fix()} method might not work because of used criteria
	 * @see DsvUtils
	 */
	public String[] optimizedFix(final String[] values, final int nbSafeColumns) throws MenderException {
		if (null == values) {
			throw new NullPointerException("Invalid values (not null expected)");
		}
		if (0 > nbSafeColumns) {
			throw new IllegalArgumentException("Invalid number of safe columns (greater than or equal to 0 expected)");
		}
		return fix(DsvUtils.optimize(values, delimiter, nbColumns, nbSafeColumns));
	}

	/**
	 * <p>Fix a row from the DSV data using previously learned ones, only if it is malformed.</p>
	 * 
	 * @param row Row of separated values
	 * @return Fixed extracted values from the row if it was malformed
	 * @throws MenderException In particular cases that method might not work because of used criteria
	 * @throws NullPointerException If the row is {@code null}
	 */
	public String[] fixIfNotValid(final String row) throws MenderException {
		if (null == row) {
			throw new NullPointerException("Invalid row (not null expected)");
		}
		return fixIfNotValid(StringUtils.splitByWholeSeparatorPreserveAllTokens(row, delimiter));
	}

	/**
	 * <p>Fix a row from the DSV data using previously learned ones, only if it is malformed.</p>
	 * 
	 * @param values Array of separated values
	 * @return Fixed values if they were malformed
	 * @throws MenderException In particular cases that method might not work because of used criteria
	 * @throws NullPointerException If values are {@code null}
	 */
	public String[] fixIfNotValid(final String[] values) throws MenderException {
		if (null == values) {
			throw new NullPointerException("Invalid values (not null expected)");
		}
		if (nbColumns != values.length) {
			return fix(values);
		} else {
			return values;
		}
	}

	/**
	 * <p>Fix a malformed row from the DSV data using previously learned ones.</p>
	 * 
	 * @param row Row of malformed separated values
	 * @return Fixed extracted values from the row
	 * @throws MenderException In particular cases that method might not work because of used criteria
	 * @throws NullPointerException If the row is {@code null}
	 */
	public String[] fix(final String row) throws MenderException {
		if (null == row) {
			throw new NullPointerException("Invalid row (not null expected)");
		}
		return fix(StringUtils.splitByWholeSeparatorPreserveAllTokens(row, delimiter));
	}

	/**
	 * <p>Fix a malformed row from the DSV data using previously learned ones.</p>
	 * 
	 * @param values Array of malformed separated values
	 * @return Fixed values
	 * @throws MenderException In particular cases that method might not work because of used criteria
	 * @throws NullPointerException If values are {@code null}
	 * @throws IllegalArgumentException If the number of values is equal to the number of columns
	 */
	@Override
	public String[] fix(final String[] values) throws MenderException {
		if (null == values) {
			throw new NullPointerException("Invalid values (not null expected)");
		}
		if (nbColumns == values.length) {
			throw new IllegalArgumentException("Invalid number of values: " + values.length + " (different from " + nbColumns + " expected)");
		}
		if (maxDepth < Math.abs(nbColumns - values.length - 2)) {
			throw new MenderException("Could not fix because the depth should be less than or equal to " + maxDepth + " (" + Math.abs(nbColumns - values.length - 2) + " for given values)");
		}
		final Nodes nodes = new Nodes();
		if (nbColumns < values.length) {
			nodes.addAll(generateJoinChildNodes(values));
			for (int i = nbColumns; i < values.length - 1; ++i) {
				final List<String[]> subNodes = new ArrayList<>(nodes.values());
				nodes.clear();
				for (final String[] subNode : subNodes) {
					nodes.addAll(generateJoinChildNodes(subNode));
				}
			}
		} else {
			nodes.addAll(generateShiftChildNodes(values));
			for (int i = nbColumns; i > values.length + 1; --i) {
				final List<String[]> subNodes = new ArrayList<>(nodes.values());
				nodes.clear();
				for (final String[] subNode : subNodes) {
					nodes.addAll(generateShiftChildNodes(subNode));
				}
			}
		}
		nodeScores = new HashMap<>();
		for (final String[] node : nodes.values()) {
			final double nodeScore = evaluator.evaluate(node);
			if (0 < nodeScore) {
				nodeScores.put(node, nodeScore);
			}
		}
		if (nodeScores.isEmpty()) {
			throw new MenderException("No solution has been found for values: \"" + Arrays.toString(values) + "\" (consider using others estimations or constraints)");
		}
		final Map.Entry<String[], Double> bestNode = Collections.max(nodeScores.entrySet(), Map.Entry.comparingByValue());
		score = bestNode.getValue();
		return bestNode.getKey();
	}

	/**
	 * <p>Create child nodes from a parent by computing all possible merging cases of values. Each value is attempted to
	 * be merged with the next one using the delimiter as separator.</p>
	 * 
	 * @param parent The parent node
	 * @return List of generated child nodes
	 */
	private List<String[]> generateJoinChildNodes(final String[] parent) {
		final List<String[]> children = new ArrayList<>(parent.length - 1);
		for (int i = 0; i < parent.length - 1; ++i) {
			final String[] child = new String[parent.length - 1];
			for (int j = 0; j < parent.length - 1; ++j) {
				if (j == i) {
					child[j] = parent[j] + delimiter + parent[j + 1];
				} else {
					child[j] = parent[j < i ? j : j + 1];
				}
			}
			children.add(child);
		}
		return children;
	}

	/**
	 * <p>Create child nodes from a parent by computing all possible shifting cases of values. An empty value is added
	 * at any position between all values.</p>
	 * 
	 * @param parent The parent node
	 * @return List of generated child nodes
	 */
	private List<String[]> generateShiftChildNodes(final String[] parent) {
		final List<String[]> children = new ArrayList<>(parent.length + 1);
		for (int i = 0; i < parent.length + 1; ++i) {
			final String[] child = new String[parent.length + 1];
			for (int j = 0; j < parent.length + 1; ++j) {
				if (j == i) {
					child[j] = "";
				} else {
					child[j] = parent[j < i ? j : j - 1];
				}
			}
			children.add(child);
		}
		return children;
	}

	/**
	 * <p>Return a map that associates scores to each node generated by the last call to {@code fix()}.</p>
	 * 
	 * @return An unmodifiable map of nodes' scores
	 */
	public Map<String[], Double> getNodeScores() {
		return Collections.unmodifiableMap(nodeScores);
	}

	/**
	 * <p>Return the score of the more relevant node used by the last call to {@code fix()}.</p>
	 * 
	 * @return The more relevant score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * <p>Create a new {@link DsvBuilder} instance to build a {@code DsvMender} object, using a {@code char} delimiter
	 * and the default maximum depth.</p>
	 * 
	 * @param delimiter {@code char} delimiter of the DSV data
	 * @param nbColumns Number of columns of the DSV data
	 * @return The instantiated {@link DsvBuilder} instance
	 */
	public static DsvBuilder builder(final char delimiter, final int nbColumns) {
		return builder(Character.toString(delimiter), nbColumns);
	}

	/**
	 * <p>Create a new {@link DsvBuilder} instance to build a {@code DsvMender} object, using a {@code String} delimiter
	 * and the default maximum depth.</p>
	 * 
	 * @param delimiter {@code String} delimiter of the DSV data
	 * @param nbColumns Number of columns of the DSV data
	 * @return The instantiated {@link DsvBuilder} instance
	 */
	public static DsvBuilder builder(final String delimiter, final int nbColumns) {
		return builder(delimiter, nbColumns, DEFAULT_MAX_DEPTH);
	}

	/**
	 * <p>Create a new {@link DsvBuilder} instance to build a {@code DsvMender} object, using a {@code char} delimiter
	 * and a non-default maximum depth.</p>
	 * 
	 * @param delimiter {@code char} delimiter of the DSV data
	 * @param nbColumns Number of columns of the DSV data
	 * @param maxDepth Maximum depth of the nodes tree
	 * @return The instantiated {@link DsvBuilder} instance
	 * @throws NullPointerException If the delimiter is {@code null}
	 * @throws IllegalArgumentException If the number of columns is lower than 2
	 */
	public static DsvBuilder builder(final char delimiter, final int nbColumns, final int maxDepth) {
		return builder(Character.toString(delimiter), nbColumns, maxDepth);
	}

	/**
	 * <p>Create a new {@link DsvBuilder} instance to build a {@code DsvMender} object, using a {@code String} delimiter
	 * and a non-default maximum depth.</p>
	 * 
	 * @param delimiter {@code String} delimiter of the DSV data
	 * @param nbColumns Number of columns of the DSV data
	 * @param maxDepth Maximum depth of the nodes tree
	 * @return The instantiated {@link DsvBuilder} instance
	 * @throws NullPointerException If the delimiter is {@code null}
	 * @throws IllegalArgumentException If the number of columns is lower than 2
	 */
	public static DsvBuilder builder(final String delimiter, final int nbColumns, final int maxDepth) {
		if (null == delimiter) {
			throw new NullPointerException("Invalid delimiter (not null expected)");
		}
		if (0 > nbColumns) {
			throw new IllegalArgumentException("Invalid number of columns: " + nbColumns + " (greater than or equal to 2 expected)");
		}
		return new DsvBuilder(delimiter, nbColumns, maxDepth);
	}

	/**
	 * <p>Create a new {@link DsvBuilder} instance automatically, using a {@code char} delimiter and configured with
	 * empty-string and length-string estimations.</p>
	 * 
	 * @param delimiter {@code char} delimiter of the DSV data
	 * @param nbColumns Number of columns of the DSV data
	 * @return The instantiated {@link DsvBuilder} instance
	 */
	public static DsvMender auto(final char delimiter, final int nbColumns) {
		return auto(Character.toString(delimiter), nbColumns);
	}

	/**
	 * <p>Create a new {@link DsvBuilder} instance automatically, using a {@code String} delimiter and configured with
	 * empty-string and length-string estimations.</p>
	 * 
	 * @param delimiter {@code String} delimiter of the DSV data
	 * @param nbColumns Number of columns of the DSV data
	 * @return The instantiated {@link DsvBuilder} instance
	 */
	public static DsvMender auto(final String delimiter, final int nbColumns) {
		return builder(delimiter, nbColumns, DEFAULT_MAX_DEPTH)
				.withEmptyEstimations()
				.withLengthEstimations()
				.build();
	}
}