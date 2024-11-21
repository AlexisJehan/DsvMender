/*
 * MIT License
 *
 * Copyright (c) 2017-2024 Alexis Jehan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.alexisjehan.mender.dsv;

import com.github.alexisjehan.javanilla.lang.Strings;
import com.github.alexisjehan.javanilla.lang.array.ObjectArrays;
import com.github.alexisjehan.javanilla.misc.quality.Ensure;
import com.github.alexisjehan.javanilla.misc.quality.ToString;
import com.github.alexisjehan.mender.api.MendException;
import com.github.alexisjehan.mender.api.Mender;
import com.github.alexisjehan.mender.api.evaluators.ConstraintEvaluator;
import com.github.alexisjehan.mender.api.evaluators.EstimationEvaluator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * {@link Mender} able to mend invalid DSV (Delimiter-Separated Values) rows based on valid ones.
 * @since 1.0.0
 */
@SuppressWarnings("overrides")
public final class DsvMender implements Mender<String[], DsvMendResult> {

	/**
	 * {@link Builder}'s step to set the delimiter.
	 *
	 * <p><b>Note</b>: This interface is a {@link FunctionalInterface} whose abstract method is
	 * {@link #withDelimiter(String)}.</p>
	 * @since 1.0.0
	 */
	@FunctionalInterface
	public interface DelimiterStep {

		/**
		 * Set the delimiter with the given {@code char}.
		 * @param delimiter the {@code char} delimiter
		 * @return the current {@link Builder} at the next step
		 * @since 1.0.0
		 */
		default LengthStep withDelimiter(final char delimiter) {
			return withDelimiter(Character.toString(delimiter));
		}

		/**
		 * Set the delimiter with the given {@link String}.
		 * @param delimiter the {@link String} delimiter
		 * @return the current {@link Builder} at the next step
		 * @since 1.0.0
		 */
		LengthStep withDelimiter(String delimiter);
	}

	/**
	 * {@link Builder}'s step to set the length.
	 *
	 * <p><b>Note</b>: This interface is a {@link FunctionalInterface} whose abstract method is
	 * {@link #withLength(int)}.</p>
	 * @since 1.0.0
	 */
	@FunctionalInterface
	public interface LengthStep {

		/**
		 * Set the length.
		 * @param length the length
		 * @return the current {@link Builder} at the next step
		 * @since 1.0.0
		 */
		OptionalMaxDepthStep withLength(int length);
	}

	/**
	 * {@link Builder}'s optional step to set the maximum depth.
	 * @since 1.0.0
	 */
	public interface OptionalMaxDepthStep extends OptionalEvaluatorStep {

		/**
		 * Set the maximum depth.
		 * @param maxDepth the maximum depth
		 * @return the current {@link Builder} at the next step
		 * @since 1.0.0
		 */
		OptionalEvaluatorStep withMaxDepth(int maxDepth);
	}

	/**
	 * {@link Builder}'s optional step to add {@link ConstraintEvaluator}s and {@link EstimationEvaluator}s.
	 * @since 1.0.0
	 */
	public interface OptionalEvaluatorStep extends BuildStep {

		/**
		 * Add a {@link ConstraintEvaluator} with the given validator {@link Predicate} on every value.
		 * @param validator the validator {@link Predicate}
		 * @return the current {@link Builder} at the next step
		 * @throws NullPointerException if the validator {@link Predicate} is {code null}
		 * @since 1.0.0
		 */
		OptionalEvaluatorStep withConstraint(Predicate<String> validator);

		/**
		 * Add a {@link ConstraintEvaluator} with the given validator {@link Predicate} on values at provided indexes.
		 * @param validator the validator {@link Predicate}
		 * @param indexes values' indexes
		 * @return the current {@link Builder} at the next step
		 * @throws NullPointerException if the validator {@link Predicate} or the array of indexes is {code null}
		 * @throws IllegalArgumentException if the array of indexes is empty or if any of them is not valid
		 * @since 1.0.0
		 */
		OptionalEvaluatorStep withConstraint(Predicate<String> validator, int... indexes);

