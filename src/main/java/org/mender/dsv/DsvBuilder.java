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

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.mender.criteria.Constraint;
import org.mender.criteria.Estimation;

/**
 * <p>Builder pattern implementation to easily create new {@link DsvMender} instances.</p>
 * 
 * @since 1.0
 */
public final class DsvBuilder {

	/**
	 * <p>{@code DsvEvaluator} object.</p>
	 */
	private final DsvEvaluator evaluator = new DsvEvaluator();

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
	 * <p>Construct a new {@code DsvBuilder}.</p>
	 * 
	 * @param delimiter {@code String} delimiter of the DSV data
	 * @param nbColumns Number of columns of the DSV data
	 * @param maxDepth Maximum depth of the nodes tree
	 */
	DsvBuilder(final String delimiter, final int nbColumns, final int maxDepth) {
		this.delimiter = delimiter;
		this.nbColumns = nbColumns;
		this.maxDepth = maxDepth;
	}

	// EMPTY
	/**
	 * <p>Add {@code String.isEmpty()} estimations to all columns of the DSV data.</p>
	 * 
	 * @return The {@code DsvBuilder} instance
	 */
	public DsvBuilder withEmptyEstimations() {
		return withEstimations(String::isEmpty);
	}

	/**
	 * <p>Add a {@code String.isEmpty()} estimation to the column at specified index of the DSV data.</p>
	 * 
	 * @param index Index of the column
	 * @return The {@code DsvBuilder} instance
	 */
	public DsvBuilder withEmptyEstimation(final int index) {
		return withEstimation(index, String::isEmpty);
	}

	/**
	 * <p>Add a {@code String.isEmpty()} constraint to the column at specified index of the DSV data. In others words
	 * that means this column should always be empty.</p>
	 * 
	 * @param index Index of the column
	 * @return The {@code DsvBuilder} instance
	 */
	public DsvBuilder withEmptyConstraint(final int index) {
		return withConstraint(index, String::isEmpty);
	}

	// NON EMPTY
	/**
	 * <p>Add {@code String} non-empty constraints to all columns of the DSV data. In others words that means all
	 * columns should never be empty.</p>
	 * 
	 * @return The {@code DsvBuilder} instance
	 */
	public DsvBuilder withNonEmptyConstraints() {
		return withConstraints(value -> !value.isEmpty());
	}

	/**
	 * <p>Add a {@code String} non-empty constraint to the column at specified index of the DSV data. In others words
	 * that means this column should never be empty.</p>
	 * 
	 * @param index Index of the column
	 * @return The {@code DsvBuilder} instance
	 */
	public DsvBuilder withNonEmptyConstraint(final int index) {
		return withConstraint(index, value -> !value.isEmpty());
	}

	// LENGTH
	/**
	 * <p>Add {@code String.length()} estimations to all columns of the DSV data.</p>
	 * 
	 * @return The {@code DsvBuilder} instance
	 */
	public DsvBuilder withLengthEstimations() {
		return withEstimations(String::length);
	}

	/**
	 * <p>Add a {@code String.length()} estimation to the column at specified index of the DSV data.</p>
	 * 
	 * @param index Index of the column
	 * @return The {@code DsvBuilder} instance
	 */
	public DsvBuilder withLengthEstimation(final int index) {
		return withEstimation(index, String::length);
	}

	/**
	 * <p>Add a {@code String.length()} constraint to the column at specified index of the DSV data. In others words
	 * that means each value of this column should always have that length.</p>
	 * 
	 * @param index Index of the column
	 * @param length The required length for specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws IllegalArgumentException If the length is less than 0
	 */
	public DsvBuilder withLengthConstraint(final int index, final int length) {
		if (0 > length) {
			throw new IllegalArgumentException("Invalid length: " + length + " (greater than or equal to 0 expected)");
		}
		return withConstraint(index, value -> length == value.length());
	}

