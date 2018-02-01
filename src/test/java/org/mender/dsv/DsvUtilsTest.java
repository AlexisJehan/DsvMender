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
package org.mender.dsv;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>{@link DsvUtils} unit tests.</p>
 */
public class DsvUtilsTest {

	@Test
	public void testSimple() {
		Assert.assertArrayEquals(
				new String[] {"a", ":", "b", ":", "c"},
				DsvUtils.optimize(new String[] {"a", "", "", "b", ":", "c"}, ":", 5, 0)
		);
	}

	@Test
	public void testNbSafeColumns() {
		Assert.assertArrayEquals(
				new String[] {"a", "", ":", "", "b", "", ":", "", "c"},
				DsvUtils.optimize(new String[] {"a", "", "", "", "", "b", "", "", "", "", "c"}, ":", 5, 1)
		);
	}

	@Test
	public void testTooLargeNbSafeColumns() {
		Assert.assertArrayEquals(
				new String[] {"a", "", "", "b", "", "", "c"},
				DsvUtils.optimize(new String[] {"a", "", "", "b", "", "", "c"}, ":", 5, 1)
		);
	}

	@Test
	public void testLargeConsecutive() {
		Assert.assertArrayEquals(
				new String[] {"a", "", "", ":::::::::::::::", "", "", "b", "c"},
				DsvUtils.optimize(new String[] {"a", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "b", "c"}, ":", 5, 2)
		);
	}

	@Test
	public void testFirstColumn() {
		Assert.assertArrayEquals(
				new String[] {"::", "a", "b", ":", "c"},
				DsvUtils.optimize(new String[] {"", "", "", "a", "b", "", "", "c"}, ":", 5, 0)
		);
	}

	@Test
	public void testLastColumn() {
		Assert.assertArrayEquals(
				new String[] {"a", ":", "b", "c", "::"},
				DsvUtils.optimize(new String[] {"a", "", "", "b", "c", "", "", ""}, ":", 5, 0)
		);
	}
}