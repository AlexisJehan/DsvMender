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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

final class DsvMendCandidateTest {

	private static final String[] VALUE = ObjectArrays.of("foo");
	private static final double SCORE = 1.0d;

	private final DsvMendCandidate mendCandidate = new DsvMendCandidate(VALUE, SCORE);

	@Test
	void testConstructorImmutable() {
		final var value = VALUE.clone();
		final var mendCandidate = new DsvMendCandidate(value, SCORE);
		value[0] = null;
		assertThat(mendCandidate.getValue()).isEqualTo(VALUE);
	}

	@Test
	void testConstructorInvalid() {
		assertThatNullPointerException()
				.isThrownBy(() -> new DsvMendCandidate(null, SCORE));
		assertThatIllegalArgumentException()
				.isThrownBy(() -> new DsvMendCandidate(ObjectArrays.empty(String.class), SCORE));
		assertThatNullPointerException()
				.isThrownBy(() -> new DsvMendCandidate(ObjectArrays.of((String) null), SCORE));
		assertThatIllegalArgumentException()
				.isThrownBy(() -> new DsvMendCandidate(VALUE, -1.0d));
	}

	@Test
	void testEqualsAndHashCodeAndToString() {
		assertThat(mendCandidate.equals(mendCandidate)).isTrue();
		assertThat(mendCandidate).isNotEqualTo(new Object());
		assertThat(new DsvMendCandidate(VALUE, SCORE)).satisfies(otherMendCandidate -> {
			assertThat(mendCandidate).isNotSameAs(otherMendCandidate);
			assertThat(mendCandidate).isEqualTo(otherMendCandidate);
			assertThat(mendCandidate).hasSameHashCodeAs(otherMendCandidate);
			assertThat(mendCandidate).hasToString(otherMendCandidate.toString());
		});
		assertThat(new DsvMendCandidate(ObjectArrays.of("bar"), SCORE)).satisfies(otherMendCandidate -> {
			assertThat(mendCandidate).isNotSameAs(otherMendCandidate);
			assertThat(mendCandidate).isNotEqualTo(otherMendCandidate);
			assertThat(mendCandidate).doesNotHaveSameHashCodeAs(otherMendCandidate);
			assertThat(mendCandidate).doesNotHaveToString(otherMendCandidate.toString());
		});
		assertThat(new DsvMendCandidate(VALUE, 2.0d)).satisfies(otherMendCandidate -> {
			assertThat(mendCandidate).isNotSameAs(otherMendCandidate);
			assertThat(mendCandidate).isNotEqualTo(otherMendCandidate);
			assertThat(mendCandidate).doesNotHaveSameHashCodeAs(otherMendCandidate);
			assertThat(mendCandidate).doesNotHaveToString(otherMendCandidate.toString());
		});
	}

	@Test
	void testGetters() {
		assertThat(mendCandidate.getValue()).isEqualTo(VALUE);
		assertThat(mendCandidate.getScore()).isEqualTo(SCORE);
	}

	@Test
	void testGettersImmutable() {
		assertThat(mendCandidate.getValue()).isEqualTo(VALUE);
		mendCandidate.getValue()[0] = null;
		assertThat(mendCandidate.getValue()).isEqualTo(VALUE);
	}
}