	// MIN LENGTH
	/**
	 * <p>Add {@code String} minimum length estimations to all columns of the DSV data.</p>
	 * 
	 * @param minLength The estimated minimum length for all column values
	 * @return The {@code DsvBuilder} instance
	 * @throws IllegalArgumentException If the minimum length is less than or equal to 0
	 */
	public DsvBuilder withMinLengthEstimations(final int minLength) {
		if (1 > minLength) {
			throw new IllegalArgumentException("Invalid minimum length: " + minLength + " (greater than 0 expected)");
		}
		return withEstimations(value -> minLength <= value.length());
	}

	/**
	 * <p>Add a {@code String} minimum length estimation to the column at specified index of the DSV data.</p>
	 * 
	 * @param index Index of the column
	 * @param minLength The estimated minimum length for specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws IllegalArgumentException If the minimum length is less than or equal to 0
	 */
	public DsvBuilder withMinLengthEstimation(final int index, final int minLength) {
		if (1 > minLength) {
			throw new IllegalArgumentException("Invalid minimum length: " + minLength + " (greater than 0 expected)");
		}
		return withEstimation(index, value -> minLength <= value.length());
	}

	/**
	 * <p>Add a {@code String} minimum length constraint to the column at specified index of the DSV data. In others
	 * words that means each value of this column should always have that minimum length.</p>
	 * 
	 * @param index Index of the column
	 * @param minLength The required minimum length for specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws IllegalArgumentException If the minimum length is less than or equal to 0
	 */
	public DsvBuilder withMinLengthConstraint(final int index, final int minLength) {
		if (1 > minLength) {
			throw new IllegalArgumentException("Invalid minimum length: " + minLength + " (greater than 0 expected)");
		}
		return withConstraint(index, value -> minLength <= value.length());
	}

	// MAX LENGTH
	/**
	 * <p>Add {@code String} maximum length estimations to all columns of the DSV data.</p>
	 * 
	 * @param maxLength The estimated maximum length for all column values
	 * @return The {@code DsvBuilder} instance
	 * @throws IllegalArgumentException If the maximum length is less than or equal to 0
	 */
	public DsvBuilder withMaxLengthEstimations(final int maxLength) {
		if (1 > maxLength) {
			throw new IllegalArgumentException("Invalid maximum length: " + maxLength + " (greater than 0 expected)");
		}
		return withEstimations(value -> maxLength >= value.length());
	}

	/**
	 * <p>Add a {@code String} maximum length estimation to the column at specified index of the DSV data.</p>
	 * 
	 * @param index Index of the column
	 * @param maxLength The estimated maximum length for specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws IllegalArgumentException If the maximum length is less than or equal to 0
	 */
	public DsvBuilder withMaxLengthEstimation(final int index, final int maxLength) {
		if (1 > maxLength) {
			throw new IllegalArgumentException("Invalid maximum length: " + maxLength + " (greater than 0 expected)");
		}
		return withEstimation(index, value -> maxLength >= value.length());
	}

	/**
	 * <p>Add a {@code String} maximum length constraint to the column at specified index of the DSV data. In others
	 * words that means each value of this column should always have that maximum length.</p>
	 * 
	 * @param index Index of the column
	 * @param maxLength The required maximum length for specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws IllegalArgumentException If the maximum length is less than or equal to 0
	 */
	public DsvBuilder withMaxLengthConstraint(final int index, final int maxLength) {
		if (1 > maxLength) {
			throw new IllegalArgumentException("Invalid maximum length: " + maxLength + " (greater than 0 expected)");
		}
		return withConstraint(index, value -> maxLength >= value.length());
	}

	// RANGE LENGTH
	/**
	 * <p>Add {@code String} range length estimations to all columns of the DSV data.</p>
	 * 
	 * @param minLength The estimated minimum length for all column values
	 * @param maxLength The estimated maximum length for all column values
	 * @return The {@code DsvBuilder} instance
	 * @throws IllegalArgumentException If the minimum length is less than or equal to 0 or if the maximum length is
	 * greater than the minimum length
	 */
	public DsvBuilder withRangeLengthEstimations(final int minLength, final int maxLength) {
		if (1 > minLength) {
			throw new IllegalArgumentException("Invalid maximum length: " + minLength + " (greater than 0 expected)");
		}
		if (minLength + 1 > maxLength) {
			throw new IllegalArgumentException("Invalid maximum length: " + maxLength + " (greater than minimum length expected)");
		}
		return withEstimations(value -> minLength <= value.length() && maxLength >= value.length());
	}

