/*
 * MIT License
 *
 * Copyright (c) 2017-2024 Alexis Jehan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.alexisjehan.mender.api;

import com.github.alexisjehan.javanilla.misc.quality.Ensure;

/**
 * <p>An unchecked {@link Exception} that can occur with {@link Mender#mend(Object)}.</p>
 * <p><b>Note</b>: This class is {@link java.io.Serializable}.</p>
 * @since 1.0.0
 */
public final class MendException extends RuntimeException {

	/**
	 * <p>Serial version unique ID.</p>
	 * @since 1.0.0
	 */
	private static final long serialVersionUID = -2548765841738762668L;

	/**
	 * <p>Constructor with a message.</p>
	 * @param message the message
	 * @throws NullPointerException if the message is {@code null}
	 * @since 1.0.0
	 */
	public MendException(final String message) {
		super(Ensure.notNull("message", message));
	}
}