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

import com.github.alexisjehan.javanilla.lang.array.ObjectArrays;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

final class DsvMendResultTest {

	private static final String[] VALUE = ObjectArrays.of("foo");
	private static final Set<DsvMendCandidate> CANDIDATES;
	private static final DsvMendCandidate BEST_CANDIDATE;

	static {
		final var candidate = new DsvMendCandidate(ObjectArrays.of("foo"), 1.0d);
		CANDIDATES = Set.of(candidate);
		BEST_CANDIDATE = candidate;
	}

	private final DsvMendResult mendResult = new DsvMendResult(VALUE, CANDIDATES, BEST_CANDIDATE);

	@Test
	void testConstructorImmutable() {
		final var value = VALUE.clone();
		final var mendResult = new DsvMendResult(value, CANDIDATES, BEST_CANDIDATE);
		value[0] = null;
		assertThat(mendResult.getValue()).isEqualTo(VALUE);
	}

	@Test
	void testConstructorInvalid() {
		assertThatNullPointerException()
				.isThrownBy(() -> new DsvMendResult(null, CANDIDATES, BEST_CANDIDATE));
		assertThatIllegalArgumentException()
				.isThrownBy(() -> new DsvMendResult(ObjectArrays.empty(String.class), CANDIDATES, BEST_CANDIDATE));
		assertThatNullPointerException()
				.isThrownBy(() -> new DsvMendResult(ObjectArrays.of((String) null), CANDIDATES, BEST_CANDIDATE));
		assertThatNullPointerException()
				.isThrownBy(() -> new DsvMendResult(VALUE, null, BEST_CANDIDATE));
		assertThatIllegalArgumentException()
				.isThrownBy(() -> new DsvMendResult(VALUE, Set.of(), BEST_CANDIDATE));
		assertThatNullPointerException()
				.isThrownBy(() -> new DsvMendResult(VALUE, Collections.singleton(null), BEST_CANDIDATE));
		assertThatNullPointerException()
				.isThrownBy(() -> new DsvMendResult(VALUE, CANDIDATES, null));
	}

	@Test
	void testEqualsAndHashCodeAndToString() {
		assertThat(mendResult.equals(mendResult)).isTrue();
		assertThat(mendResult).isNotEqualTo(new Object());
		assertThat(
				new DsvMendResult(
						VALUE,
						CANDIDATES,
						BEST_CANDIDATE
				)
		).satisfies(otherMendResult -> {
			assertThat(mendResult).isNotSameAs(otherMendResult);
			assertThat(mendResult).isEqualTo(otherMendResult);
			assertThat(mendResult).hasSameHashCodeAs(otherMendResult);
			assertThat(mendResult).hasToString(otherMendResult.toString());
		});
		assertThat(
				new DsvMendResult(
						ObjectArrays.of("bar"),
						CANDIDATES,
						BEST_CANDIDATE
				)
		).satisfies(otherMendResult -> {
			assertThat(mendResult).isNotSameAs(otherMendResult);
			assertThat(mendResult).isNotEqualTo(otherMendResult);
			assertThat(mendResult).doesNotHaveSameHashCodeAs(otherMendResult);
			assertThat(mendResult).doesNotHaveToString(otherMendResult.toString());
		});
		assertThat(
				new DsvMendResult(
						VALUE,
						Set.of(
								new DsvMendCandidate(ObjectArrays.of("foo"), 1.0d),
								new DsvMendCandidate(ObjectArrays.of("bar"), 2.0d)
						),
						BEST_CANDIDATE
				)
		).satisfies(otherMendResult -> {
			assertThat(mendResult).isNotSameAs(otherMendResult);
			assertThat(mendResult).isNotEqualTo(otherMendResult);
			assertThat(mendResult).doesNotHaveSameHashCodeAs(otherMendResult);
			assertThat(mendResult).doesNotHaveToString(otherMendResult.toString());
		});
		assertThat(
				new DsvMendResult(
						VALUE,
						CANDIDATES,
						new DsvMendCandidate(ObjectArrays.of("bar"), 2.0d)
				)
		).satisfies(otherMendResult -> {
			assertThat(mendResult).isNotSameAs(otherMendResult);
			assertThat(mendResult).isNotEqualTo(otherMendResult);
			assertThat(mendResult).doesNotHaveSameHashCodeAs(otherMendResult);
			assertThat(mendResult).doesNotHaveToString(otherMendResult.toString());
		});
	}

	@Test
	void testGetters() {
		assertThat(mendResult.getValue()).isEqualTo(VALUE);
		assertThat(mendResult.getCandidates()).isEqualTo(CANDIDATES);
		assertThat(mendResult.getBestCandidate()).isEqualTo(BEST_CANDIDATE);
	}

	@Test
	void testGettersImmutable() {
		assertThat(mendResult.getValue()).isEqualTo(VALUE);
		mendResult.getValue()[0] = null;
		assertThat(mendResult.getValue()).isEqualTo(VALUE);
	}
}