	/**
	 * <p>Add a {@code String} range length estimation to the column at specified index of the DSV data.</p>
	 * 
	 * @param index Index of the column
	 * @param minLength The estimated minimum length for specified column values
	 * @param maxLength The estimated maximum length for specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws IllegalArgumentException If the minimum length is less than or equal to 0 or if the maximum length is
	 * greater than the minimum length
	 */
	public DsvBuilder withRangeLengthEstimation(final int index, final int minLength, final int maxLength) {
		if (1 > minLength) {
			throw new IllegalArgumentException("Invalid maximum length: " + minLength + " (greater than 0 expected)");
		}
		if (minLength + 1 > maxLength) {
			throw new IllegalArgumentException("Invalid maximum length: " + maxLength + " (greater than minimum length expected)");
		}
		return withEstimation(index, value -> minLength <= value.length() && maxLength >= value.length());
	}

	/**
	 * <p>Add a {@code String} range length constraint to the column at specified index of the DSV data. In others
	 * words that means each value of this column should always have that minimum and maximum lengths.</p>
	 * 
	 * @param index Index of the column
	 * @param minLength The required minimum length for specified column values
	 * @param maxLength The required maximum length for specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws IllegalArgumentException If the minimum length is less than or equal to 0 or if the maximum length is
	 * greater than the minimum length
	 */
	public DsvBuilder withRangeLengthConstraint(final int index, final int minLength, final int maxLength) {
		if (1 > minLength) {
			throw new IllegalArgumentException("Invalid maximum length: " + minLength + " (greater than 0 expected)");
		}
		if (minLength + 1 > maxLength) {
			throw new IllegalArgumentException("Invalid maximum length: " + maxLength + " (greater than minimum length expected)");
		}
		return withConstraint(index, value -> minLength <= value.length() && maxLength >= value.length());
	}

	// PATTERN
	/**
	 * <p>Add {@link Pattern} estimations to all columns of the DSV data.</p>
	 * 
	 * @param pattern The estimated matched {@code Pattern} by all column values
	 * @return The {@code DsvBuilder} instance
	 * @throws NullPointerException If the pattern is null
	 */
	public DsvBuilder withPatternEstimations(final Pattern pattern) {
		if (null == pattern) {
			throw new NullPointerException("Invalid pattern (not null expected)");
		}
		return withEstimations(value -> pattern.matcher(value).matches());
	}

	/**
	 * <p>Add a {@link Pattern} estimation to the column at specified index of the DSV data.</p>
	 * 
	 * @param index Index of the column
	 * @param pattern The estimated matched {@code Pattern} by specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws NullPointerException If the pattern is {@code null}
	 */
	public DsvBuilder withPatternEstimation(final int index, final Pattern pattern) {
		if (null == pattern) {
			throw new NullPointerException("Invalid pattern (not null expected)");
		}
		return withEstimation(index, value -> pattern.matcher(value).matches());
	}

	/**
	 * <p>Add a {@link Pattern} constraint to the column at specified index of the DSV data. In others
	 * words that means each value of this column should always match that {@code Pattern}.</p>
	 * 
	 * @param index Index of the column
	 * @param pattern The required matched {@code Pattern} by specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws NullPointerException If the pattern is {@code null}
	 */
	public DsvBuilder withPatternConstraint(final int index, final Pattern pattern) {
		if (null == pattern) {
			throw new NullPointerException("Invalid pattern (not null expected)");
		}
		return withConstraint(index, value -> pattern.matcher(value).matches());
	}

	// CONTAINS
	/**
	 * <p>Add {@code String.contains()} estimations to all columns of the DSV data.</p>
	 * 
	 * @param substring The estimated contained substring by all column values
	 * @return The {@code DsvBuilder} instance
	 * @throws NullPointerException If the substring is {@code null}
	 */
	public DsvBuilder withContainsEstimations(final String substring) {
		if (null == substring) {
			throw new NullPointerException("Invalid substring (not null expected)");
		}
		return withEstimations(value -> value.contains(substring));
	}