		/**
		 * Add an {@link EstimationEvaluator} with the given transformer {@link Function} on every value.
		 * @param transformer the transformer {@link Function}
		 * @return the current {@link Builder} at the next step
		 * @throws NullPointerException if the transformer {@link Function} is {code null}
		 * @since 1.0.0
		 */
		OptionalEvaluatorStep withEstimation(Function<String, ?> transformer);

		/**
		 * Add an {@link EstimationEvaluator} with the given transformer {@link Function} on values at provided indexes.
		 * @param transformer the transformer {@link Function}
		 * @param indexes values' indexes
		 * @return the current {@link Builder} at the next step
		 * @throws NullPointerException if the transformer {@link Function} or the array of indexes is {code null}
		 * @throws IllegalArgumentException if the array of indexes is empty or if any of them is not valid
		 * @since 1.0.0
		 */
		OptionalEvaluatorStep withEstimation(Function<String, ?> transformer, int... indexes);
	}

	/**
	 * {@link Builder}'s build step.
	 *
	 * <p><b>Note</b>: This interface is a {@link FunctionalInterface} whose abstract method is {@link #build()}.</p>
	 * @since 1.0.0
	 */
	@FunctionalInterface
	public interface BuildStep {

		/**
		 * Build the {@link DsvMender}.
		 * @return the built {@link DsvMender}
		 * @since 1.0.0
		 */
		DsvMender build();
	}

	/**
	 * Step builder to build {@link DsvMender} instances.
	 * @since 1.0.0
	 */
	static final class Builder implements DelimiterStep, LengthStep, OptionalMaxDepthStep {

		/**
		 * Default maximum depth.
		 * @since 1.0.0
		 */
		static final int DEFAULT_MAX_DEPTH = 20;

		/**
		 * {@link DsvMender}'s delimiter.
		 * @since 1.0.0
		 */
		private String delimiter;

		/**
		 * {@link DsvMender}'s length.
		 * @since 1.0.0
		 */
		private int length;

		/**
		 * {@link DsvMender}'s maximum depth.
		 * @since 1.0.0
		 */
		private int maxDepth = DEFAULT_MAX_DEPTH;

		/**
		 * {@link DsvMender}'s {@link Set} of {@link ConstraintEvaluator}s.
		 * @since 1.0.0
		 */
		private final Set<ConstraintEvaluator<String[]>> constraintEvaluators = new HashSet<>();

