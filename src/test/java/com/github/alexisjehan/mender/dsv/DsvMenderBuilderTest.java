/*
 * MIT License
 *
 * Copyright (c) 2017-2019 Alexis Jehan
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * <p>{@link DsvMender.Builder} unit tests.</p>
 */
final class DsvMenderBuilderTest {

	@Test
	void testDefaultMaxDepth() {
		final var dsvMender = DsvMender.builder()
				.withDelimiter(",")
				.withLength(2)
				.build();
		assertThat(dsvMender.getMaxDepth()).isEqualTo(20);
	}

	@Test
	void testWithDelimiter() {
		{
			final var delimiterStep = DsvMender.builder();
			final var lengthStep = delimiterStep.withDelimiter(',');
			assertThat(lengthStep).isSameAs(delimiterStep);
			final var buildStep = lengthStep.withLength(2);
			final var dsvMender = buildStep.build();
			assertThat(dsvMender.getDelimiter()).isEqualTo(",");
		}
		final var delimiterStep = DsvMender.builder();
		final var lengthStep = delimiterStep.withDelimiter(",");
		assertThat(lengthStep).isSameAs(delimiterStep);
		final var buildStep = lengthStep.withLength(2);
		final var dsvMender = buildStep.build();
		assertThat(dsvMender.getDelimiter()).isEqualTo(",");
	}

	@Test
	void testWithLength() {
		final var lengthStep = DsvMender.builder()
				.withDelimiter(",");
		final var buildStep = lengthStep.withLength(2);
		assertThat(buildStep).isSameAs(lengthStep);
		final var dsvMender = buildStep.build();
		assertThat(dsvMender.getLength()).isEqualTo(2);
	}

	@Test
	void testWithMaxDepth() {
		final var optionalMaxDepthStep = DsvMender.builder()
				.withDelimiter(",")
				.withLength(2);
		final var buildStep = optionalMaxDepthStep.withMaxDepth(1);
		assertThat(buildStep).isSameAs(optionalMaxDepthStep);
		final var dsvMender = buildStep.build();
		assertThat(dsvMender.getMaxDepth()).isEqualTo(1);
	}