	/**
	 * <p>Add a {@code String.contains()} estimation to the column at specified index of the DSV data.</p>
	 * 
	 * @param index Index of the column
	 * @param substring The estimated contained substring by specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws NullPointerException If the substring is {@code null}
	 */
	public DsvBuilder withContainsEstimation(final int index, final String substring) {
		if (null == substring) {
			throw new NullPointerException("Invalid substring (not null expected)");
		}
		return withEstimation(index, value -> value.contains(substring));
	}

	/**
	 * <p>Add a {@code String.contains()} constraint to the column at specified index of the DSV data. In others words
	 * that means each value of this column should always contain the substring.</p>
	 * 
	 * @param index Index of the column
	 * @param substring The required contained substring by specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws NullPointerException If the substring is {@code null}
	 */
	public DsvBuilder withContainsConstraint(final int index, final String substring) {
		if (null == substring) {
			throw new NullPointerException("Invalid substring (not null expected)");
		}
		return withConstraint(index, value -> value.contains(substring));
	}

	// CONTAINS NONE
	/**
	 * <p>Add a {@code String} contains-none constraint to the column at specified index of the DSV data. In others
	 * words that means each value of this column should never contain the substring.</p>
	 * 
	 * @param index Index of the column
	 * @param substring The required not contained substring by specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws NullPointerException If the substring is {@code null}
	 */
	public DsvBuilder withContainsNoneConstraint(final int index, final String substring) {
		if (null == substring) {
			throw new NullPointerException("Invalid substring (not null expected)");
		}
		return withConstraint(index, value -> !value.contains(substring));
	}

	// STARTS WITH
	/**
	 * <p>Add {@code String.startsWith()} estimations to all columns of the DSV data.</p>
	 * 
	 * @param prefix The estimated prefix of all column values
	 * @return The {@code DsvBuilder} instance
	 * @throws NullPointerException If the prefix is {@code null}
	 */
	public DsvBuilder withStartsWithEstimations(final String prefix) {
		if (null == prefix) {
			throw new NullPointerException("Invalid prefix (not null expected)");
		}
		return withEstimations(value -> value.startsWith(prefix));
	}

	/**
	 * <p>Add a {@code String.startsWith()} estimation to the column at specified index of the DSV data.</p>
	 * 
	 * @param index Index of the column
	 * @param prefix The estimated prefix of specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws NullPointerException If the prefix is {@code null}
	 */
	public DsvBuilder withStartsWithEstimation(final int index, final String prefix) {
		if (null == prefix) {
			throw new NullPointerException("Invalid prefix (not null expected)");
		}
		return withEstimation(index, value -> value.startsWith(prefix));
	}

	/**
	 * <p>Add a {@code String.startsWith()} constraint to the column at specified index of the DSV data. In others
	 * words that means each value of this column should always start with the prefix.</p>
	 * 
	 * @param index Index of the column
	 * @param prefix The required prefix of specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws NullPointerException If the prefix is {@code null}
	 */
	public DsvBuilder withStartsWithConstraint(final int index, final String prefix) {
		if (null == prefix) {
			throw new NullPointerException("Invalid prefix (not null expected)");
		}
		return withConstraint(index, value -> value.startsWith(prefix));
	}

	// ENDS WITH
	/**
	 * <p>Add {@code String.endsWith()} estimations to all columns of the DSV data.</p>
	 * 
	 * @param suffix The estimated suffix of all column values
	 * @return The {@code DsvBuilder} instance
	 * @throws NullPointerException If the suffix is {@code null}
	 */
	public DsvBuilder withEndsWithEstimations(final String suffix) {
		if (null == suffix) {
			throw new NullPointerException("Invalid suffix (not null expected)");
		}
		return withEstimations(value -> value.endsWith(suffix));
	}

