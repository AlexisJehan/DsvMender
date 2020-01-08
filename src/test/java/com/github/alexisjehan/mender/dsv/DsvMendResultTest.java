/*
 * MIT License
 *
 * Copyright (c) 2017-2020 Alexis Jehan
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

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * <p>{@link DsvMendResult} unit tests.</p>
 */
final class DsvMendResultTest {

	@Test
	void testConstructorImmutable() {
		final var values = ObjectArrays.singleton("foo");
		final var mendResult = new DsvMendResult(values, Set.of(new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d)), new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d));
		assertThat(mendResult.getValue()).containsExactly("foo");
		values[0] = "bar";
		assertThat(mendResult.getValue()).containsExactly("foo");
	}

	@Test
	void testConstructorInvalid() {
		assertThatNullPointerException().isThrownBy(() -> new DsvMendResult(null, Set.of(new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d)), new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d)));
		assertThatIllegalArgumentException().isThrownBy(() -> new DsvMendResult(ObjectArrays.empty(String.class), Set.of(new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d)), new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d)));
		assertThatNullPointerException().isThrownBy(() -> new DsvMendResult(ObjectArrays.singleton("foo"), null, new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d)));
		assertThatNullPointerException().isThrownBy(() -> new DsvMendResult(ObjectArrays.singleton("foo"), new HashSet<>(null), new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d)));
		assertThatIllegalArgumentException().isThrownBy(() -> new DsvMendResult(ObjectArrays.singleton("foo"), Set.of(), new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d)));
		assertThatNullPointerException().isThrownBy(() -> new DsvMendResult(ObjectArrays.singleton("foo"), Set.of(new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d)), null));
	}

	@Test
	void testEqualsHashCodeToString() {
		final var mendResult = new DsvMendResult(ObjectArrays.singleton("foo"), Set.of(new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d)), new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d));
		assertThat(mendResult).isEqualTo(mendResult);
		assertThat(mendResult).isNotEqualTo(1);
		{
			final var otherMendResult = new DsvMendResult(mendResult.getValue(), mendResult.getCandidates(), mendResult.getBestCandidate());
			assertThat(mendResult).isEqualTo(otherMendResult);
			assertThat(mendResult).hasSameHashCodeAs(otherMendResult);
			assertThat(mendResult).hasToString(otherMendResult.toString());
		}
		{
			final var otherMendResult = new DsvMendResult(ObjectArrays.singleton("bar"), mendResult.getCandidates(), mendResult.getBestCandidate());
			assertThat(mendResult).isNotEqualTo(otherMendResult);
			assertThat(mendResult.hashCode()).isNotEqualTo(otherMendResult.hashCode());
			assertThat(mendResult.toString()).isNotEqualTo(otherMendResult.toString());
		}
		{
			final var otherMendResult = new DsvMendResult(mendResult.getValue(), Set.of(new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d), new DsvMendCandidate(ObjectArrays.singleton("bar"), 1.0d)), mendResult.getBestCandidate());
			assertThat(mendResult).isNotEqualTo(otherMendResult);
			assertThat(mendResult.hashCode()).isNotEqualTo(otherMendResult.hashCode());
			assertThat(mendResult.toString()).isNotEqualTo(otherMendResult.toString());
		}
		{
			final var otherMendResult = new DsvMendResult(mendResult.getValue(), mendResult.getCandidates(), new DsvMendCandidate(ObjectArrays.singleton("bar"), 1.0d));
			assertThat(mendResult).isNotEqualTo(otherMendResult);
			assertThat(mendResult.hashCode()).isNotEqualTo(otherMendResult.hashCode());
			assertThat(mendResult.toString()).isNotEqualTo(otherMendResult.toString());
		}
	}

	@Test
	void testGetters() {
		final var mendResult = new DsvMendResult(ObjectArrays.singleton("foo"), Set.of(new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d)), new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d));
		assertThat(mendResult.getValue()).containsExactly("foo");
		assertThat(mendResult.getCandidates()).containsExactly(new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d));
		assertThat(mendResult.getBestCandidate()).isEqualTo(new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d));
	}

	@Test
	void testGettersImmutable() {
		final var mendResult = new DsvMendResult(ObjectArrays.singleton("foo"), Set.of(new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d)), new DsvMendCandidate(ObjectArrays.singleton("bar"), 0.5d));
		assertThat(mendResult.getValue()).containsExactly("foo");
		mendResult.getValue()[0] = "bar";
		assertThat(mendResult.getValue()).containsExactly("foo");
	}
}