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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.mender.MenderException;

/**
 * <p>A reader for DSV data that fit itself progressively while forwarding in the stream using the given
 * {@code DsvMender}. Invalids rows are automatically fixed using at-the-time knowledges.</p>
 * 
 * <p><b>Node</b>: For better results you should instead use a two-pass process that first fit valid rows and then fix
 * invalids ones.</p>
 * 
 * @since 1.0
 */
public class DsvReader implements Closeable {
	
	/**
	 * <p>Configured {@code DsvMender} to use.</p>
	 */
	private final DsvMender mender;
	
	/**
	 * <p>Delegated {@code BufferedReader} to read lines from the input.</p>
	 */
	private final BufferedReader bufferedReader;
	
	/**
	 * <p>Boolean that indicate if a line has already been read.</p>
	 */
	private boolean hasStarted = false;
	
	/**
	 * <p>Constructor using a file {@code Path}.</p>
	 * 
	 * @param mender The {@code DSVMender} to use
	 * @param file The input file {@code Path}
	 * @throws IOException Might occurs with I/O operations
	 */
	public DsvReader(final DsvMender mender, final Path file) throws IOException {
		this(mender, Files.newBufferedReader(file));
	}
	
	/**
	 * <p>Constructor using a file {@code Path} and a custom {@code Charset}.</p>
	 * 
	 * @param mender The {@code DSVMender} to use
	 * @param file The input file {@code Path}
	 * @param charset Custom {@code Charset}
	 * @throws IOException Might occurs with I/O operations
	 */
	public DsvReader(final DsvMender mender, final Path file, final Charset charset) throws IOException {
		this(mender, Files.newBufferedReader(file, charset));
	}
	
	/**
	 * <p>Constructor using a {@code String}</p>
	 * 
	 * @param mender The {@code DSVMender} to use
	 * @param string The {@code String} to use
	 */
	public DsvReader(final DsvMender mender, final String string) {
		this(mender, new StringReader(string));
	}
	
	/**
	 * <p>Constructor using a {@code Reader}</p>
	 * 
	 * @param mender The {@code DSVMender} to use
	 * @param reader The {@code Reader} to use
	 */
	public DsvReader(final DsvMender mender, final Reader reader) {
		this(mender, new BufferedReader(reader));
	}

	/**
	 * <p>Constructor using a {@code BufferedReader}</p>
	 * 
	 * @param mender The {@code DSVMender} to use
	 * @param bufferedReader The {@code BufferedReader} to use
	 * @throws NullPointerException If the {@code DsvMender} or the {@code BufferedReader} are null
	 */
	public DsvReader(final DsvMender mender, final BufferedReader bufferedReader) {
		if (null == mender) {
			throw new NullPointerException("Invalid DSV mender (not null expected)");
		}
		if (null == bufferedReader) {
			throw new NullPointerException("Invalid buffered reader (not null expected)");
		}
		this.mender = mender;
		this.bufferedReader = bufferedReader;
	}
	
	/**
	 * <p>Read the DSV header, it must be the first read line. Note that no fit operation will be performed.</p>
	 * 
	 * @return Array of DSV header values
	 * @throws MenderException If the header has invalid number of values and if the fix has failed
	 * @throws IOException Might occurs with I/O operations
	 */
	public String[] readHeader() throws MenderException, IOException {
		if (hasStarted) {
			throw new IllegalStateException("Header must be the first read line");
		}
		final String line = bufferedReader.readLine();
		if (null == line) {
			return null;
		}
		return mender.fixIfNotValid(line);
	}
	
	/**
	 * <p>Read a DSV row, performing a fit operation if it is valid, or a fix operation else.</p>
	 * 
	 * @return Array of DSV row values
	 * @throws MenderException If the row has invalid number of values and if the fix has failed
	 * @throws IOException Might occurs with I/O operations
	 */
	public String[] readRow() throws MenderException, IOException {
		hasStarted = true;
		final String line = bufferedReader.readLine();
		if (null == line) {
			return null;
		}
		mender.fitIfValid(line);
		return mender.fixIfNotValid(line);
	}

	@Override
	public void close() throws IOException {
		bufferedReader.close();
	}
}