	/**
	 * <p>Add a {@code String.endsWith()} estimation to the column at specified index of the DSV data.</p>
	 * 
	 * @param index Index of the column
	 * @param suffix The estimated suffix of specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws NullPointerException If the suffix is {@code null}
	 */
	public DsvBuilder withEndsWithEstimation(final int index, final String suffix) {
		if (null == suffix) {
			throw new NullPointerException("Invalid suffix (not null expected)");
		}
		return withEstimation(index, value -> value.endsWith(suffix));
	}

	/**
	 * <p>Add a {@code String.endsWith()} constraint to the column at specified index of the DSV data. In others words
	 * that means each value of this column should always end with the suffix.</p>
	 * 
	 * @param index Index of the column
	 * @param suffix The required suffix of specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws NullPointerException If the suffix is {@code null}
	 */
	public DsvBuilder withEndsWithConstraint(final int index, final String suffix) {
		if (null == suffix) {
			throw new NullPointerException("Invalid suffix (not null expected)");
		}
		return withConstraint(index, value -> value.endsWith(suffix));
	}

	// CUSTOM
	/**
	 * <p>Add custom estimation function to all columns of the DSV data.</p>
	 * 
	 * @param function The custom estimation function for all column values
	 * @return The {@code DsvBuilder} instance
	 */
	public DsvBuilder withEstimations(final Function<String, ?> function) {
		IntStream.range(0, nbColumns).forEach(i -> evaluator.addEstimation(i, Estimation.of(function)));
		return this;
	}

	/**
	 * <p>Add a custom estimation function to the column at specified index of the DSV data.</p>
	 * 
	 * @param index Index of the column
	 * @param function The custom estimation function for specified column values
	 * @return The {@code DsvBuilder} instance
	 */
	public DsvBuilder withEstimation(final int index, final Function<String, ?> function) {
		return withEstimation(index, Estimation.of(function));
	}

	/**
	 * <p>Add a custom estimation to the column at specified index of the DSV data.</p>
	 * 
	 * @param index Index of the column
	 * @param estimation The custom estimation for specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws IndexOutOfBoundsException If the index is not valid
	 */
	public DsvBuilder withEstimation(final int index, final Estimation<String, ?> estimation) {
		if (0 > index || nbColumns <= index) {
			throw new IndexOutOfBoundsException("Invalid index: " + index + " (between 0 and " + (nbColumns - 1) + " expected)");
		}
		evaluator.addEstimation(index, estimation);
		return this;
	}

	/**
	 * <p>Add a custom constraint predicate to all columns of the DSV data.</p>
	 * 
	 * @param predicate The custom constraint predicate for all column values
	 * @return The {@code DsvBuilder} instance
	 */
	public DsvBuilder withConstraints(final Predicate<String> predicate) {
		IntStream.range(0, nbColumns).forEach(i -> evaluator.addConstraint(i, Constraint.of(predicate)));
		return this;
	}

	/**
	 * <p>Add a custom constraint predicate to the column at specified index of the DSV data.</p>
	 * 
	 * @param index Index of the column
	 * @param predicate The custom constraint predicate for specified column values
	 * @return The {@code DsvBuilder} instance
	 */
	public DsvBuilder withConstraint(final int index, final Predicate<String> predicate) {
		return withConstraint(index, Constraint.of(predicate));
	}

	/**
	 * <p>Add a custom constraint to the column at specified index of the DSV data.</p>
	 * 
	 * @param index Index of the column
	 * @param constraint The custom constraint for specified column values
	 * @return The {@code DsvBuilder} instance
	 * @throws IndexOutOfBoundsException If the index is not valid
	 */
	public DsvBuilder withConstraint(final int index, final Constraint<String> constraint) {
		if (0 > index || nbColumns <= index) {
			throw new IndexOutOfBoundsException("Invalid index: " + index + " (between 0 and " + (nbColumns - 1) + " expected)");
		}
		evaluator.addConstraint(index, constraint);
		return this;
	}

	/**
	 * <p>Build and return a {@code DsvMender} instance using configured attributes.</p>
	 * 
	 * @return The {@code DsvMender} instance
	 */
	public DsvMender build() {
		return new DsvMender(evaluator, delimiter, nbColumns, maxDepth);
	}
}