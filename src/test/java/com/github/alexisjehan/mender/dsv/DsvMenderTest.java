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
import com.github.alexisjehan.javanilla.lang.array.ObjectArrays;
import com.github.alexisjehan.mender.api.MendException;
import com.github.alexisjehan.mender.api.evaluators.ConstraintEvaluator;
import com.github.alexisjehan.mender.api.evaluators.EstimationEvaluator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * <p>{@link DsvMender} unit tests.</p>
 */
final class DsvMenderTest {

	@Test
	void testConstructorInvalid() {
		assertThatNullPointerException().isThrownBy(() -> new DsvMender(null, 3, 5, Set.of(new ConstraintEvaluator<>(values -> "foo".equals(values[0]))), Set.of(new EstimationEvaluator<>(values -> values[2]))));
		assertThatIllegalArgumentException().isThrownBy(() -> new DsvMender(Strings.EMPTY, 3, 5, Set.of(new ConstraintEvaluator<>(values -> "foo".equals(values[0]))), Set.of(new EstimationEvaluator<>(values -> values[2]))));
		assertThatIllegalArgumentException().isThrownBy(() -> new DsvMender(",", 1, 5, Set.of(new ConstraintEvaluator<>(values -> "foo".equals(values[0]))), Set.of(new EstimationEvaluator<>(values -> values[2]))));
		assertThatIllegalArgumentException().isThrownBy(() -> new DsvMender(",", 3, 0, Set.of(new ConstraintEvaluator<>(values -> "foo".equals(values[0]))), Set.of(new EstimationEvaluator<>(values -> values[2]))));
		assertThatNullPointerException().isThrownBy(() -> new DsvMender(",", 3, 5, null, Set.of(new EstimationEvaluator<>(values -> values[2]))));
		assertThatNullPointerException().isThrownBy(() -> new DsvMender(",", 3, 5, Set.of((ConstraintEvaluator<String[]>) null), Set.of(new EstimationEvaluator<>(values -> values[2]))));
		assertThatNullPointerException().isThrownBy(() -> new DsvMender(",", 3, 5, Set.of(new ConstraintEvaluator<>(values -> "foo".equals(values[0]))), null));
		assertThatNullPointerException().isThrownBy(() -> new DsvMender(",", 3, 5, Set.of(new ConstraintEvaluator<>(values -> "foo".equals(values[0]))), Set.of((EstimationEvaluator<String[]>) null)));
	}

	@Test
	void testOptimize() {
		final var dsvMender = new DsvMender(",", 3, 5, Set.of(new ConstraintEvaluator<>(values -> "foo".equals(values[0]))), Set.of(new EstimationEvaluator<>(values -> values[2])));
		assertThat(dsvMender.optimize(0, "foo,")).containsExactly("foo", Strings.EMPTY);
		assertThat(dsvMender.optimize(0, "foo", Strings.EMPTY)).containsExactly("foo", Strings.EMPTY);
		assertThat(dsvMender.optimize(0, "foo", "bar", Strings.EMPTY, Strings.EMPTY)).containsExactly("foo", "bar", ",");
		assertThat(dsvMender.optimize(0, Strings.EMPTY, "foo")).containsExactly(Strings.EMPTY, "foo");
		assertThat(dsvMender.optimize(0, Strings.EMPTY, Strings.EMPTY, "bar", "foo")).containsExactly(",", "bar", "foo");
		assertThat(dsvMender.optimize(0, "foo", Strings.EMPTY, Strings.EMPTY, "bar", Strings.EMPTY)).containsExactly("foo", ",", "bar", Strings.EMPTY);
		assertThat(dsvMender.optimize(0, "foo", "bar")).containsExactly("foo", "bar");
		assertThat(dsvMender.optimize(0, "foo", Strings.EMPTY, "bar")).containsExactly("foo", Strings.EMPTY, "bar");
		assertThat(dsvMender.optimize(0, "foo", Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, "bar")).containsExactly("foo", ",,,", "bar");
		assertThat(dsvMender.optimize(1, "foo", Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, "bar")).containsExactly("foo", Strings.EMPTY, ",", Strings.EMPTY, "bar");
		assertThat(dsvMender.optimize(2, "foo", Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, "bar")).containsExactly("foo", Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, "bar");
	}

	@Test
	void testOptimizeInvalid() {
		final var dsvMender = new DsvMender(",", 3, 5, Set.of(new ConstraintEvaluator<>(values -> "foo".equals(values[0]))), Set.of(new EstimationEvaluator<>(values -> values[2])));
		assertThatIllegalArgumentException().isThrownBy(() -> dsvMender.optimize(-1, "foo", Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, "bar"));
		assertThatNullPointerException().isThrownBy(() -> dsvMender.optimize(0, (String) null));
		assertThatNullPointerException().isThrownBy(() -> dsvMender.optimize(0, (String[]) null));
	}

