/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */

package org.apache.commons.jelly.tags.xmlunit;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

/**
 * Compares two XML documents using XMLUnit (http://xmlunit.sourceforge.net/).
 * If they are different an exception will be thrown.
 */
public class AssertDocumentsEqualTag extends XMLUnitTagSupport {

	private Object actual;
	private Document actualDocument;

	private Object expected;
	private Document expectedDocument;

	/**
	 * Controls whether whitespace differences are reported as differences.
	 * 
	 * Defaults to <code>false</code>, so if <code>trim</code> is set to
	 * <code>false</code> whitespace differences are detected.
	 */
	private boolean ignoreWhitespace = false;

	public void doTag(XMLOutput output) throws JellyTagException {
		invokeBody(output);

		if (actual != null) {
			if (actualDocument != null) {
				fail("Cannot specify both actual attribute and element");
			}
			actualDocument = parse(actual);
		}

		if (expected != null) {
			if (expectedDocument != null) {
				fail("Cannot specify both expected attribute and element");
			}
			expectedDocument = parse(expected);
		}

		if ((expectedDocument == null
			|| expectedDocument.getRootElement() == null)
			&& (actualDocument == null
				|| actualDocument.getRootElement() == null)) {
			return;
		}

		if (actualDocument != null) {
			XMLUnit.setIgnoreWhitespace(ignoreWhitespace);
            
			Diff delta = null;
            try {
				delta = XMLUnit.compare(
					expectedDocument.asXML(),
					actualDocument.asXML());
            }
            catch (SAXException e) {
                throw new JellyTagException(e);
            }
            catch (IOException e) {
                throw new JellyTagException(e);
            }
            catch (ParserConfigurationException e) {
                throw new JellyTagException(e);
            }
            
			if (delta.identical()) {
				return;
			}
			fail(delta.toString());
		}
	}

	/**
	 * Sets the actual XML document which is either a Document, String (of an
	 * URI), URI, Reader, or InputStream.
	 */
	public void setActual(Object actual) {
		this.actual = actual;
	}

	/**
	 * Sets the expected XML document which is either a Document, String (of an
	 * URI), URI, Reader, or InputStream.
	 */
	public void setExpected(Object expected) {
		this.expected = expected;
	}

	/**
	 * Controls whether whitespace differences should be interpreted as
	 * differences or not.  The default is <code>false</code>.  Note that the
	 * use of the <code>trim</code> attribute is crucial here.
	 */
	public void setIgnoreWhitespace(boolean ignoreWhitespace) {
		this.ignoreWhitespace = ignoreWhitespace;
	}

	protected SAXReader createSAXReader() {
		return new SAXReader();
	}

}
