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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * <p>A {@link Criterion} that collects values, the score is equal to the frequency among all collected values.</p>
 * 
 * <p><b>Note</b>: Values are transformed using a function to be stored and collected
 * <i>(example: collect by features)</i>.</p>
 *
 * @param <V> Value's type
 * @param <S> Value's store type
 * @since 1.0
 */
public class Estimation<V, S> implements Criterion<V> {

	/**
	 * <p>Internal used function.</p>
	 */
	private final Function<V, S> function;
	
	/**
	 * <p>Bag of stored values.</p>
	 */
	private final Map<S, AtomicLong> bag = new HashMap<>();
	
	/**
	 * <p>Total values in the bag <i>(different of the total of unique values)</i>.</p>
	 */
	private long total = 0;
	
	/**
	 * <p>Constructor using a function that will be used to be applied to values.<p>
	 * 
	 * @param function The function
	 * @throws NullPointerException If the function is null
	 */
	public Estimation(final Function<V, S> function) {
		if (null == function) {
			throw new NullPointerException("Invalid function (not null expected)");
		}
		this.function = function;
	}

	/**
	 * <p>Adjust by adding a value.</p>
	 * 
	 * @param value The value to add
	 */
	public void adjust(final V value) {
		final S key = function.apply(value);
		if (bag.containsKey(key)) {
			bag.get(key).incrementAndGet();
		} else {
			bag.put(key, new AtomicLong(1L));
		}
		++total;
	}

	@Override
	public double calculate(final V value) {
		final S key = function.apply(value);
		if (0 < total && bag.containsKey(key)) {
			return (double) bag.get(key).get() / total;
		} else {
			return 0.0d;
		}
	}
	
	/**
	 * <p>Sugar constructor for easiest uses with generic.</p>
	 * 
	 * @param <V> Value's type
	 * @param <S> Value's store type
	 * @param function The function
	 * @return The instantiated {@code Estimation}
	 */
	public static <V, S> Estimation<V, S> of(final Function<V, S> function) {
		return new Estimation<>(function);
	}
}