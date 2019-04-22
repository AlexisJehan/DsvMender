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

import com.github.alexisjehan.mender.api.MendResult;
import com.github.alexisjehan.javanilla.misc.quality.Ensure;
import com.github.alexisjehan.javanilla.misc.quality.Equals;
import com.github.alexisjehan.javanilla.misc.quality.HashCode;
import com.github.alexisjehan.javanilla.misc.quality.ToString;
import com.github.alexisjehan.javanilla.misc.tuples.Pair;

import java.util.Set;

/**
 * <p>An immutable {@link MendResult} implementation to work with {@link DsvMender}.</p>
 * <p><b>Note</b>: This class implements its own {@link #equals(Object)}, {@link #hashCode()} and {@link #toString()}
 * methods.</p>
 * @since 1.0.0
 */
public final class DsvMendResult implements MendResult<String[], DsvMendCandidate> {

	/**
	 * <p>Initial value.</p>
	 * @since 1.0.0
	 */
	private final String[] value;

	/**
	 * <p>{@link Set} of all candidates.</p>
	 * @since 1.0.0
	 */
	private final Set<DsvMendCandidate> candidates;

	/**
	 * <p>Best candidate.</p>
	 * @since 1.0.0
	 */
	private final DsvMendCandidate bestCandidate;

	/**
	 * <p>Constructor with an initial value, its candidates and the best one.</p>
	 * @param value the initial value
	 * @param candidates the {@link Set} of all candidates
	 * @param bestCandidate the best candidate
	 * @throws NullPointerException if the initial value, the {@link Set} of all candidates or any of them or the best
	 * candidate is {@code null}
	 * @throws IllegalArgumentException if the initial value or the {@link Set} of candidates is empty
	 * @since 1.0.0
	 */
	DsvMendResult(final String[] value, final Set<DsvMendCandidate> candidates, final DsvMendCandidate bestCandidate) {
		Ensure.notNullAndNotEmpty("value", value);
		Ensure.notNullAndNotNullElements("candidates", candidates);
		Ensure.notNullAndNotEmpty("candidates", candidates);
		Ensure.notNull("bestCandidate", bestCandidate);
		this.value = value.clone();
		this.candidates = Set.copyOf(candidates);
		this.bestCandidate = bestCandidate;
	}

	@Override
	public boolean equals(final Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof DsvMendResult)) {
			return false;
		}
		final var other = (DsvMendResult) object;
		return Equals.equals(value, other.value)
				&& Equals.equals(candidates, other.candidates)
				&& Equals.equals(bestCandidate, other.bestCandidate);
	}

	@Override
	public int hashCode() {
		return HashCode.of(
				HashCode.hashCode(value),
				HashCode.hashCode(candidates),
				HashCode.hashCode(bestCandidate)
		);
	}

	@Override
	public String toString() {
		return ToString.of(
				this,
				Pair.of("value", ToString.toString(value)),
				Pair.of("candidates", ToString.toString(candidates.size())),
				Pair.of("bestCandidate", ToString.toString(bestCandidate))
		);
	}

	@Override
	public String[] getValue() {
		return value.clone();
	}

	@Override
	public Set<DsvMendCandidate> getCandidates() {
		return candidates;
	}

	@Override
	public DsvMendCandidate getBestCandidate() {
		return bestCandidate;
	}
}