package org.mender.dsv;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;
import org.mender.MenderException;

/**
 * <p>{@link DsvBuilder} unit tests.</p>
 */
public class DsvBuilderTest {

	@Test
	public void testEmptyEstimations() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withEmptyEstimations()
				.build();
		mender.fit("a,b,c");
		mender.fit("a,,c");
		mender.fit("a,,c");
		try {
			final String[] values = mender.fix("a,,,");
			Assert.assertEquals("a", values[0]);
			Assert.assertEquals( "", values[1]); // Has been empty two times
			Assert.assertEquals(",", values[2]); // Has not been empty
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testEmptyEstimation() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withEmptyEstimation(0)
				.withEmptyEstimation(2)
				.build();
		mender.fit(",,");
		mender.fit(",,");
		try {
			final String[] values = mender.fix(",,,,,,");
			Assert.assertTrue(values[0].isEmpty()); // Has been empty all times
			Assert.assertFalse(values[1].isEmpty());
			Assert.assertTrue(values[2].isEmpty()); // Has been empty all times
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testEmptyConstraint() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withEmptyConstraint(0)
				.build();
		try {
			final String[] values = mender.fix(",a,b,c");
			Assert.assertTrue(values[0].isEmpty()); // Must be empty
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = MenderException.class)
	public void testEmptyConstraintNoSolution() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withEmptyConstraint(0)
				.build();
		mender.fix("a,b,c,"); // No solution
	}

	@Test(expected = MenderException.class)
	public void testEmptyConstraintConflict() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withEmptyConstraint(0)
				.withNonEmptyConstraint(0)
				.build();
		mender.fix("a,b,c,"); // Conflict
	}

