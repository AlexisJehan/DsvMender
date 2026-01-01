/*
 * MIT License
 *
 * Copyright (c) 2017-2026 Alexis Jehan
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

	private final DsvMendResult dsvMendResult = new DsvMendResult(VALUE, CANDIDATES, BEST_CANDIDATE);

	@Test
	void testConstructorImmutable() {
		final var value = VALUE.clone();
		assertThat(new DsvMendResult(value, CANDIDATES, BEST_CANDIDATE))
				.satisfies(immutableDsvMendResult -> {
					value[0] = null;
					assertThat(immutableDsvMendResult.getValue()).isEqualTo(VALUE);
				});
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
		assertThat(dsvMendResult.equals(dsvMendResult)).isTrue();
		assertThat(dsvMendResult).isNotEqualTo(new Object());
		assertThat(
				new DsvMendResult(
						VALUE,
						CANDIDATES,
						BEST_CANDIDATE
				)
		).satisfies(otherDsvMendResult -> {
			assertThat(otherDsvMendResult).isNotSameAs(dsvMendResult);
			assertThat(otherDsvMendResult).isEqualTo(dsvMendResult);
			assertThat(otherDsvMendResult).hasSameHashCodeAs(dsvMendResult);
			assertThat(otherDsvMendResult).hasToString(dsvMendResult.toString());
		});
		assertThat(
				new DsvMendResult(
						ObjectArrays.of("bar"),
						CANDIDATES,
						BEST_CANDIDATE
				)
		).satisfies(otherDsvMendResult -> {
			assertThat(otherDsvMendResult).isNotSameAs(dsvMendResult);
			assertThat(otherDsvMendResult).isNotEqualTo(dsvMendResult);
			assertThat(otherDsvMendResult).doesNotHaveSameHashCodeAs(dsvMendResult);
			assertThat(otherDsvMendResult).doesNotHaveToString(dsvMendResult.toString());
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
		).satisfies(otherDsvMendResult -> {
			assertThat(otherDsvMendResult).isNotSameAs(dsvMendResult);
			assertThat(otherDsvMendResult).isNotEqualTo(dsvMendResult);
			assertThat(otherDsvMendResult).doesNotHaveSameHashCodeAs(dsvMendResult);
			assertThat(otherDsvMendResult).doesNotHaveToString(dsvMendResult.toString());
		});
		assertThat(
				new DsvMendResult(
						VALUE,
						CANDIDATES,
						new DsvMendCandidate(ObjectArrays.of("bar"), 2.0d)
				)
		).satisfies(otherDsvMendResult -> {
			assertThat(otherDsvMendResult).isNotSameAs(dsvMendResult);
			assertThat(otherDsvMendResult).isNotEqualTo(dsvMendResult);
			assertThat(otherDsvMendResult).doesNotHaveSameHashCodeAs(dsvMendResult);
			assertThat(otherDsvMendResult).doesNotHaveToString(dsvMendResult.toString());
		});
	}

	@Test
	void testGetters() {
		assertThat(dsvMendResult.getValue()).isEqualTo(VALUE);
		assertThat(dsvMendResult.getCandidates()).isEqualTo(CANDIDATES);
		assertThat(dsvMendResult.getBestCandidate()).isEqualTo(BEST_CANDIDATE);
	}

	@Test
	void testGettersImmutable() {
		assertThat(dsvMendResult.getValue()).isEqualTo(VALUE);
		dsvMendResult.getValue()[0] = null;
		assertThat(dsvMendResult.getValue()).isEqualTo(VALUE);
	}
}