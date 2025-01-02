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

import java.util.function.Predicate;

/**
 * A binary {@link Evaluator} that is based on values validity.
 * @param <V> the value's type
 * @since 1.0.0
 */
public final class ConstraintEvaluator<V> implements Evaluator<V> {

	/**
	 * Validator {@link Predicate}.
	 * @since 1.0.0
	 */
	private final Predicate<V> validator;

	/**
	 * Constructor with a validator {@link Predicate}.
	 * @param validator the validator {@link Predicate}
	 * @throws NullPointerException if the validator {@link Predicate} is {@code null}
	 * @since 1.0.0
	 */
	public ConstraintEvaluator(final Predicate<V> validator) {
		Ensure.notNull("validator", validator);
		this.validator = validator;
	}

	/**
	 * Check that the given value is valid.
	 * @param value the value to check
	 * @return {@code true} if the value is valid
	 * @since 1.0.0
	 */
	public boolean isValid(final V value) {
		return validator.test(value);
	}

	/**
	 * Evaluate the given value based on its validity.
	 * @param value the value to evaluate
	 * @return {@code 1} if the value is valid, {@code NaN} otherwise
	 * @since 1.0.0
	 */
	@Override
	public double evaluate(final V value) {
		return validator.test(value) ? 1.0d : Double.NaN;
	}
}