/*
MIT License

Copyright (c) 2018 Alexis Jehan

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
 * <p>A {@code Mender} aims to fix invalids elements by learning from valid ones.</p>
 *
 * @param <E> Element's type
 * @since 1.0
 */
public interface Mender<E> {

	/**
	 * <p>Learn from a valid element, for example that can be done using an {@link Evaluator}.</p>
	 * 
	 * @param element The valid element to learn from
	 */
	void fit(final E element);

	/**
	 * <p>Fix an invalid element using custom strategies and knowledges.</p>
	 * 
	 * @param element The invalid element to repair
	 * @return Fixed version of the element
	 * @throws MenderException In particular conditions the {@code Mender} might not be able to fix the given element
	 */
	E fix(final E element) throws MenderException;
}