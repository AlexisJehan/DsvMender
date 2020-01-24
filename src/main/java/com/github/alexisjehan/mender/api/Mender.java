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
package com.github.alexisjehan.mender.api;

import java.util.Optional;

/**
 * <p>Interface for objects that can mend a value. Fixed values are generated and then scored by some
 * {@link com.github.alexisjehan.mender.api.evaluators.Evaluator}s and the best one is returned.</p>
 * @param <V> the value's type
 * @param <R> the last result's type
 * @since 1.0.0
 */
public interface Mender<V, R extends MendResult<V, ? extends MendCandidate<V>>> {

	/**
	 * <p>Mend the given value if needed.</p>
	 * @param value the value to mend
	 * @return the best fixed value
	 * @throws MendException might occurs if mending the value is not possible
	 * @since 1.0.0
	 */
	V mend(final V value);

	/**
	 * <p>Optionally get the {@link MendResult} of the last {@link #mend(Object)} call.</p>
	 * @return an {@link Optional} of the last {@link MendResult}
	 * @since 1.0.0
	 */
	Optional<R> getLastResult();
}