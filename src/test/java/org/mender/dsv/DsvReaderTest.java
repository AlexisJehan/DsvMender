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
		try (final DsvReader reader = new DsvReader(null, "")) {
			
		} catch (final IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testNullBufferedReader() {
		final DsvMender mender = DsvMender.auto(",", 3);
		try (final DsvReader reader = new DsvReader(mender, (BufferedReader) null)) {
			
		} catch (final IOException e) {
			Assert.fail(e.getMessage());
		}
	}
}