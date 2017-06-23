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
package org.mender;

/**
 * <p>An {@code Evaluator} is some kind of internal {@link Mender} component that is able to evaluate an element based
 * on previous ones that were given to be adjusted. The evaluation can be done using
 * {@link org.mender.criteria.Constraint} or {@link org.mender.criteria.Estimation} objects.</p>
 *
 * @param <E> Element's type
 * @since 1.0
 */
public interface Evaluator<E> {
	
	/**
	 * <p>Check if the element passes all configured {@code Constraint} objects.</p>
	 * 
	 * @param element The element to check
	 * @return {@code true} if the element passes all configured {@code Constraint} objects
	 */
	boolean checkConstraints(final E element);

	/**
	 * <p>Adjust the {@code Evaluator} using the given element <i>(expected to adjust configured {@code Estimation}
	 * objects)</i>.</p>
	 * 
	 * @param element The element to adjust
	 */
	void adjustEstimations(final E element);
	
	/**
	 * <p>Evaluate the given element by returning a score <i>(the score should be computed with {@code Constraint} or
	 * {@code Estimation} objects)</i>.</p>
	 * 
	 * @param element The element to evaluate
	 * @return An evaluation score, preferably between 0 and 1
	 */
	double evaluate(final E element);
}