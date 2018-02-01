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
package org.mender.criteria;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>{@link Estimation} unit tests.</p>
 */
public class EstimationTest {

	@Test
	public void testSimple() {
		final Estimation<String, String> estimation = new Estimation<>(value -> value);
		estimation.adjust("foo");
		estimation.adjust("foo");
		estimation.adjust("foo");
		estimation.adjust("bar");
		Assert.assertEquals(0.75d, estimation.calculate("foo"), 0);
		Assert.assertEquals(0.25d, estimation.calculate("bar"), 0);
	}

	@Test
	public void testStatic() {
		final Estimation<String, String> estimation = Estimation.of(value -> value);
		estimation.adjust("foo");
		estimation.adjust("foo");
		estimation.adjust("foo");
		estimation.adjust("bar");
		Assert.assertEquals(0.75d, estimation.calculate("foo"), 0);
		Assert.assertEquals(0.25d, estimation.calculate("bar"), 0);
	}

	@Test(expected = NullPointerException.class)
	public void testNull() {
		new Estimation<>(null);
	}

	@Test
	public void testNotPresent() {
		final Estimation<String, String> estimation = new Estimation<>(value -> value);
		estimation.adjust("foo");
		estimation.adjust("foo");
		Assert.assertEquals(0.0d, estimation.calculate("bar"), 0);
	}

	@Test
	public void testEmpty() {
		final Estimation<String, String> estimation = new Estimation<>(value -> value);
		Assert.assertEquals(0.0d, estimation.calculate("foo"), 0);
	}

	@Test
	public void testTransform() {
		final Estimation<String, Integer> estimation = new Estimation<>(String::length);
		estimation.adjust("fooo");
		estimation.adjust("fooo");
		estimation.adjust("foo");
		estimation.adjust("bar");
		Assert.assertEquals(0.0d, estimation.calculate("fo"),   0);
		Assert.assertEquals(0.5d, estimation.calculate("foo"),  0);
		Assert.assertEquals(0.5d, estimation.calculate("fooo"), 0);
		Assert.assertEquals(0.5d, estimation.calculate("bar"),  0);
	}
}