/*
MIT License

Copyright (c) 2017 Alexis Jehan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package org.mender.criteria;

import java.util.function.Predicate;

/**
 * <p>A {@link Criterion} that returns a binary <i>(0 or 1)</i> score using a predicate on values.</p>
 * 
 * @param <V> Value's type
 * @since 1.0
 */
public class Constraint<V> implements Criterion<V> {

	/**
	 * <p>Internal used predicate.</p>
	 */
	private final Predicate<V> predicate;
	
	/**
	 * <p>Constructor using a predicate that will be used to test the value.<p>
	 * 
	 * @param predicate The predicate function
	 * @throws NullPointerException If the predicate is null
	 */
	public Constraint(final Predicate<V> predicate) {
		if (null == predicate) {
			throw new NullPointerException("Invalid predicate (not null expected)");
		}
		this.predicate = predicate;
	}
	
	/**
	 * <p>Check if the value passes the predicate.</p>
	 * 
	 * @param value The value to check
	 * @return {@code true} if the value passes the predicate
	 */
	public boolean check(final V value) {
		return predicate.test(value);
	}
	
	@Override
	public double calculate(final V value) {
		return predicate.test(value) ? 1.0d : 0.0d;
	}

	/**
	 * <p>Sugar constructor for easiest uses with generic.</p>
	 * 
	 * @param <V> Value's type
	 * @param predicate The predicate function
	 * @return The instantiated {@code Constraint}
	 */
	public static <V> Constraint<V> of(final Predicate<V> predicate) {
		return new Constraint<>(predicate);
	}
}