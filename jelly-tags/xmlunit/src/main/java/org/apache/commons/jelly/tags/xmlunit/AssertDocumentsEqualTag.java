/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jelly.tags.xmlunit;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

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
     * Defaults to {@code false}, so if <code>trim</code> is set to
     * {@code false} whitespace differences are detected.
     */
    private boolean ignoreWhitespace = false;

    @Override
    protected SAXReader createSAXReader() {
        return new SAXReader();
    }

    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
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
                delta = new Diff(
                    expectedDocument.asXML(),
                    actualDocument.asXML());
            }
            catch (final Throwable e) {
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
    public void setActual(final Object actual) {
        this.actual = actual;
    }

    /**
     * Sets the expected XML document which is either a Document, String (of an
     * URI), URI, Reader, or InputStream.
     */
    public void setExpected(final Object expected) {
        this.expected = expected;
    }

    /**
     * Controls whether whitespace differences should be interpreted as
     * differences or not.  The default is {@code false}.  Note that the
     * use of the <code>trim</code> attribute is crucial here.
     */
    public void setIgnoreWhitespace(final boolean ignoreWhitespace) {
        this.ignoreWhitespace = ignoreWhitespace;
    }

}