	@Test
	public void testNonEmptyConstraints() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.build();
		try {
			final String[] values = mender.fix(",a,,,c,");
			Assert.assertFalse(values[0].isEmpty());
			Assert.assertFalse(values[1].isEmpty());
			Assert.assertFalse(values[2].isEmpty());
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = MenderException.class)
	public void testNonEmptyConstraintsNoSolution() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.build();
		mender.fix(",,,"); // No solution
	}

	@Test
	public void testNonEmptyConstraint() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraint(2)
				.build();
		try {
			final String[] values = mender.fix("a,,,c");
			Assert.assertFalse(values[2].isEmpty()); // Must not be empty
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = MenderException.class)
	public void testNonEmptyConstraintNoSolution() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraint(1)
				.withNonEmptyConstraint(2)
				.build();
		mender.fix("a,,,"); // No solution
	}

	@Test
	public void testLengthEstimations() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withLengthEstimations()
				.build();
		mender.fit("a,bb,ccc");
		mender.fit("a,bb,ccc");
		mender.fit("a,b,ccc");
		try {
			final String[] values = mender.fix("a,ccc");
			Assert.assertEquals(  "a", values[0]); // Always had a length of 1
			Assert.assertEquals(   "", values[1]);
			Assert.assertEquals("ccc", values[2]); // Always had a length of 3
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testLengthEstimation() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withLengthEstimation(1)
				.build();
		mender.fit("aaa,bb,c");
		mender.fit("aa,bb,cc");
		mender.fit("a,bb,ccc");
		try {
			final String[] values = mender.fix("bb");
			Assert.assertEquals(  "", values[0]);
			Assert.assertEquals("bb", values[1]); // Always had a length of 2
			Assert.assertEquals(  "", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testLengthConstraint() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withLengthConstraint(1, 2)
				.build();
		try {
			final String[] values = mender.fix("aa,,bb,cc");
			Assert.assertEquals("aa,", values[0]);
			Assert.assertEquals( "bb", values[1]); // Must have a length of 2
			Assert.assertEquals( "cc", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLengthConstraintInvalid() throws MenderException {
		DsvMender.builder(",", 3)
				.withLengthConstraint(1, -1)
				.build();
	}

	@Test(expected = MenderException.class)
	public void testLengthConstraintNoSolution() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withLengthConstraint(1, 2)
				.build();
		mender.fix(",a,bbb,cc"); // No solution
	}

	@Test(expected = MenderException.class)
	public void testLengthConstraintConflict() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withLengthConstraint(1, 2)
				.withLengthConstraint(1, 3)
				.build();
		mender.fix("a,bb,,cc"); // Conflict
	}

	@Test
	public void testMinLengthEstimations() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withMinLengthEstimations(2)
				.build();
		mender.fit("a,bb,ccc");
		mender.fit("a,bb,ccc");
		mender.fit("a,b,ccc");
		try {
			final String[] values = mender.fix("a,,b,,c");
			Assert.assertEquals( "a", values[0]); // Always had a length less than 2
			Assert.assertEquals(",b", values[1]);
			Assert.assertEquals(",c", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMinLengthEstimationsInvalid() {
		DsvMender.builder(",", 3)
				.withMinLengthEstimations(0)
				.build();
	}
	
	@Test
	public void testMinLengthEstimation() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withMinLengthEstimation(1, 2)
				.build();
		mender.fit("a,bb,ccc");
		mender.fit("a,bbb,ccc");
		mender.fit("aa,b,ccc");
		try {
			final String[] values = mender.fix("a,b,,ccc");
			Assert.assertEquals(  "a", values[0]);
			Assert.assertEquals( "b,", values[1]); // Had a length greater than or equal to 2 most of times
			Assert.assertEquals("ccc", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMinLengthEstimationInvalid() {
		DsvMender.builder(",", 3)
				.withMinLengthEstimation(1, 0)
				.build();
	}

	@Test
	public void testMinLengthConstraint() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withMinLengthConstraint(1, 2)
				.build();
		try {
			final String[] values = mender.fix(",,b,cc");
			Assert.assertEquals(  "", values[0]);
			Assert.assertEquals(",b", values[1]); // Must have a length greater than or equal to 2
			Assert.assertEquals("cc", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMinLengthConstraintInvalid() {
		DsvMender.builder(",", 3)
				.withMinLengthConstraint(1, 0)
				.build();
	}

	@Test(expected = MenderException.class)
	public void testMinLengthConstraintNoSolution() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withLengthConstraint(1, 2)
				.build();
		mender.fix(",a,bbb,cc"); // No solution
	}

	@Test
	public void testMaxLengthEstimations() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withMaxLengthEstimations(3)
				.build();
		mender.fit("aaa,bb,c");
		mender.fit("aaa,bb,c");
		mender.fit("aaa,b,cc");
		try {
			final String[] values = mender.fix("aaa,,,b,ccc");
			Assert.assertEquals("aaa", values[0]); // Could not have a length greater than 3
			Assert.assertEquals(",,b", values[1]);
			Assert.assertEquals("ccc", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMaxLengthEstimationsInvalid() {
		DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withMaxLengthEstimations(0)
				.build();
	}

	@Test
	public void testMaxLengthEstimation() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withMaxLengthEstimation(1, 2)
				.build();
		mender.fit("aaa,bb,c");
		mender.fit("aaa,bb,c");
		mender.fit("aaa,b,cc");
		try {
			final String[] values = mender.fix("aaa,,bb,,ccc");
			Assert.assertEquals("aaa,", values[0]);
			Assert.assertEquals(  "bb", values[1]); // Could not have a length greater than 2
			Assert.assertEquals(",ccc", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMaxLengthEstimationInvalid() {
		DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withMaxLengthEstimation(1, 0)
				.build();
	}

	@Test
	public void testMaxLengthConstraint() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withMaxLengthConstraint(1, 2)
				.build();
		try {
			final String[] values = mender.fix("aa,,bb,,cc");
			Assert.assertEquals("aa,", values[0]);
			Assert.assertEquals( "bb", values[1]); // Must not have a length greater than 2
			Assert.assertEquals(",cc", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMaxLengthConstraintInvalid() {
		DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withMaxLengthConstraint(1, 0)
				.build();
	}
	
	@Test(expected = MenderException.class)
	public void testMaxLengthConstraintNoSolution() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withMaxLengthConstraint(2, 2)
				.build();
		mender.fix(",a,b,cccccc"); // No solution
	}
	
	@Test(expected = MenderException.class)
	public void testMaxLengthConstraintConflict() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withMinLengthConstraint(2, 3)
				.withMaxLengthConstraint(2, 2)
				.build();
		mender.fix(",a,b,cc"); // Conflict
	}

	@Test
	public void testRangeLengthEstimations() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withRangeLengthEstimations(2, 3)
				.build();
		mender.fit("aaa,b,ccc");
		mender.fit("aaa,,cc");
		mender.fit("aaa,b,cc");
		try {
			final String[] values = mender.fix("aaa,,,b,,c,c");
			Assert.assertEquals( "aaa", values[0]); // Could not have a length greater than 3
			Assert.assertEquals(",,b,", values[1]);
			Assert.assertEquals( "c,c", values[2]); // Could not have a length greater than 3
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRangeLengthEstimationsInvalidMinLength() {
		DsvMender.builder(",", 3)
				.withRangeLengthEstimations(0, 1)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRangeLengthEstimationsInvalidMaxLength() {
		DsvMender.builder(",", 3)
				.withRangeLengthEstimations(2, 1)
				.build();
	}

	@Test
	public void testRangeLengthEstimation() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withRangeLengthEstimation(1, 2, 3)
				.build();
		mender.fit("aaa,bb,c");
		mender.fit("aaa,bb,c");
		mender.fit("aaa,bbb,cc");
		try {
			final String[] values = mender.fix("aaa,,bbb,,ccc");
			Assert.assertEquals("aaa,", values[0]);
			Assert.assertEquals( "bbb", values[1]); // Could not have a length greater than 3
			Assert.assertEquals(",ccc", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRangeLengthEstimationInvalidMinLength() {
		DsvMender.builder(",", 3)
				.withRangeLengthEstimation(1, 0, 1)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRangeLengthEstimationInvalidMaxLength() {
		DsvMender.builder(",", 3)
				.withRangeLengthEstimation(1, 2, 1)
				.build();
	}

	@Test
	public void testRangeLengthConstraint() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withRangeLengthConstraint(1, 2, 3)
				.build();
		try {
			final String[] values = mender.fix("aa,,,,cc");
			Assert.assertEquals("aa", values[0]);
			Assert.assertEquals(",,", values[1]); // Must not have a length lower than 2
			Assert.assertEquals("cc", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRangeLengthConstraintInvalidMinLength() {
		DsvMender.builder(",", 3)
				.withRangeLengthConstraint(1, 0, 1)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRangeLengthConstraintInvalidMaxLength() {
		DsvMender.builder(",", 3)
				.withRangeLengthConstraint(1, 2, 1)
				.build();
	}
	
	@Test(expected = MenderException.class)
	public void testRangeLengthConstraintNoSolution() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withRangeLengthConstraint(2, 5, 10)
				.build();
		mender.fix(",a,b,ccc"); // No solution
	}
	
	@Test(expected = MenderException.class)
	public void testRangeLengthConstraintConflict() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withRangeLengthConstraint(2, 1, 2)
				.withRangeLengthConstraint(2, 2, 3)
				.build();
		mender.fix(",a,b,ccc"); // Conflict
	}

	@Test
	public void testPatternEstimations() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withPatternEstimations(Pattern.compile("[a-z]+"))
				.build();
		mender.fit("aaa,bbb,ccc");
		mender.fit("aaa,bb,");
		mender.fit("aaa,b,ccc");
		try {
			final String[] values = mender.fix("aaa,bb,,cc");
			Assert.assertEquals("aaa", values[0]);
			Assert.assertEquals( "bb", values[1]);
			Assert.assertEquals(",cc", values[2]); // Pattern matched only two times
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testPatternEstimationsNull() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withPatternEstimations(null)
				.build();
		mender.fit("aaa,bbb,ccc");
	}

	@Test
	public void testPatternEstimation() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withPatternEstimation(2, Pattern.compile("c+"))
				.build();
		mender.fit("aaa,bbb,ccc");
		mender.fit("aaa,b,cc");
		mender.fit("aaa,bbb,c");
		try {
			final String[] values = mender.fix("aaa,bb,,ccc");
			Assert.assertEquals("aaa", values[0]);
			Assert.assertEquals("bb,", values[1]);
			Assert.assertEquals("ccc", values[2]); // Had always matched
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testPatternEstimationNull() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withPatternEstimation(2, null)
				.build();
		mender.fit("aaa,bbb,ccc");
	}

	@Test
	public void testPatternConstraint() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withPatternConstraint(0, Pattern.compile("a+"))
				.withPatternConstraint(2, Pattern.compile("c+"))
				.build();
		try {
			final String[] values = mender.fix("aa,,,,,cc");
			Assert.assertEquals( "aa", values[0]); // Must only contains 'a' chars
			Assert.assertEquals(",,,", values[1]);
			Assert.assertEquals( "cc", values[2]); // Must only contains 'c' chars
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testPatternConstraintNull() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withPatternConstraint(2, null)
				.build();
		try {
			mender.fix("a,b,c,");
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(expected = MenderException.class)
	public void testPatternConstraintNoSolution() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withPatternConstraint(2, Pattern.compile("c+"))
				.build();
		mender.fix(",a,b,cc,"); // No solution
	}
	
	@Test(expected = MenderException.class)
	public void testPatternConstraintConflict() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withPatternConstraint(2, Pattern.compile("[^c]+"))
				.withPatternConstraint(2, Pattern.compile("c+"))
				.build();
		mender.fix(",a,b,cc"); // Conflict
	}

	@Test
	public void testContainsEstimations() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withContainsEstimations("bb")
				.build();
		mender.fit("aa,bbb,ccc");
		mender.fit("aa,bb,c");
		mender.fit("aa,bb,ccc");
		try {
			final String[] values = mender.fix(",aa,bb,cc,");
			Assert.assertEquals(",aa", values[0]);
			Assert.assertEquals( "bb", values[1]); // Always contained "bb" substring
			Assert.assertEquals("cc,", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testContainsEstimationsNull() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withContainsEstimations(null)
				.build();
		mender.fit("aa,bbb,ccc");
	}

	@Test
	public void testContainsEstimation() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withContainsEstimation(0, "aa")
				.build();
		mender.fit("aaa,bbb,ccc");
		mender.fit("aaa,b,cc");
		mender.fit("aaa,bbb,c");
		try {
			final String[] values = mender.fix("a,aa,bb,ccc");
			Assert.assertEquals( "a,aa", values[0]); // Had always matched
			Assert.assertEquals(   "bb", values[1]);
			Assert.assertEquals(  "ccc", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testContainsEstimationNull() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withContainsEstimation(0, null)
				.build();
		mender.fit("aaa,bbb,ccc");
	}

	@Test
	public void testContainsConstraint() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withContainsConstraint(0, "aa")
				.withContainsConstraint(2, "cc")
				.build();
		try {
			final String[] values = mender.fix(",aa,,cc,");
			Assert.assertEquals(",aa", values[0]); // Must contains "aa" substring
			Assert.assertEquals(   "", values[1]);
			Assert.assertEquals("cc,", values[2]); // Must contains "cc" substring
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testContainsConstraintNull() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withContainsConstraint(0, null)
				.build();
		try {
			mender.fix(",aa,,cc,");
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(expected = MenderException.class)
	public void testContainsConstraintNoSolution() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withContainsConstraint(1, "z")
				.build();
		mender.fix(",a,b,cc,"); // No solution
	}
	
	@Test(expected = MenderException.class)
	public void testContainsConstraintConflict() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withContainsConstraint(1, "z")
				.withContainsNoneConstraint(1, "z")
				.build();
		mender.fix(",a,b,cc"); // Conflict
	}

	@Test
	public void testContainsNoneConstraint() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withContainsNoneConstraint(0, ",")
				.withContainsNoneConstraint(2, ",")
				.build();
		try {
			final String[] values = mender.fix("aa,,,,,cc");
			Assert.assertEquals( "aa", values[0]); // Must contains "," substring
			Assert.assertEquals(",,,", values[1]);
			Assert.assertEquals( "cc", values[2]); // Must contains "," substring
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testContainsNoneConstraintNull() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withContainsNoneConstraint(0, null)
				.build();
		try {
			mender.fix("aa,,,,,cc");
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testStartsWithEstimations() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withStartsWithEstimations(",")
				.build();
		mender.fit("aaa,bb,c");
		mender.fit("aaa,bb,cc");
		mender.fit("aaa,bb,ccc");
		try {
			final String[] values = mender.fix("aaa,bb,,ccc");
			Assert.assertEquals("aaa", values[0]);
			Assert.assertEquals("bb,", values[1]);
			Assert.assertEquals("ccc", values[2]); // Had never started with ','
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testStartsWithEstimationsNull() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withStartsWithEstimations(null)
				.build();
		mender.fit("aaa,bbb,ccc");
	}

	@Test
	public void testStartsWithEstimation() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withStartsWithEstimation(1, "b")
				.build();
		mender.fit("aaa,bb,c");
		mender.fit("aaa,bb,cc");
		mender.fit("aaa,bb,ccc");
		try {
			final String[] values = mender.fix("a,,,bb,ccc");
			Assert.assertEquals("a,,", values[0]);
			Assert.assertEquals( "bb", values[1]); // Had always started with 'b'
			Assert.assertEquals("ccc", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testStartsWithEstimationNull() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withStartsWithEstimation(1, null)
				.build();
		mender.fit("aaa,bbb,ccc");
	}

	@Test
	public void testStartsWithConstraint() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withStartsWithConstraint(1, "b")
				.build();
		try {
			final String[] values = mender.fix("a,,,bb,ccc");
			Assert.assertEquals("a,,", values[0]);
			Assert.assertEquals( "bb", values[1]); // Must start with 'b'
			Assert.assertEquals("ccc", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testStartsWithConstraintNull() {
		DsvMender.builder(",", 3)
				.withStartsWithConstraint(1, null)
				.build();
	}
	
	@Test(expected = MenderException.class)
	public void testStartsWithConstraintNoSolution() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withStartsWithConstraint(1, "z")
				.build();
		mender.fix(",a,b,cc,"); // No solution
	}
	
	@Test(expected = MenderException.class)
	public void testStartsWithConstraintConflict() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withStartsWithConstraint(1, "b")
				.withStartsWithConstraint(1, "c")
				.build();
		mender.fix(",a,b,cc"); // Conflict
	}
	
	@Test
	public void testEndsWithEstimations() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withEndsWithEstimations(",")
				.build();
		mender.fit("aaa,bb,c");
		mender.fit("aaa,bb,cc");
		mender.fit("aaa,bb,ccc");
		try {
			final String[] values = mender.fix("aaa,bbb,,cc");
			Assert.assertEquals("aaa", values[0]);
			Assert.assertEquals("bbb", values[1]); // Had never ended with ','
			Assert.assertEquals(",cc", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testEndsWithEstimationsNull() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withEndsWithEstimations(null)
				.build();
		mender.fit("aaa,bbb,ccc");
	}

	@Test
	public void testEndsWithEstimation() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withEndsWithEstimation(1, "b")
				.build();
		mender.fit("aaa,bb,c");
		mender.fit("aaa,bb,cc");
		mender.fit("aaa,bb,ccc");
		try {
			final String[] values = mender.fix("aaa,bb,,,c");
			Assert.assertEquals("aaa", values[0]);
			Assert.assertEquals( "bb", values[1]); // Had always ended with 'b'
			Assert.assertEquals(",,c", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testEndsWithEstimationNull() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withEndsWithEstimation(1, null)
				.build();
		mender.fit("aaa,bbb,ccc");
	}

	@Test
	public void testEndsWithConstraint() {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withNonEmptyConstraints()
				.withEndsWithConstraint(1, "b")
				.build();
		try {
			final String[] values = mender.fix("aaa,bb,,,c");
			Assert.assertEquals("aaa", values[0]);
			Assert.assertEquals( "bb", values[1]); // Must end with 'b'
			Assert.assertEquals(",,c", values[2]);
		} catch (final MenderException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testEndsWithConstraintNull() {
		DsvMender.builder(",", 3)
				.withEndsWithConstraint(1, null)
				.build();
	}
	
	@Test(expected = MenderException.class)
	public void testEndsWithConstraintNoSolution() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withEndsWithConstraint(1, "z")
				.build();
		mender.fix(",a,b,cc,"); // No solution
	}
	
	@Test(expected = MenderException.class)
	public void testEndsWithConstraintConflict() throws MenderException {
		final DsvMender mender = DsvMender.builder(",", 3)
				.withEndsWithConstraint(1, "b")
				.withEndsWithConstraint(1, "c")
				.build();
		mender.fix(",a,b,cc"); // Conflict
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testEstimationInvalid() {
		DsvMender.builder(",", 3)
				.withEstimation(3, String::isEmpty)
				.build();
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testConstraintInvalid() {
		DsvMender.builder(",", 3)
				.withConstraint(3, String::isEmpty)
				.build();
	}
}