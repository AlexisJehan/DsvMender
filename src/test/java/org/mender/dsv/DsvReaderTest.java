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

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.mender.MenderException;

public class DsvReaderTest {

	@Test
	public void testSimple() {
		final DsvMender mender = DsvMender.auto(",", 3);
		final String dsv = "1,2,3\n"
				+ "aaa,bbb,ccc\n"
				+ "aaa,bbb,ccc\n"
				+ "aa,,bbb,ccc";
		try (final DsvReader reader = new DsvReader(mender, dsv)) {
			Assert.assertArrayEquals(new String[] {  "1",   "2",   "3"}, reader.readHeader());
			Assert.assertArrayEquals(new String[] {"aaa", "bbb", "ccc"}, reader.readRow());
			Assert.assertArrayEquals(new String[] {"aaa", "bbb", "ccc"}, reader.readRow());
			Assert.assertArrayEquals(new String[] {"aa,", "bbb", "ccc"}, reader.readRow());
			Assert.assertArrayEquals(null, reader.readRow());
		} catch (final IOException | MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testReadHeaderLate() {
		final DsvMender mender = DsvMender.auto(",", 3);
		final String dsv = "1,2,3\n"
				+ "aaa,bbb,ccc\n"
				+ "aaa,bbb,ccc\n"
				+ "aa,,bbb,ccc";
		try (final DsvReader reader = new DsvReader(mender, dsv)) {
			reader.readRow();
			reader.readHeader();
		} catch (final IOException | MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testNullDsvMender() {
		new DsvReader(null, "");
	}

	@Test(expected = NullPointerException.class)
	public void testNullBufferedReader() {
		new DsvReader(DsvMender.auto(",", 3), (BufferedReader) null);
	}
}