	@Test
	void testMend() {
		{
			final var dsvMender = new DsvMender(",", 3, 5, Set.of(new ConstraintEvaluator<>(values -> "foo".equals(values[0]))), Set.of(new EstimationEvaluator<>(values -> values[2])));
			assertThat(dsvMender.mend("foo,,bar")).containsExactly("foo", Strings.EMPTY, "bar");
			assertThat(dsvMender.mend("foo", Strings.EMPTY, "bar")).containsExactly("foo", Strings.EMPTY, "bar");
			assertThat(dsvMender.mend("foo")).containsExactly("foo", Strings.EMPTY, Strings.EMPTY);
			assertThat(dsvMender.mend("foo", Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, "bar")).containsExactly("foo", ",,", "bar");
			assertThatExceptionOfType(MendException.class).isThrownBy(() -> dsvMender.mend("bar", Strings.EMPTY, "foo"));
		}
		{
			final var dsvMender = new DsvMender(",", 3, 5, Set.of(), Set.of());
			assertThatExceptionOfType(MendException.class).isThrownBy(() -> dsvMender.mend("foo", "bar"));
		}
		{
			final var dsvMender = new DsvMender(",", 3, 5, Set.of(), Set.of(new EstimationEvaluator<>(values -> values[0].length()), new EstimationEvaluator<>(values -> values[1].length()), new EstimationEvaluator<>(values -> values[2].length())));
			assertThat(dsvMender.mend("foo", Strings.EMPTY, "bar")).containsExactly("foo", Strings.EMPTY, "bar");
			assertThat(dsvMender.mend("f", "o", Strings.EMPTY, "b", "r")).containsExactly("f,o", Strings.EMPTY, "b,r");
		}
	}

	@Test
	void testMendInvalid() {
		final var dsvMender = new DsvMender(",", 3, 5, Set.of(new ConstraintEvaluator<>(values -> "foo".equals(values[0]))), Set.of(new EstimationEvaluator<>(values -> values[2])));
		assertThatNullPointerException().isThrownBy(() -> dsvMender.mend((String) null));
		assertThatNullPointerException().isThrownBy(() -> dsvMender.mend((String[]) null));
	}

	@Test
	void testGetLastResult() {
		final var dsvMender = new DsvMender(",", 3, 5, Set.of(new ConstraintEvaluator<>(values -> "foo".equals(values[0]))), Set.of(new EstimationEvaluator<>(values -> values[2])));
		assertThat(dsvMender.getLastResult()).isEmpty();
		assertThat(dsvMender.mend("foo", Strings.EMPTY, "bar")).containsExactly("foo", Strings.EMPTY, "bar");
		assertThat(dsvMender.getLastResult()).isEmpty();
		assertThat(dsvMender.mend("foo", "bar")).containsExactly("foo", Strings.EMPTY, "bar");
		final var optionalLastResult = dsvMender.getLastResult();
		assertThat(optionalLastResult).isPresent();
		final var lastResult = optionalLastResult.orElseThrow();
		assertThat(lastResult.getValue()).containsExactly("foo", "bar");
		assertThat(lastResult.getCandidates()).containsExactlyInAnyOrder(
				new DsvMendCandidate(ObjectArrays.of(Strings.EMPTY, "foo", "bar"), Double.NaN),
				new DsvMendCandidate(ObjectArrays.of("foo", Strings.EMPTY, "bar"), 1.0d),
				new DsvMendCandidate(ObjectArrays.of("foo", "bar", Strings.EMPTY), 0.5d)
		);
		assertThat(lastResult.getBestCandidate()).isEqualTo(new DsvMendCandidate(ObjectArrays.of("foo", Strings.EMPTY, "bar"), 1.0d));
	}

	@Test
	void testGetters() {
		final var dsvMender = new DsvMender(",", 3, 5, Set.of(new ConstraintEvaluator<>(values -> "foo".equals(values[0]))), Set.of(new EstimationEvaluator<>(values -> values[2])));
		assertThat(dsvMender.getDelimiter()).isEqualTo(",");
		assertThat(dsvMender.getLength()).isEqualTo(3);
		assertThat(dsvMender.getMaxDepth()).isEqualTo(5);
		final var constraintEvaluators = dsvMender.getConstraintEvaluators();
		assertThat(constraintEvaluators).hasSize(1);
		for (final var constraintEvaluator : constraintEvaluators) {
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(1.0d);
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("bar", "foo", "foo"))).isNaN();
		}
		final var estimationEvaluators = dsvMender.getEstimationEvaluators();
		assertThat(estimationEvaluators).hasSize(1);
		for (final var estimationEvaluator : estimationEvaluators) {
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isNaN();
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isNaN();
			estimationEvaluator.fit(ObjectArrays.of("foo", "foo", "foo"));
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(1.0d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "bar"))).isEqualTo(0.0d);
			estimationEvaluator.fit(ObjectArrays.of("foo", "foo", "bar"));
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(0.5d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "bar"))).isEqualTo(0.5d);
		}
	}
}