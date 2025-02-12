/*
 * MIT License
 *
 * Copyright (c) 2017-2025 Alexis Jehan
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
package com.github.alexisjehan.mender.api;

import java.util.Set;

/**
 * A result of a mending operation containing all candidates and the best one.
 * @param <V> the value's type
 * @param <C> candidates' type
 * @since 1.0.0
 */
public interface MendResult<V, C extends MendCandidate<V>> {

	/**
	 * Get the initial value.
	 * @return the initial value
	 * @since 1.0.0
	 */
	V getValue();

	/**
	 * Get the {@link Set} of all candidates.
	 * @return the {@link Set} of all candidates
	 * @since 1.0.0
	 */
	Set<C> getCandidates();

	/**
	 * Get the best candidate.
	 * @return the best candidate
	 * @since 1.0.0
	 */
	C getBestCandidate();
}