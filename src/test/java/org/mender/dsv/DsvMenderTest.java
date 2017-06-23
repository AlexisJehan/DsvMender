package org.mender.dsv;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;
import org.mender.MenderException;

/**
 * <p>{@link DsvMender} unit tests.</p>
 */
public class DsvMenderTest {

	@Test
	public void testSimple() {
		final DsvMender mender = DsvMender.auto(",", 3);
		mender.fit("aaa,bbb,ccc");
		try {
			final String[] values = mender.fix("aaa,bbb,c,c");
			Assert.assertEquals("aaa", values[0]);
			Assert.assertEquals("bbb", values[1]);
			Assert.assertEquals("c,c", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testNullEvaluator() {
		new DsvMender(null, ",", 2, DsvMender.DEFAULT_MAX_DEPTH);
	}

	@Test(expected = NullPointerException.class)
	public void testNullDelimiter() {
		new DsvMender(new DsvEvaluator(), null, 2, DsvMender.DEFAULT_MAX_DEPTH);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidNbColumns() {
		new DsvMender(new DsvEvaluator(), ",", 1, DsvMender.DEFAULT_MAX_DEPTH);
	}

	@Test(expected = NullPointerException.class)
	public void testFitRowNull() {
		final DsvMender mender = new DsvMender(new DsvEvaluator(), ",", 2, DsvMender.DEFAULT_MAX_DEPTH);
		mender.fit((String) null);
	}

	@Test(expected = NullPointerException.class)
	public void testFitValuesNull() {
		final DsvMender mender = new DsvMender(new DsvEvaluator(), ",", 2, DsvMender.DEFAULT_MAX_DEPTH);
		mender.fit((String[]) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFitValuesInvalidNbColumns() {
		final DsvMender mender = new DsvMender(new DsvEvaluator(), ",", 2, DsvMender.DEFAULT_MAX_DEPTH);
		mender.fit(new String[] {"1", "2", "3"});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFitValuesInvalidConstraint() {
		final DsvMender mender = DsvMender.builder(",", 2)
				.withNonEmptyConstraints()
				.build();
		mender.fit(new String[] {"1", ""});
	}

	@Test(expected = NullPointerException.class)
	public void testOptimizedFixRowNull() {
		final DsvMender mender = new DsvMender(new DsvEvaluator(), ",", 2, DsvMender.DEFAULT_MAX_DEPTH);
		try {
			mender.optimizedFix((String) null, 1);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testOptimizedFixValuesNull() {
		final DsvMender mender = new DsvMender(new DsvEvaluator(), ",", 2, DsvMender.DEFAULT_MAX_DEPTH);
		try {
			mender.optimizedFix((String[]) null, 1);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testFixRowNull() {
		final DsvMender mender = new DsvMender(new DsvEvaluator(), ",", 2, DsvMender.DEFAULT_MAX_DEPTH);
		try {
			mender.fix((String) null);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testFixValuesNull() {
		final DsvMender mender = new DsvMender(new DsvEvaluator(), ",", 2, DsvMender.DEFAULT_MAX_DEPTH);
		try {
			mender.fix((String[]) null);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFitInvalidColumns() {
		final DsvMender mender = DsvMender.auto(",", 3);
		mender.fit("aaa,bbb,ccc,ddd");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFixValidColumns() {
		final DsvMender mender = DsvMender.auto(",", 4);
		try {
			mender.fix("aaa,bbb,ccc,ddd");
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testHashCode() {
		final Function<String[], Integer> standard = array -> Arrays.hashCode(array);
		final Function<String[], Integer> custom  = array -> {
			try {
				final Method method = DsvMender.class.getDeclaredClasses()[0].getDeclaredMethod("customHashCode", new Class[] {String[].class});
				method.setAccessible(true);
				return (int) method.invoke(null, new Object[] {array});
			} catch (final NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				Assert.fail(e.getMessage());
			}
			return null;
		};
		
		Assert.assertEquals(standard.apply(new String[] {"00", "00"}), standard.apply(new String[] {"00", "00"}));
		Assert.assertEquals(  custom.apply(new String[] {"00", "00"})  , custom.apply(new String[] {"00", "00"}));

		Assert.assertEquals(   standard.apply(new String[] {"0", "000"}), standard.apply(new String[] {"00", "00"})); // Collision
		Assert.assertNotEquals(  custom.apply(new String[] {"0", "000"}),   custom.apply(new String[] {"00", "00"}));
	}
}