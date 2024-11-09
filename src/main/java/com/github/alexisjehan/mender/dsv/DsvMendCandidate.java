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

import com.github.alexisjehan.javanilla.misc.quality.Ensure;
import com.github.alexisjehan.javanilla.misc.quality.Equals;
import com.github.alexisjehan.javanilla.misc.quality.HashCode;
import com.github.alexisjehan.javanilla.misc.quality.ToString;
import com.github.alexisjehan.javanilla.misc.tuple.Pair;
import com.github.alexisjehan.mender.api.MendCandidate;

/**
 * An immutable {@link MendCandidate} implementation to work with {@link DsvMender}.
 *
 * <p><b>Note</b>: This class implements its own {@link #equals(Object)}, {@link #hashCode()} and {@link #toString()}
 * methods.</p>
 * @since 1.0.0
 */
public final class DsvMendCandidate implements MendCandidate<String[]> {

	/**
	 * Fixed value.
	 * @since 1.0.0
	 */
	private final String[] value;

	/**
	 * Value's score.
	 * @since 1.0.0
	 */
	private final double score;

	/**
	 * Constructor with a fixed value and its score.
	 * @param value the fixed value
	 * @param score the score
	 * @throws NullPointerException if the fixed value is {@code null}
	 * @throws IllegalArgumentException if the fixed value is empty or if the score is lower than {@code 0}
	 * @since 1.0.0
	 */
	DsvMendCandidate(final String[] value, final double score) {
		Ensure.notNullAndNotEmpty("value", value);
		Ensure.notNullAndNotNullElements("value", value);
		Ensure.greaterThanOrEqualTo("score", score, 0.0d);
		this.value = value.clone();
		this.score = score;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof DsvMendCandidate)) {
			return false;
		}
		final var other = (DsvMendCandidate) object;
		return Equals.equals(value, other.value)
				&& Equals.equals(score, other.score);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return HashCode.of(
				HashCode.hashCode(value),
				HashCode.hashCode(score)
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return ToString.of(
				this,
				Pair.of("value", ToString.toString(value)),
				Pair.of("score", ToString.toString(score))
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getValue() {
		return value.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getScore() {
		return score;
	}
}