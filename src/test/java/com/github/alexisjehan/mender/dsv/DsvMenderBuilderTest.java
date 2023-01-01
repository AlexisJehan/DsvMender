/*
 * MIT License
 *
 * Copyright (c) 2017-2023 Alexis Jehan
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
import com.github.alexisjehan.javanilla.lang.array.IntArrays;
import com.github.alexisjehan.javanilla.lang.array.ObjectArrays;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

final class DsvMenderBuilderTest {

	private static final String DELIMITER = ",";
	private static final int LENGTH = 3;
	private static final int MAX_DEPTH = 5;
	private static final Predicate<String> CONSTRAINT_VALIDATOR = "foo"::equals;
	private static final int[] CONSTRAINT_INDEXES = IntArrays.singleton(0);
	private static final Function<String, ?> ESTIMATION_TRANSFORMER = Function.identity();
	private static final int[] ESTIMATION_INDEXES = IntArrays.singleton(0);

	@Test
	void testDefaultMaxDepth() {
		final var dsvMender = DsvMender.builder()
				.withDelimiter(DELIMITER)
				.withLength(LENGTH)
				.build();
		assertThat(dsvMender.getMaxDepth()).isEqualTo(DsvMender.Builder.DEFAULT_MAX_DEPTH);
	}

	@Test
	void testWithDelimiterChar() {
		final var delimiterStep = DsvMender.builder();
		final var lengthStep = delimiterStep.withDelimiter(Strings.toChar(DELIMITER));
		assertThat(lengthStep).isSameAs(delimiterStep);
		final var buildStep = lengthStep.withLength(LENGTH);
		final var dsvMender = buildStep.build();
		assertThat(dsvMender.getDelimiter()).isEqualTo(DELIMITER);
	}

	@Test
	void testWithDelimiterString() {
		final var delimiterStep = DsvMender.builder();
		final var lengthStep = delimiterStep.withDelimiter(DELIMITER);
		assertThat(lengthStep).isSameAs(delimiterStep);
		final var buildStep = lengthStep.withLength(LENGTH);
		final var dsvMender = buildStep.build();
		assertThat(dsvMender.getDelimiter()).isEqualTo(DELIMITER);
	}

	@Test
	void testWithLength() {
		final var lengthStep = DsvMender.builder()
				.withDelimiter(DELIMITER);
		final var buildStep = lengthStep.withLength(LENGTH);
		assertThat(buildStep).isSameAs(lengthStep);
		final var dsvMender = buildStep.build();
		assertThat(dsvMender.getLength()).isEqualTo(LENGTH);
	}

	@Test
	void testWithMaxDepth() {
		final var optionalMaxDepthStep = DsvMender.builder()
				.withDelimiter(DELIMITER)
				.withLength(LENGTH);
		final var buildStep = optionalMaxDepthStep.withMaxDepth(MAX_DEPTH);
		assertThat(buildStep).isSameAs(optionalMaxDepthStep);
		final var dsvMender = buildStep.build();
		assertThat(dsvMender.getMaxDepth()).isEqualTo(MAX_DEPTH);
	}

	@Test
	void testWithConstraint() {
		final var optionalEvaluatorStep = DsvMender.builder()
				.withDelimiter(DELIMITER)
				.withLength(LENGTH);
		final var buildStep = optionalEvaluatorStep.withConstraint(CONSTRAINT_VALIDATOR);
		assertThat(buildStep).isSameAs(optionalEvaluatorStep);
		final var dsvMender = buildStep.build();
		final var constraintEvaluators = dsvMender.getConstraintEvaluators();
		assertThat(constraintEvaluators).hasSize(dsvMender.getLength());
		for (final var constraintEvaluator : constraintEvaluators) {
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(1.0d);
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("bar", "bar", "bar"))).isNaN();
		}
	}

	@Test
	void testWithConstraintIndexes() {
		final var optionalEvaluatorStep = DsvMender.builder()
				.withDelimiter(DELIMITER)
				.withLength(LENGTH);
		final var buildStep = optionalEvaluatorStep.withConstraint(CONSTRAINT_VALIDATOR, CONSTRAINT_INDEXES);
		assertThat(buildStep).isSameAs(optionalEvaluatorStep);
		final var dsvMender = buildStep.build();
		final var constraintEvaluators = dsvMender.getConstraintEvaluators();
		assertThat(constraintEvaluators).hasSize(1);
		for (final var constraintEvaluator : constraintEvaluators) {
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(1.0d);
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(1.0d);
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("bar", "foo", "foo"))).isNaN();
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("bar", "foo", "foo"))).isNaN();
		}
	}

	@Test
	void testWithConstraintInvalid() {
		final var optionalEvaluatorStep = DsvMender.builder()
				.withDelimiter(DELIMITER)
				.withLength(LENGTH);
		assertThatNullPointerException().isThrownBy(() -> optionalEvaluatorStep.withConstraint(null));
		assertThatNullPointerException().isThrownBy(() -> optionalEvaluatorStep.withConstraint(CONSTRAINT_VALIDATOR, (int[]) null));
		assertThatIllegalArgumentException().isThrownBy(() -> optionalEvaluatorStep.withConstraint(CONSTRAINT_VALIDATOR, IntArrays.EMPTY));
		assertThatIllegalArgumentException().isThrownBy(() -> optionalEvaluatorStep.withConstraint(CONSTRAINT_VALIDATOR, -1));
		assertThatIllegalArgumentException().isThrownBy(() -> optionalEvaluatorStep.withConstraint(CONSTRAINT_VALIDATOR, LENGTH));
	}

	@Test
	void testWithEstimation() {
		final var optionalEvaluatorStep = DsvMender.builder()
				.withDelimiter(DELIMITER)
				.withLength(LENGTH);
		final var buildStep = optionalEvaluatorStep.withEstimation(ESTIMATION_TRANSFORMER);
		assertThat(buildStep).isSameAs(optionalEvaluatorStep);
		final var dsvMender = buildStep.build();
		final var estimationEvaluators = dsvMender.getEstimationEvaluators();
		assertThat(estimationEvaluators).hasSize(dsvMender.getLength());
		for (final var estimationEvaluator : estimationEvaluators) {
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isNaN();
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isNaN();
			estimationEvaluator.fit(ObjectArrays.of("foo", "foo", "foo"));
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(1.0d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "bar", "bar"))).isZero();
			estimationEvaluator.fit(ObjectArrays.of("bar", "bar", "bar"));
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(0.5d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "bar", "bar"))).isEqualTo(0.5d);
		}
	}

	@Test
	void testWithEstimationIndexes() {
		final var optionalEvaluatorStep = DsvMender.builder()
				.withDelimiter(DELIMITER)
				.withLength(LENGTH);
		final var buildStep = optionalEvaluatorStep.withEstimation(ESTIMATION_TRANSFORMER, ESTIMATION_INDEXES);
		assertThat(buildStep).isSameAs(optionalEvaluatorStep);
		final var dsvMender = buildStep.build();
		final var estimationEvaluators = dsvMender.getEstimationEvaluators();
		assertThat(estimationEvaluators).hasSize(1);
		for (final var estimationEvaluator : estimationEvaluators) {
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isNaN();
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isNaN();
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "foo", "foo"))).isNaN();
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "foo", "foo"))).isNaN();
			estimationEvaluator.fit(ObjectArrays.of("foo", "foo", "foo"));
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(1.0d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(1.0d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "foo", "foo"))).isZero();
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "foo", "foo"))).isZero();
			estimationEvaluator.fit(ObjectArrays.of("bar", "foo", "foo"));
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(0.5d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(0.5d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "foo", "foo"))).isEqualTo(0.5d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "foo", "foo"))).isEqualTo(0.5d);
		}
	}

	@Test
	void testWithEstimationInvalid() {
		final var optionalEvaluatorStep = DsvMender.builder()
				.withDelimiter(DELIMITER)
				.withLength(LENGTH);
		assertThatNullPointerException().isThrownBy(() -> optionalEvaluatorStep.withEstimation(null));
		assertThatNullPointerException().isThrownBy(() -> optionalEvaluatorStep.withEstimation(ESTIMATION_TRANSFORMER, (int[]) null));
		assertThatIllegalArgumentException().isThrownBy(() -> optionalEvaluatorStep.withEstimation(ESTIMATION_TRANSFORMER, IntArrays.EMPTY));
		assertThatIllegalArgumentException().isThrownBy(() -> optionalEvaluatorStep.withEstimation(ESTIMATION_TRANSFORMER, -1));
		assertThatIllegalArgumentException().isThrownBy(() -> optionalEvaluatorStep.withEstimation(ESTIMATION_TRANSFORMER, LENGTH));
	}

	@Test
	void testBasic() {
		assertThat(DsvMender.basic(Strings.toChar(DELIMITER), LENGTH)).satisfies(dsvMender -> {
			assertThat(dsvMender.getDelimiter()).isEqualTo(DELIMITER);
			assertThat(dsvMender.getLength()).isEqualTo(LENGTH);
			assertThat(dsvMender.getConstraintEvaluators()).isEmpty();
			final var estimationEvaluators = dsvMender.getEstimationEvaluators();
			assertThat(estimationEvaluators).hasSize(2 * dsvMender.getLength());
			for (final var estimationEvaluator : estimationEvaluators) {
				assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isNaN();
				assertThat(estimationEvaluator.evaluate(ObjectArrays.of(Strings.EMPTY, Strings.EMPTY, Strings.EMPTY))).isNaN();
				estimationEvaluator.fit(ObjectArrays.of("foo", "foo", "foo"));
				assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(1.0d);
				assertThat(estimationEvaluator.evaluate(ObjectArrays.of(Strings.EMPTY, Strings.EMPTY, Strings.EMPTY))).isZero();
			}
		});
		assertThat(DsvMender.basic(DELIMITER, LENGTH)).satisfies(dsvMender -> {
			assertThat(dsvMender.getDelimiter()).isEqualTo(DELIMITER);
			assertThat(dsvMender.getLength()).isEqualTo(LENGTH);
			assertThat(dsvMender.getConstraintEvaluators()).isEmpty();
			final var estimationEvaluators = dsvMender.getEstimationEvaluators();
			assertThat(estimationEvaluators).hasSize(2 * dsvMender.getLength());
			for (final var estimationEvaluator : estimationEvaluators) {
				assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isNaN();
				assertThat(estimationEvaluator.evaluate(ObjectArrays.of(Strings.EMPTY, Strings.EMPTY, Strings.EMPTY))).isNaN();
				estimationEvaluator.fit(ObjectArrays.of("foo", "foo", "foo"));
				assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(1.0d);
				assertThat(estimationEvaluator.evaluate(ObjectArrays.of(Strings.EMPTY, Strings.EMPTY, Strings.EMPTY))).isZero();
			}
		});
	}
}