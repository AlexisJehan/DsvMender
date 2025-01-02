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
package com.github.alexisjehan.mender.api.evaluators;

import com.github.alexisjehan.javanilla.misc.quality.Ensure;
import com.github.alexisjehan.javanilla.util.bag.Bag;
import com.github.alexisjehan.javanilla.util.bag.MapBag;

import java.util.function.Function;

/**
 * An empiric {@link Evaluator} that can be fitted to evaluate transformed values based on their frequency.
 * @param <V> the value's type
 * @since 1.0.0
 */
public final class EstimationEvaluator<V> implements Evaluator<V> {

	/**
	 * Transformer {@link Function}.
	 * @since 1.0.0
	 */
	private final Function<V, ?> transformer;

	/**
	 * {@link Bag} to count transformed values.
	 * @since 1.0.0
	 */
	private final Bag<Object> bag = new MapBag<>();

	/**
	 * Constructor with a transformer {@link Function}.
	 * @param transformer the transformer {@link Function}
	 * @throws NullPointerException if the transformer {@link Function} is {@code null}
	 * @since 1.0.0
	 */
	public EstimationEvaluator(final Function<V, ?> transformer) {
		Ensure.notNull("transformer", transformer);
		this.transformer = transformer;
	}

	/**
	 * Adjust the frequency of the given value after being transformed.
	 * @param value the value to fit
	 * @since 1.0.0
	 */
	public void fit(final V value) {
		bag.add(transformer.apply(value));
	}

	/**
	 * Evaluate the given value after being transformed, based on the frequency among all of them.
	 * @param value the value to evaluate
	 * @return a score between {@code 0} and {@code 1}
	 * @since 1.0.0
	 */
	@Override
	public double evaluate(final V value) {
		final var size = bag.size();
		if (0L == size) {
			return Double.NaN;
		}
		return (double) bag.count(transformer.apply(value)) / size;
	}
}