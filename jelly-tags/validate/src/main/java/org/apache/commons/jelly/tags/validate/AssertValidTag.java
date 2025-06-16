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
package org.apache.commons.jelly.tags.validate;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.junit.JellyAssertionFailedError;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This tag performs an assertion that the tags body contains XML
 * which matches a givem schema validation. This tag is used with
 * JellyUnit to implement an assertion.
 */
public class AssertValidTag extends ValidateTag {

    private final StringBuffer buffer = new StringBuffer();

/*
    public AssertValidTag() {
        setErrorHandler(
            new ErrorHandler() {
                public void error(SAXParseException exception) throws SAXException {
                    outputException(output, "error", exception);
                }

                public void fatalError(SAXParseException exception) throws SAXException {
                    outputException(output, "fatalError", exception);
                }

                public void warning(SAXParseException exception) throws SAXException {
                    outputException(output, "warning", exception);
                }
            }
        );
    }
*/
    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        buffer.setLength(0);
        super.doTag(output);
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Processes whether or not the document is valid.
     * Derived classes can overload this method to do different things, such
     * as to throw assertion exceptions etc.
     */
    @Override
    protected void handleValid(final boolean valid) {
        super.handleValid(valid);

        if ( ! valid ) {
            final String message = buffer.toString();
            throw new JellyAssertionFailedError( "The XML is not valid according to the schema: " + message );
        }
    }

    /**
     * Outputs the given validation exception as XML to the output
     */
    @Override
    protected void outputException(final XMLOutput output, final String name, final SAXParseException e) throws SAXException {
        buffer.append( name );
        buffer.append( " : line: " );
        buffer.append( e.getLineNumber() );
        buffer.append( " column: " );
        buffer.append( e.getColumnNumber() );
        buffer.append( " message: " );
        buffer.append( e.getMessage() );
        buffer.append( '\n' );
    }

}
