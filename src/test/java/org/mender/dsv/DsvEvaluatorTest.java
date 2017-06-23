package org.mender.dsv;

import org.junit.Assert;
import org.junit.Test;
import org.mender.criteria.Constraint;
import org.mender.criteria.Estimation;

/**
 * <p>{@link DsvEvaluator} unit tests.</p>
 */
public class DsvEvaluatorTest {

	@Test
	public void testConstraint() {
		final DsvEvaluator evaluator = new DsvEvaluator();
		evaluator.addConstraint(1, Constraint.of(value -> "+".equals(value)));
		
		Assert.assertEquals(0.0d, evaluator.evaluate(new String[] {"-", "-", "-"}), 0); // Second value is not "+"
		Assert.assertEquals(1.0d, evaluator.evaluate(new String[] {"-", "+", "-"}), 0); // Ok
	}

	@Test
	public void testConstraints() {
		final DsvEvaluator evaluator = new DsvEvaluator();
		evaluator.addConstraint(0, Constraint.of(value ->   "-".equals(value)));
		evaluator.addConstraint(1, Constraint.of(value ->   "+".equals(value)));
		evaluator.addConstraint(2, Constraint.of(value -> 2 == value.length()));

		Assert.assertEquals(0.0d, evaluator.evaluate(new String[] {"+", "-", "-" }), 0); // First value is not "-"
		Assert.assertEquals(0.0d, evaluator.evaluate(new String[] {"-", "-", "-" }), 0); // Second value is not "+"
		Assert.assertEquals(0.0d, evaluator.evaluate(new String[] {"-", "+", "-" }), 0); // Third value is not two chars length
		Assert.assertEquals(1.0d, evaluator.evaluate(new String[] {"-", "+", "--"}), 0); // Ok
	}

	@Test
	public void testEstimation() {
		final DsvEvaluator evaluator = new DsvEvaluator();
		evaluator.addEstimation(1, Estimation.of(value -> "+".equals(value)));
		
		// Not adjusted yet
		Assert.assertEquals(0.0d, evaluator.evaluate(new String[] {"-", "-", "-"}), 0);
		Assert.assertEquals(0.0d, evaluator.evaluate(new String[] {"-", "+", "-"}), 0);
		
		evaluator.adjustEstimations(new String[] {"-", "+", "-"});
		
		// Adjusted with "+" once
		Assert.assertEquals(0.0d, evaluator.evaluate(new String[] {"-", "-", "-"}), 0);
		Assert.assertEquals(1.0d, evaluator.evaluate(new String[] {"-", "+", "-"}), 0);
		
		evaluator.adjustEstimations(new String[] {"-", "-", "-"});
		
		// Adjusted with "+" once and "-" once
		Assert.assertEquals(0.5d, evaluator.evaluate(new String[] {"-", "-", "-"}), 0);
		Assert.assertEquals(0.5d, evaluator.evaluate(new String[] {"-", "+", "-"}), 0);
	}

	@Test
	public void testEstimations() {
		final DsvEvaluator evaluator = new DsvEvaluator();
		evaluator.addEstimation(0, Estimation.of(value ->   "-".equals(value)));
		evaluator.addEstimation(1, Estimation.of(value ->   "+".equals(value)));
		evaluator.addEstimation(2, Estimation.of(value -> 2 == value.length()));
		
		// Not adjusted yet
		Assert.assertEquals(0.0d, evaluator.evaluate(new String[] {"-", "+", "--"}), 0);
		
		evaluator.adjustEstimations(new String[] {"-", "+", "--"});

		Assert.assertEquals(0.0d, evaluator.evaluate(new String[] {"+", "-", "-" }), 0); // First value is not "-", second value is not "+" and third value is not two chars length
		Assert.assertEquals(1.0d, evaluator.evaluate(new String[] {"-", "-", "-" }), 0); // Second value is not "+" and third value is not two chars length
		Assert.assertEquals(2.0d, evaluator.evaluate(new String[] {"-", "+", "-" }), 0); // Third value is not two chars length
		Assert.assertEquals(3.0d, evaluator.evaluate(new String[] {"-", "+", "--"}), 0); // Ok
		
		evaluator.adjustEstimations(new String[] {"+", "-", "-"});

		// Conflicts in adjustments
		Assert.assertEquals(1.5d, evaluator.evaluate(new String[] {"+", "-", "-" }), 0);
		Assert.assertEquals(1.5d, evaluator.evaluate(new String[] {"-", "-", "-" }), 0);
		Assert.assertEquals(1.5d, evaluator.evaluate(new String[] {"-", "+", "-" }), 0);
		Assert.assertEquals(1.5d, evaluator.evaluate(new String[] {"-", "+", "--"}), 0);
	}

	@Test(expected = NullPointerException.class)
	public void testConstraintNull() {
		final DsvEvaluator evaluator = new DsvEvaluator();
		evaluator.addConstraint(0, null);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testConstraintNegativeIndex() {
		final DsvEvaluator evaluator = new DsvEvaluator();
		evaluator.addConstraint(-1, Constraint.of(value -> "-".equals(value)));
	}

	@Test(expected = NullPointerException.class)
	public void testEstimationNull() {
		final DsvEvaluator evaluator = new DsvEvaluator();
		evaluator.addEstimation(0, null);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testEstimationNegativeIndex() {
		final DsvEvaluator evaluator = new DsvEvaluator();
		evaluator.addEstimation(-1, Estimation.of(value -> "-".equals(value)));
	}

	@Test(expected = NullPointerException.class)
	public void testAdjustNull() {
		final DsvEvaluator evaluator = new DsvEvaluator();
		evaluator.adjustEstimations(null);
	}

	@Test(expected = NullPointerException.class)
	public void testEvaluateNull() {
		final DsvEvaluator evaluator = new DsvEvaluator();
		evaluator.evaluate(null);
	}

	@Test
	public void testEvaluateEmpty() {
		final DsvEvaluator evaluator = new DsvEvaluator();
		Assert.assertEquals(1.0d, evaluator.evaluate(new String[] {"1", "2", "3"}), 0);
	}
}