		/**
		 * {@link DsvMender}'s {@link Set} of {@link EstimationEvaluator}s.
		 * @since 1.0.0
		 */
		private final Set<EstimationEvaluator<String[]>> estimationEvaluators = new HashSet<>();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public LengthStep withDelimiter(final String delimiter) {
			this.delimiter = delimiter;
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public OptionalMaxDepthStep withLength(final int length) {
			this.length = length;
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public OptionalEvaluatorStep withMaxDepth(final int maxDepth) {
			this.maxDepth = maxDepth;
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public OptionalEvaluatorStep withConstraint(final Predicate<String> validator) {
			return withConstraint(validator, IntStream.range(0, length).toArray());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public OptionalEvaluatorStep withConstraint(final Predicate<String> validator, final int... indexes) {
			Ensure.notNull("validator", validator);
			Ensure.notNullAndNotEmpty("indexes", indexes);
			for (final var index : indexes) {
				Ensure.between("indexes index", index, 0, length - 1);
				constraintEvaluators.add(new ConstraintEvaluator<>(values -> validator.test(values[index])));
			}
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public OptionalEvaluatorStep withEstimation(final Function<String, ?> transformer) {
			return withEstimation(transformer, IntStream.range(0, length).toArray());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public OptionalEvaluatorStep withEstimation(final Function<String, ?> transformer, final int... indexes) {
			Ensure.notNull("transformer", transformer);
			Ensure.notNullAndNotEmpty("indexes", indexes);
			for (final var index : indexes) {
				Ensure.between("indexes index", index, 0, length - 1);
				estimationEvaluators.add(new EstimationEvaluator<>(values -> transformer.apply(values[index])));
			}
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DsvMender build() {
			return new DsvMender(delimiter, length, maxDepth, constraintEvaluators, estimationEvaluators);
		}
	}

	/**
	 * Delimiter.
	 * @since 1.0.0
	 */
	private final String delimiter;

	/**
	 * Length.
	 * @since 1.0.0
	 */
	private final int length;

	/**
	 * Maximum depth.
	 * @since 1.0.0
	 */
	private final int maxDepth;

	/**
	 * {@link Set} of {@link ConstraintEvaluator}s.
	 * @since 1.0.0
	 */
	private final Set<ConstraintEvaluator<String[]>> constraintEvaluators;

	/**
	 * {@link Set} of {@link EstimationEvaluator}s.
	 * @since 1.0.0
	 */
	private final Set<EstimationEvaluator<String[]>> estimationEvaluators;

	/**
	 * Last {@link #mend(String...)} result or {@code null}.
	 * @since 1.0.0
	 */
	private DsvMendResult lastResult;

	/**
	 * Constructor with a delimiter, a length, a maximum depth, a {@link Set} of {@link ConstraintEvaluator}s and a
	 * {@link Set} of {@link EstimationEvaluator}s.
	 * @param delimiter the delimiter
	 * @param length the length
	 * @param maxDepth the maximum depth
	 * @param constraintEvaluators the {@link Set} of {@link ConstraintEvaluator}s
	 * @param estimationEvaluators the {@link Set} of {@link EstimationEvaluator}s
	 * @throws NullPointerException if the delimiter, the {@link Set} of {@link ConstraintEvaluator}s or any of them or
	 *         the {@link Set} of {@link EstimationEvaluator}s or any of them if {@code null}
	 * @throws IllegalArgumentException if delimiter is empty, the length is lower than 2, the maximum depth if lower
	 *         than 1 or if the {@link Set} of {@link ConstraintEvaluator}s or the {@link Set} of
	 *         {@link EstimationEvaluator}s is empty
	 * @since 1.0.0
	 */
	public DsvMender(
			final String delimiter,
			final int length,
			final int maxDepth,
			final Set<ConstraintEvaluator<String[]>> constraintEvaluators,
			final Set<EstimationEvaluator<String[]>> estimationEvaluators
	) {
		Ensure.notNullAndNotEmpty("delimiter", delimiter);
		Ensure.greaterThanOrEqualTo("length", length, 2);
		Ensure.greaterThanOrEqualTo("maxDepth", maxDepth, 1);
		Ensure.notNullAndNotNullElements("constraintEvaluators", constraintEvaluators);
		Ensure.notNullAndNotNullElements("estimationEvaluators", estimationEvaluators);
		this.delimiter = delimiter;
		this.length = length;
		this.maxDepth = maxDepth;
		this.constraintEvaluators = Set.copyOf(constraintEvaluators);
		this.estimationEvaluators = Set.copyOf(estimationEvaluators);
	}

	/**
	 * Test if values are valid based on the length and {@link ConstraintEvaluator}s.
	 * @param values values to test
	 * @return {@code true} if values are valid
	 * @since 1.0.0
	 */
	private boolean isValid(final String[] values) {
		if (length != values.length) {
			return false;
		}
		for (final var constraintEvaluator : constraintEvaluators) {
			if (!constraintEvaluator.isValid(values)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Could optimize the given DSV row by merging consecutive empty values to improve the {@link #mend(String...)}
	 * operation.
	 * @param threshold the threshold of consecutive empty values to keep
	 * @param row the row to optimize
	 * @return optimized values
	 * @throws NullPointerException if the row is {@code null}
	 * @throws IllegalArgumentException if the threshold is lower than {@code 0}
	 * @since 1.0.0
	 */
	public String[] optimize(final int threshold, final String row) {
		Ensure.notNull("row", row);
		return optimize(threshold, Strings.split(delimiter, row).toArray(String[]::new));
	}

	/**
	 * Could optimize given values by merging consecutive empty values to improve the {@link #mend(String...)}
	 * operation.
	 * @param threshold the threshold of consecutive empty values to keep
	 * @param values values to optimize
	 * @return optimized values
	 * @throws NullPointerException if values or any of them is {@code null}
	 * @throws IllegalArgumentException if the threshold is lower than {@code 0}
	 * @since 1.0.0
	 */
	public String[] optimize(final int threshold, final String... values) {
		Ensure.greaterThanOrEqualTo("threshold", threshold, 0);
		Ensure.notNullAndNotNullElements("values", values);
		var optimizedValues = values.clone();
		while (length < optimizedValues.length) {
			var from = 0;
			var to = 0;
			var index = -1;
			for (var i = 0; i < optimizedValues.length; ++i) {
				if (-1 == index && optimizedValues[i].isEmpty()) {
					index = i;
					continue;
				}
				if (-1 != index && !optimizedValues[i].isEmpty()) {
					if (to - from < i - index) {
						from = index;
						to = i;
					}
					index = -1;
				}
			}
			if (-1 != index && to - from < optimizedValues.length - index) {
				from = index;
				to = optimizedValues.length;
			}
			if (to - from > 2 * threshold + 1) {
				for (var i = from + threshold + 1; i < to - threshold; ++i) {
					optimizedValues = ObjectArrays.remove(optimizedValues, from + threshold + 1);
					optimizedValues[from + threshold] += delimiter;
				}
			} else {
				break;
			}
		}
		return optimizedValues;
	}

	/**
	 * Mend the given DSV row if needed.
	 * @param row the row to mend
	 * @return best fixed values
	 * @throws MendException might occur if mending the value is not possible
	 * @throws NullPointerException if the row is {@code null}
	 * @since 1.0.0
	 */
	public String[] mend(final String row) {
		Ensure.notNull("row", row);
		return mend(Strings.split(delimiter, row).toArray(String[]::new));
	}

	/**
	 * Mend given values if needed.
	 * @param values values to mend
	 * @return best fixed values
	 * @throws MendException might occur if mending the value is not possible
	 * @throws NullPointerException if values or any of them is {@code null}
	 * @since 1.0.0
	 */
	@Override
	public String[] mend(final String... values) {
		Ensure.notNullAndNotNullElements("values", values);
		lastResult = null;
		if (isValid(values)) {
			for (final var estimationEvaluator : estimationEvaluators) {
				estimationEvaluator.fit(values);
			}
			return values;
		}
		final var depth = StrictMath.abs(length - values.length - 2);
		Ensure.lowerThanOrEqualTo("values depth", depth, maxDepth);
		final var children = new ArrayList<String[]>();
		if (length < values.length) {
			children.addAll(generateJoinChildren(values));
			for (var i = length; i < values.length - 1; ++i) {
				final var tmpChildren = children.toArray(String[][]::new);
				children.clear();
				for (final var tmpChild : tmpChildren) {
					children.addAll(generateJoinChildren(tmpChild));
				}
			}
		} else if (length > values.length) {
			children.addAll(generateShiftChildren(values));
			for (var i = length; i > values.length + 1; --i) {
				final var tmpChildren = children.toArray(String[][]::new);
				children.clear();
				for (final var tmpChild : tmpChildren) {
					children.addAll(generateShiftChildren(tmpChild));
				}
			}
		} else {
			final var tmpChildren = generateJoinChildren(values);
			for (final var tmpChild : tmpChildren) {
				children.addAll(generateShiftChildren(tmpChild));
			}
		}
		final var candidates = new HashSet<DsvMendCandidate>();
		DsvMendCandidate bestCandidate = null;
		for (final var child : children) {
			final var optionalCandidateScore = DoubleStream.concat(
					constraintEvaluators.stream()
							.mapToDouble(
									constraintEvaluator -> constraintEvaluator.evaluate(child)
							),
					estimationEvaluators.stream()
							.mapToDouble(
									estimationEvaluator -> estimationEvaluator.evaluate(child)
							)
			).average();
			if (optionalCandidateScore.isPresent()) {
				final var candidateScore = optionalCandidateScore.getAsDouble();
				final var candidate = new DsvMendCandidate(child, candidateScore);
				candidates.add(candidate);
				if (!Double.isNaN(candidateScore)
						&& (null == bestCandidate || bestCandidate.getScore() < candidateScore)) {
					bestCandidate = candidate;
				}
			}
		}
		if (null == bestCandidate) {
			throw new MendException(
					"No solution for values: "
							+ ToString.toString(values)
							+ " (consider using others constraints and estimations)"
			);
		}
		lastResult = new DsvMendResult(values, candidates, bestCandidate);
		return bestCandidate.getValue();
	}

	/**
	 * Generate a {@link List} of every possibility of joining consecutive values with the delimiter.
	 * @param parent parent values
	 * @return a {@link List} of joining possibilities
	 * @since 1.0.0
	 */
	private List<String[]> generateJoinChildren(final String[] parent) {
		final var children = new ArrayList<String[]>(parent.length - 1);
		for (var i = 0; i < parent.length - 1; ++i) {
			final var child = new String[parent.length - 1];
			for (var j = 0; j < parent.length - 1; ++j) {
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
	 * Generate a {@link List} of every possibility of shifting consecutive values with an empty one.
	 * @param parent parent values
	 * @return a {@link List} of shifting possibilities
	 * @since 1.0.0
	 */
	private List<String[]> generateShiftChildren(final String[] parent) {
		final var children = new ArrayList<String[]>(parent.length + 1);
		for (var i = 0; i < parent.length + 1; ++i) {
			final var child = new String[parent.length + 1];
			for (var j = 0; j < parent.length + 1; ++j) {
				if (j == i) {
					child[j] = Strings.EMPTY;
				} else {
					child[j] = parent[j < i ? j : j - 1];
				}
			}
			children.add(child);
		}
		return children;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<DsvMendResult> getLastResult() {
		return Optional.ofNullable(lastResult);
	}

	/**
	 * Get the delimiter.
	 * @return the delimiter
	 * @since 1.0.0
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * Get the length.
	 * @return the length
	 * @since 1.0.0
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Get the maximum depth.
	 * @return the maximum depth
	 * @since 1.0.0
	 */
	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * Get the {@link Set} of {@link ConstraintEvaluator}s.
	 * @return the {@link Set} of {@link ConstraintEvaluator}s
	 * @since 1.0.0
	 */
	public Set<ConstraintEvaluator<String[]>> getConstraintEvaluators() {
		return constraintEvaluators;
	}

	/**
	 * Get the {@link Set} of {@link EstimationEvaluator}s.
	 * @return the {@link Set} of {@link EstimationEvaluator}s
	 * @since 1.0.0
	 */
	public Set<EstimationEvaluator<String[]>> getEstimationEvaluators() {
		return estimationEvaluators;
	}

	/**
	 * Create a new {@link Builder} instance.
	 * @return the created {@link Builder}
	 * @since 1.0.0
	 */
	public static DelimiterStep builder() {
		return new Builder();
	}

	/**
	 * Create a new basic {@code DsvMender} instance without any {@link ConstraintEvaluator} and with some
	 * {@link EstimationEvaluator}s.
	 * @param delimiter the {@code char} delimiter
	 * @param length the length
	 * @return the created basic {@code DsvMender}
	 * @since 1.0.0
	 */
	public static DsvMender basic(final char delimiter, final int length) {
		return basic(Character.toString(delimiter), length);
	}

	/**
	 * Create a new basic {@code DsvMender} instance without any {@link ConstraintEvaluator} and with some
	 * {@link EstimationEvaluator}s.
	 * @param delimiter the {@link String} delimiter
	 * @param length the length
	 * @return the created basic {@code DsvMender}
	 * @since 1.0.0
	 */
	public static DsvMender basic(final String delimiter, final int length) {
		return builder()
				.withDelimiter(delimiter)
				.withLength(length)
				.withEstimation(String::isEmpty)
				.withEstimation(String::length)
				.build();
	}
}