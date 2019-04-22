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

import com.github.alexisjehan.javanilla.lang.array.ObjectArrays;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * <p>{@link DsvMendCandidate} unit tests.</p>
 */
final class DsvMendCandidateTest {

	@Test
	void testConstructorImmutable() {
		final var values = ObjectArrays.singleton("foo");
		final var mendCandidate = new DsvMendCandidate(values, 1.0d);
		assertThat(mendCandidate.getValue()).containsExactly("foo");
		values[0] = "bar";
		assertThat(mendCandidate.getValue()).containsExactly("foo");
	}

	@Test
	void testConstructorInvalid() {
		assertThatNullPointerException().isThrownBy(() -> new DsvMendCandidate(null, 1.0d));
		assertThatIllegalArgumentException().isThrownBy(() -> new DsvMendCandidate(ObjectArrays.empty(String.class), 1.0d));
		assertThatIllegalArgumentException().isThrownBy(() -> new DsvMendCandidate(ObjectArrays.singleton("foo"), -1.0d));
	}

	@Test
	void testEqualsHashCodeToString() {
		final var mendCandidate = new DsvMendCandidate(ObjectArrays.singleton("foo"), 1.0d);
		assertThat(mendCandidate).isEqualTo(mendCandidate);
		assertThat(mendCandidate).isNotEqualTo(1);
		{
			final var otherMendCandidate = new DsvMendCandidate(mendCandidate.getValue(), mendCandidate.getScore());
			assertThat(mendCandidate).isEqualTo(otherMendCandidate);
			assertThat(mendCandidate).hasSameHashCodeAs(otherMendCandidate);
			assertThat(mendCandidate).hasToString(otherMendCandidate.toString());
		}
		{
			final var otherMendCandidate = new DsvMendCandidate(ObjectArrays.singleton("bar"), mendCandidate.getScore());
			assertThat(mendCandidate).isNotEqualTo(otherMendCandidate);
			assertThat(mendCandidate.hashCode()).isNotEqualTo(otherMendCandidate.hashCode());
			assertThat(mendCandidate.toString()).isNotEqualTo(otherMendCandidate.toString());
		}
		{
			final var otherMendCandidate = new DsvMendCandidate(mendCandidate.getValue(), 0.5d);
			assertThat(mendCandidate).isNotEqualTo(otherMendCandidate);
			assertThat(mendCandidate.hashCode()).isNotEqualTo(otherMendCandidate.hashCode());
			assertThat(mendCandidate.toString()).isNotEqualTo(otherMendCandidate.toString());
		}
	}

	@Test
	void testGetters() {
		final var mendCandidate = new DsvMendCandidate(ObjectArrays.singleton("foo"), 1.0d);
		assertThat(mendCandidate.getValue()).containsExactly("foo");
		assertThat(mendCandidate.getScore()).isEqualTo(1.0d);
	}

	@Test
	void testGettersImmutable() {
		final var mendCandidate = new DsvMendCandidate(ObjectArrays.singleton("foo"), 1.0d);
		assertThat(mendCandidate.getValue()).containsExactly("foo");
		mendCandidate.getValue()[0] = "bar";
		assertThat(mendCandidate.getValue()).containsExactly("foo");
	}
}