/*
 * MIT License
 *
 * Copyright (c) 2017-2022 Alexis Jehan
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
package com.github.alexisjehan.mender.api.evaluators;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * <p>{@link EstimationEvaluator} unit tests.</p>
 */
final class EstimationEvaluatorTest {

	@Test
	void testConstructorInvalid() {
		assertThatNullPointerException().isThrownBy(() -> new EstimationEvaluator<>(null));
	}

	@Test
	void testFitAndEvaluate() {
		assertThat(new EstimationEvaluator<>(Function.identity())).satisfies(estimationEvaluator -> {
			assertThat(estimationEvaluator.evaluate("foo")).isNaN();
			assertThat(estimationEvaluator.evaluate("bar")).isNaN();
			estimationEvaluator.fit("foo");
			assertThat(estimationEvaluator.evaluate("foo")).isEqualTo(1.0d);
			assertThat(estimationEvaluator.evaluate("bar")).isZero();
			estimationEvaluator.fit("bar");
			assertThat(estimationEvaluator.evaluate("foo")).isEqualTo(0.5d);
			assertThat(estimationEvaluator.evaluate("bar")).isEqualTo(0.5d);
		});
		assertThat(new EstimationEvaluator<>(String::length)).satisfies(estimationEvaluator -> {
			assertThat(estimationEvaluator.evaluate("foo")).isNaN();
			assertThat(estimationEvaluator.evaluate("fooo")).isNaN();
			estimationEvaluator.fit("foo");
			assertThat(estimationEvaluator.evaluate("foo")).isEqualTo(1.0d);
			assertThat(estimationEvaluator.evaluate("fooo")).isZero();
			estimationEvaluator.fit("fooo");
			assertThat(estimationEvaluator.evaluate("foo")).isEqualTo(0.5d);
			assertThat(estimationEvaluator.evaluate("fooo")).isEqualTo(0.5d);
		});
	}
}