/*
MIT License

Copyright (c) 2018 Alexis Jehan

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
/**
 * <p>{@code Mender} implementation for malformed DSV data.</p>
 * 
 * <p>DSV <i>(Delimiter-Separated Values)</i> is a data format that consists of rows of values separated by a delimiting
 * character or a string. CSV <i>(Comma-Separated Values)</i> is a famous and commonly-used format inherited from
 * DSV.</p>
 * <p>While most of that formatted data are escaping the delimiter, that's not always the case. Basically sometimes the
 * non-escaped delimiter is contained by some values and while parsing the computer is not able to retrieve the original
 * information, or malformed lines are simply ignored. Another case is when values are missing from some lines.</p>
 * <p>{@link org.mender.dsv.DsvMender} is a tool that is able to repair malformed DSV data by learning columns features
 * from corrects rows.</p>
 * 
 * @since 1.0
 */
package org.mender.dsv;