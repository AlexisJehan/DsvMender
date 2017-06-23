package org.mender.criteria;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>{@link Constraint} unit tests.</p>
 */
public class ConstraintTest {

	@Test
	public void testSimple() {
		final Constraint<String> constraint = new Constraint<>(value -> "foo".equals(value));
		
		Assert.assertEquals(1.0d, constraint.calculate("foo"), 0);
		Assert.assertEquals(0.0d, constraint.calculate("bar"), 0);
	}

	@Test
	public void testStatic() {
		final Constraint<String> constraint = Constraint.of(value -> "foo".equals(value));
		
		Assert.assertEquals(1.0d, constraint.calculate("foo"), 0);
		Assert.assertEquals(0.0d, constraint.calculate("bar"), 0);
	}

	@Test
	public void testCheck() {
		final Constraint<String> constraint = new Constraint<>(value -> "foo".equals(value));
		
		Assert.assertTrue(constraint.check("foo"));
		Assert.assertFalse(constraint.check("bar"));
	}

	@Test(expected = NullPointerException.class)
	public void testNull() {
		new Constraint<>(null);
	}
}