	@Test
	void testWithConstraint() {
		final var optionalEvaluatorStep = DsvMender.builder()
				.withDelimiter(",")
				.withLength(2);
		final var buildStep = optionalEvaluatorStep.withConstraint("foo"::equals);
		assertThat(buildStep).isSameAs(optionalEvaluatorStep);
		final var dsvMender = buildStep.build();
		final var constraintEvaluators = dsvMender.getConstraintEvaluators();
		assertThat(constraintEvaluators).hasSize(dsvMender.getLength());
		for (final var constraintEvaluator : constraintEvaluators) {
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("foo", "foo"))).isEqualTo(1.0d);
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("bar", "bar"))).isNaN();
		}
	}

	@Test
	void testWithConstraintIndexes() {
		final var optionalEvaluatorStep = DsvMender.builder()
				.withDelimiter(",")
				.withLength(2);
		final var buildStep = optionalEvaluatorStep.withConstraint("foo"::equals, 0);
		assertThat(buildStep).isSameAs(optionalEvaluatorStep);
		final var dsvMender = buildStep.build();
		final var constraintEvaluators = dsvMender.getConstraintEvaluators();
		assertThat(constraintEvaluators).hasSize(1);
		for (final var constraintEvaluator : constraintEvaluators) {
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("foo", "foo"))).isEqualTo(1.0d);
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("foo", "bar"))).isEqualTo(1.0d);
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("bar", "foo"))).isNaN();
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("bar", "bar"))).isNaN();
		}
	}

	@Test
	void testWithConstraintInvalid() {
		final var delimiterStep = DsvMender.builder();
		final var lengthStep = delimiterStep.withDelimiter(",");
		final var optionalEvaluatorStep = lengthStep.withLength(2);
		assertThatNullPointerException().isThrownBy(() -> optionalEvaluatorStep.withConstraint(null));
		assertThatNullPointerException().isThrownBy(() -> optionalEvaluatorStep.withConstraint("foo"::equals, (int[]) null));
		assertThatIllegalArgumentException().isThrownBy(() -> optionalEvaluatorStep.withConstraint("foo"::equals, IntArrays.EMPTY));
		assertThatIllegalArgumentException().isThrownBy(() -> optionalEvaluatorStep.withConstraint("foo"::equals, -1));
		assertThatIllegalArgumentException().isThrownBy(() -> optionalEvaluatorStep.withConstraint("foo"::equals, 2));
	}

	@Test
	void testWithEstimation() {
		final var optionalEvaluatorStep = DsvMender.builder()
				.withDelimiter(",")
				.withLength(2);
		final var buildStep = optionalEvaluatorStep.withEstimation(Function.identity());
		assertThat(buildStep).isSameAs(optionalEvaluatorStep);
		final var dsvMender = buildStep.build();
		final var estimationEvaluators = dsvMender.getEstimationEvaluators();
		assertThat(estimationEvaluators).hasSize(dsvMender.getLength());
		for (final var estimationEvaluator : estimationEvaluators) {
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo"))).isNaN();
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo"))).isNaN();
			estimationEvaluator.fit(ObjectArrays.of("foo", "foo"));
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo"))).isEqualTo(1.0d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "bar"))).isEqualTo(0.0d);
			estimationEvaluator.fit(ObjectArrays.of("bar", "bar"));
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo"))).isEqualTo(0.5d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "bar"))).isEqualTo(0.5d);
		}
	}

	@Test
	void testWithEstimationIndexes() {
		final var optionalEvaluatorStep = DsvMender.builder()
				.withDelimiter(",")
				.withLength(2);
		final var buildStep = optionalEvaluatorStep.withEstimation(Function.identity(), 0);
		assertThat(buildStep).isSameAs(optionalEvaluatorStep);
		final var dsvMender = buildStep.build();
		final var estimationEvaluators = dsvMender.getEstimationEvaluators();
		assertThat(estimationEvaluators).hasSize(1);
		for (final var estimationEvaluator : estimationEvaluators) {
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo"))).isNaN();
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "bar"))).isNaN();
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "foo"))).isNaN();
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "bar"))).isNaN();
			estimationEvaluator.fit(ObjectArrays.of("foo", "foo"));
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo"))).isEqualTo(1.0d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "bar"))).isEqualTo(1.0d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "foo"))).isEqualTo(0.0d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "bar"))).isEqualTo(0.0d);
			estimationEvaluator.fit(ObjectArrays.of("bar", "bar"));
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo"))).isEqualTo(0.5d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "bar"))).isEqualTo(0.5d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "foo"))).isEqualTo(0.5d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("bar", "bar"))).isEqualTo(0.5d);
		}
	}

	@Test
	void testWithEstimationInvalid() {
		final var delimiterStep = DsvMender.builder();
		final var lengthStep = delimiterStep.withDelimiter(",");
		final var optionalEvaluatorStep = lengthStep.withLength(2);
		assertThatNullPointerException().isThrownBy(() -> optionalEvaluatorStep.withEstimation(null));
		assertThatNullPointerException().isThrownBy(() -> optionalEvaluatorStep.withEstimation(Function.identity(), (int[]) null));
		assertThatIllegalArgumentException().isThrownBy(() -> optionalEvaluatorStep.withEstimation(Function.identity(), IntArrays.EMPTY));
		assertThatIllegalArgumentException().isThrownBy(() -> optionalEvaluatorStep.withEstimation(Function.identity(), -1));
		assertThatIllegalArgumentException().isThrownBy(() -> optionalEvaluatorStep.withEstimation(Function.identity(), 2));
	}

	@Test
	void testBasic() {
		{
			final var dsvMender = DsvMender.basic(',', 2);
			assertThat(dsvMender.getDelimiter()).isEqualTo(",");
		}
		final var dsvMender = DsvMender.basic(",", 2);
		assertThat(dsvMender.getDelimiter()).isEqualTo(",");
		assertThat(dsvMender.getLength()).isEqualTo(2);
		final var estimationEvaluators = dsvMender.getEstimationEvaluators();
		assertThat(estimationEvaluators).hasSize(2 * dsvMender.getLength());
		for (final var estimationEvaluator : estimationEvaluators) {
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo"))).isNaN();
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of(Strings.EMPTY, Strings.EMPTY))).isNaN();
			estimationEvaluator.fit(ObjectArrays.of("foo", "foo"));
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo"))).isEqualTo(1.0d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of(Strings.EMPTY, Strings.EMPTY))).isEqualTo(0.0d);
		}
	}
}