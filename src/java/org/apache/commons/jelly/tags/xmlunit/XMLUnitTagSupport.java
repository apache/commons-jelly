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

import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import junit.framework.AssertionFailedError;

import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.dom4j.Document;
import org.dom4j.io.SAXContentHandler;
import org.dom4j.io.SAXReader;

public abstract class XMLUnitTagSupport extends TagSupport {

	/** The SAXReader used to parser the document */
	private SAXReader saxReader;

	/** @return the SAXReader used for parsing, creating one lazily if need be  */
	public SAXReader getSAXReader() throws Exception {
		if (saxReader == null) {
			saxReader = createSAXReader();
		}
		return saxReader;
	}

	/** Sets the SAXReader used for parsing */
	public void setSAXReader(SAXReader saxReader) {
		this.saxReader = saxReader;
	}

	/**
	 * Factory method to create a new SAXReader
	 */
	protected abstract SAXReader createSAXReader() throws Exception;

	/**
	 * Parses the body of this tag and returns the parsed document
	 */
	protected Document parseBody() throws Exception {
		SAXContentHandler handler = new SAXContentHandler();
		XMLOutput newOutput = new XMLOutput(handler);
		handler.startDocument();
		invokeBody(newOutput);
		handler.endDocument();
		return handler.getDocument();
	}

	/**
	 * Parses the given source
	 */
	protected Document parse(Object source) throws Exception {
		if (source instanceof Document) {
			return (Document) source;
		} else if (source instanceof String) {
			String uri = (String) source;
			InputStream in = context.getResourceAsStream(uri);
			return getSAXReader().read(in, uri);
		} else if (source instanceof Reader) {
			return getSAXReader().read((Reader) source);
		} else if (source instanceof InputStream) {
			return getSAXReader().read((InputStream) source);
		} else if (source instanceof URL) {
			return getSAXReader().read((URL) source);
		} else {
			throw new IllegalArgumentException(
				"Invalid source argument. Must be a Document, String, Reader, InputStream or URL."
					+ " Was type: "
					+ source.getClass().getName()
					+ " with value: "
					+ source);
		}
	}

	/**
	 * Produces a failure assertion with the given message
	 */
	protected void fail(String message) throws AssertionFailedError {
		throw new AssertionFailedError(message);
	}

}
