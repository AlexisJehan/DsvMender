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
import com.github.alexisjehan.javanilla.lang.array.ObjectArrays;
import com.github.alexisjehan.mender.api.MendException;
import com.github.alexisjehan.mender.api.evaluators.ConstraintEvaluator;
import com.github.alexisjehan.mender.api.evaluators.EstimationEvaluator;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

final class DsvMenderTest {

	private static final String DELIMITER = ",";
	private static final int LENGTH = 3;
	private static final int MAX_DEPTH = 5;
	private static final Supplier<Set<ConstraintEvaluator<String[]>>> CONSTRAINT_EVALUATORS_GENERATOR = () -> Set.of(new ConstraintEvaluator<>(values -> "foo".equals(values[0])));
	private static final Supplier<Set<EstimationEvaluator<String[]>>> ESTIMATION_EVALUATORS_GENERATOR = () -> Set.of(new EstimationEvaluator<>(values -> values[2]));

	@Test
	void testConstructorInvalid() {
		assertThatNullPointerException().isThrownBy(() -> new DsvMender(null, LENGTH, MAX_DEPTH, CONSTRAINT_EVALUATORS_GENERATOR.get(), ESTIMATION_EVALUATORS_GENERATOR.get()));
		assertThatIllegalArgumentException().isThrownBy(() -> new DsvMender(Strings.EMPTY, LENGTH, MAX_DEPTH, CONSTRAINT_EVALUATORS_GENERATOR.get(), ESTIMATION_EVALUATORS_GENERATOR.get()));
		assertThatIllegalArgumentException().isThrownBy(() -> new DsvMender(DELIMITER, 1, MAX_DEPTH, CONSTRAINT_EVALUATORS_GENERATOR.get(), ESTIMATION_EVALUATORS_GENERATOR.get()));
		assertThatIllegalArgumentException().isThrownBy(() -> new DsvMender(DELIMITER, LENGTH, 0, CONSTRAINT_EVALUATORS_GENERATOR.get(), ESTIMATION_EVALUATORS_GENERATOR.get()));
		assertThatNullPointerException().isThrownBy(() -> new DsvMender(DELIMITER, LENGTH, MAX_DEPTH, null, ESTIMATION_EVALUATORS_GENERATOR.get()));
		assertThatNullPointerException().isThrownBy(() -> new DsvMender(DELIMITER, LENGTH, MAX_DEPTH, Collections.singleton(null), ESTIMATION_EVALUATORS_GENERATOR.get()));
		assertThatNullPointerException().isThrownBy(() -> new DsvMender(DELIMITER, LENGTH, MAX_DEPTH, CONSTRAINT_EVALUATORS_GENERATOR.get(), null));
		assertThatNullPointerException().isThrownBy(() -> new DsvMender(DELIMITER, LENGTH, MAX_DEPTH, CONSTRAINT_EVALUATORS_GENERATOR.get(), Collections.singleton(null)));
	}

	@Test
	void testOptimize() {
		final var dsvMender = new DsvMender(DELIMITER, LENGTH, MAX_DEPTH, CONSTRAINT_EVALUATORS_GENERATOR.get(), ESTIMATION_EVALUATORS_GENERATOR.get());
		assertThat(dsvMender.optimize(0, "foo" + DELIMITER)).containsExactly("foo", Strings.EMPTY);
		assertThat(dsvMender.optimize(0, "foo", Strings.EMPTY)).containsExactly("foo", Strings.EMPTY);
		assertThat(dsvMender.optimize(0, "foo", "bar", Strings.EMPTY, Strings.EMPTY)).containsExactly("foo", "bar", DELIMITER);
		assertThat(dsvMender.optimize(0, Strings.EMPTY, "foo")).containsExactly(Strings.EMPTY, "foo");
		assertThat(dsvMender.optimize(0, Strings.EMPTY, Strings.EMPTY, "bar", "foo")).containsExactly(DELIMITER, "bar", "foo");
		assertThat(dsvMender.optimize(0, "foo", Strings.EMPTY, Strings.EMPTY, "bar", Strings.EMPTY)).containsExactly("foo", DELIMITER, "bar", Strings.EMPTY);
		assertThat(dsvMender.optimize(0, "foo", "bar")).containsExactly("foo", "bar");
		assertThat(dsvMender.optimize(0, "foo", Strings.EMPTY, "bar")).containsExactly("foo", Strings.EMPTY, "bar");
		assertThat(dsvMender.optimize(0, "foo", Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, "bar")).containsExactly("foo", DELIMITER.repeat(3), "bar");
		assertThat(dsvMender.optimize(1, "foo", Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, "bar")).containsExactly("foo", Strings.EMPTY, DELIMITER, Strings.EMPTY, "bar");
		assertThat(dsvMender.optimize(2, "foo", Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, "bar")).containsExactly("foo", Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, "bar");
	}

