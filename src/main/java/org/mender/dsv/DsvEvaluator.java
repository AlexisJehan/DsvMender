/*
MIT License

Copyright (c) 2017 Alexis Jehan

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

import java.util.HashMap;
import java.util.Map;

import org.mender.Evaluator;
import org.mender.criteria.Constraint;
import org.mender.criteria.Estimation;

/**
 * <p>A {@link Evaluator} implementation for the DSV format.</p>
 *
 * <p><b>Score computation</b>: (1 * c[1] * c2] * c[3]...) * (e[1] + e[2] + e[3]...)<br>
 * <i>(with c[n] the n-th constraint score and e[n] the n-th estimation score)</i></p>
 * 
 * @since 1.0
 */
class DsvEvaluator implements Evaluator<String[]> {
	
	/**
	 * <p>Map of constraints associated to column indexes.</p>
	 */
	private final Map<Constraint<String>, Integer> constraints = new HashMap<>();
	
	/**
	 * <p>Map of estimations associated to column indexes.</p>
	 */
	private final Map<Estimation<String, ?>, Integer> estimations = new HashMap<>();
	
	/**
	 * <p>Add a new {@code Constraint} at the specified index.</p>
	 * 
	 * @param index The related column index
	 * @param constraint The constraint to register
	 * @throws NullPointerException If the constraint is null
	 * @throws IndexOutOfBoundsException If the index is negative
	 */
	public void addConstraint(final int index, final Constraint<String> constraint) {
		if (null == constraint) {
			throw new NullPointerException("Invalid constraint (not null expected)");
		}
		if (0 > index) {
			throw new IndexOutOfBoundsException("Invalid index: " + index + " (greater than or equal to 0 expected)");
		}
		constraints.put(constraint, index);
	}
	
	/**
	 * <p>Add a new {@code Estimation} at the specified index.</p>
	 * 
	 * @param index The related column index
	 * @param estimation The estimation to register
	 * @throws NullPointerException If the estimation is null
	 * @throws IndexOutOfBoundsException If the index is negative
	 */
	public void addEstimation(final int index, final Estimation<String, ?> estimation) {
		if (null == estimation) {
			throw new NullPointerException("Invalid estimation (not null expected)");
		}
		if (0 > index) {
			throw new IndexOutOfBoundsException("Invalid index: " + index + " (greater than or equal to 0 expected)");
		}
		estimations.put(estimation, index);
	}

	/**
	 * <p>Check constraints using given values.</p>
	 * 
	 * @param values Values to use to check
	 * @return {@code true} if values pass all constraints
	 * @throws NullPointerException If values are null
	 */
	@Override
	public boolean checkConstraints(final String[] values) {
		if (null == values) {
			throw new NullPointerException("Invalid values (not null expected)");
		}
		for (final Map.Entry<Constraint<String>, Integer> constraint : constraints.entrySet()) {
			if (!constraint.getKey().check(values[constraint.getValue()])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>Adjust estimations using given values.</p>
	 * 
	 * @param values Values to use to adjust
	 * @throws NullPointerException If values are null
	 */
	@Override
	public void adjustEstimations(final String[] values) {
		if (null == values) {
			throw new NullPointerException("Invalid values (not null expected)");
		}
		for (final Map.Entry<Estimation<String, ?>, Integer> estimation : estimations.entrySet()) {
			estimation.getKey().adjust(values[estimation.getValue()]);
		}
	}

	/**
	 * <p>Evaluate given values to get a computed score.</p>
	 * 
	 * @param values Values to evaluate
	 * @throws NullPointerException If values are null
	 */
	@Override
	public double evaluate(final String[] values) {
		if (null == values) {
			throw new NullPointerException("Invalid values (not null expected)");
		}
		return
				constraints.entrySet()
						.stream()
						.mapToDouble(constraint -> constraint.getKey().calculate(values[constraint.getValue()]))
						.reduce(1, (a, b) -> a * b) * 
				(
						estimations.isEmpty() ? 1.0d : estimations.entrySet()
						.stream()
						.mapToDouble(estimation -> estimation.getKey().calculate(values[estimation.getValue()]))
						.sum()
				);
	}
}