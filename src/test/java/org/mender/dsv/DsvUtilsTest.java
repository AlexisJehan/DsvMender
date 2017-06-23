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