	@Test
	void testOptimizeInvalid() {
		final var dsvMender = new DsvMender(DELIMITER, LENGTH, MAX_DEPTH, CONSTRAINT_EVALUATORS_GENERATOR.get(), ESTIMATION_EVALUATORS_GENERATOR.get());
		assertThatIllegalArgumentException().isThrownBy(() -> dsvMender.optimize(-1, "foo", Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, "bar"));
		assertThatNullPointerException().isThrownBy(() -> dsvMender.optimize(0, (String) null));
		assertThatNullPointerException().isThrownBy(() -> dsvMender.optimize(0, (String[]) null));
	}

	@Test
	void testMend() {
		assertThat(new DsvMender(DELIMITER, LENGTH, MAX_DEPTH, CONSTRAINT_EVALUATORS_GENERATOR.get(), ESTIMATION_EVALUATORS_GENERATOR.get())).satisfies(dsvMender -> {
			assertThat(dsvMender.mend("foo" + DELIMITER.repeat(2) + "bar")).containsExactly("foo", Strings.EMPTY, "bar");
			assertThat(dsvMender.mend("foo", Strings.EMPTY, "bar")).containsExactly("foo", Strings.EMPTY, "bar");
			assertThat(dsvMender.mend("foo")).containsExactly("foo", Strings.EMPTY, Strings.EMPTY);
			assertThat(dsvMender.mend("foo", Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, "bar")).containsExactly("foo", DELIMITER.repeat(2), "bar");
			assertThatExceptionOfType(MendException.class).isThrownBy(() -> dsvMender.mend("bar", Strings.EMPTY, "foo"));
		});
		assertThat(new DsvMender(DELIMITER, LENGTH, MAX_DEPTH, Set.of(), Set.of())).satisfies(
				dsvMender -> assertThatExceptionOfType(MendException.class).isThrownBy(() -> dsvMender.mend("foo", "bar"))
		);
		assertThat(
				new DsvMender(
						DELIMITER,
						LENGTH,
						MAX_DEPTH,
						Set.of(),
						Set.of(
								new EstimationEvaluator<>(values -> values[0].length()),
								new EstimationEvaluator<>(values -> values[1].length()),
								new EstimationEvaluator<>(values -> values[2].length())
						)
				)
		).satisfies(dsvMender -> {
			assertThat(dsvMender.mend("foo", Strings.EMPTY, "bar")).containsExactly("foo", Strings.EMPTY, "bar");
			assertThat(dsvMender.mend("f", "o", Strings.EMPTY, "b", "r")).containsExactly("f" + DELIMITER + "o", Strings.EMPTY, "b" + DELIMITER + "r");
		});
	}

	@Test
	void testMendInvalid() {
		final var dsvMender = new DsvMender(DELIMITER, LENGTH, MAX_DEPTH, CONSTRAINT_EVALUATORS_GENERATOR.get(), ESTIMATION_EVALUATORS_GENERATOR.get());
		assertThatNullPointerException().isThrownBy(() -> dsvMender.mend((String) null));
		assertThatNullPointerException().isThrownBy(() -> dsvMender.mend((String[]) null));
	}

	@Test
	void testGetLastResult() {
		final var dsvMender = new DsvMender(DELIMITER, LENGTH, MAX_DEPTH, CONSTRAINT_EVALUATORS_GENERATOR.get(), ESTIMATION_EVALUATORS_GENERATOR.get());
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
		final var dsvMender = new DsvMender(DELIMITER, LENGTH, MAX_DEPTH, CONSTRAINT_EVALUATORS_GENERATOR.get(), ESTIMATION_EVALUATORS_GENERATOR.get());
		assertThat(dsvMender.getDelimiter()).isEqualTo(DELIMITER);
		assertThat(dsvMender.getLength()).isEqualTo(LENGTH);
		assertThat(dsvMender.getMaxDepth()).isEqualTo(MAX_DEPTH);
		final var constraintEvaluators = dsvMender.getConstraintEvaluators();
		assertThat(constraintEvaluators).hasSize(CONSTRAINT_EVALUATORS_GENERATOR.get().size());
		for (final var constraintEvaluator : constraintEvaluators) {
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(1.0d);
			assertThat(constraintEvaluator.evaluate(ObjectArrays.of("bar", "foo", "foo"))).isNaN();
		}
		final var estimationEvaluators = dsvMender.getEstimationEvaluators();
		assertThat(estimationEvaluators).hasSize(ESTIMATION_EVALUATORS_GENERATOR.get().size());
		for (final var estimationEvaluator : estimationEvaluators) {
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isNaN();
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isNaN();
			estimationEvaluator.fit(ObjectArrays.of("foo", "foo", "foo"));
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(1.0d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "bar"))).isZero();
			estimationEvaluator.fit(ObjectArrays.of("foo", "foo", "bar"));
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "foo"))).isEqualTo(0.5d);
			assertThat(estimationEvaluator.evaluate(ObjectArrays.of("foo", "foo", "bar"))).isEqualTo(0.5d);
		}
	}
}