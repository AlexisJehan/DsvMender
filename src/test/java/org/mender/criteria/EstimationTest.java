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
		final Estimation<String, Integer> estimation = new Estimation<>(value -> value.length());
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