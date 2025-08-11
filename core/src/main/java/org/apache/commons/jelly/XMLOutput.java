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

package org.apache.commons.jelly;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.io.XMLWriter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

/** <p>{@code XMLOutput} is used to output XML events
  * in a SAX-like manner. This also allows pipelining to be done
  * such as in the <a href="http://xml.apache.org/cocoon/">Cocoon</a> project.</p>
  */

public class XMLOutput implements ContentHandler, LexicalHandler {

    private final class NamespaceStack {
        /** A list of maps: Each map contains prefix to uri mapping */
        private final List nsStack;

        private NamespaceStack() {
            this.nsStack = new ArrayList();
            this.nsStack.add(new HashMap());
        }

        public void decreaseLevel() {
            nsStack.remove(0);
        }

        public void increaseLevel() {
            nsStack.add(0, new HashMap());
        }

        private boolean isRootNodeDefaultNs(final String prefix, final String uri) {
            return "".equals(prefix) && "".equals(uri) && nsStack.size() == 1;
        }

        public void popNamespace(String prefix) throws SAXException {
            final Map prefixUriMap = (Map)nsStack.get(0);

            if (prefix == null) {
                prefix = "";
            }

            if ("xml".equals(prefix)) {
                // We should ignore setting 'xml' prefix
                // As declared in java of ContentHandler#startPrefixMapping
                return;
            }

            if (prefixUriMap.containsKey(prefix)) {
                final String uri = (String) prefixUriMap.get(prefix);
                prefixUriMap.remove(prefix);
                // If we havent called startPrefixMapping for root node if we wanted to avoid xmlns=""
                // We aren't going to call endPrefixMapping neither
                if (!isRootNodeDefaultNs(prefix, uri)) {
//                    System.out.println(">>>"+XMLOutput.this.hashCode()+">NamespaceStack.popNamespace() prefix="+prefix);
                    contentHandler.endPrefixMapping(prefix);
                }
            }/* else {
                improper nesting ? or already removed in popNamespaces
            }
            */
        }

        public void popNamespaces() throws SAXException {
            final Map prefixUriMap = (Map)nsStack.get(0);
            for (final Iterator iter = prefixUriMap.keySet().iterator();iter.hasNext();) {
                final String prefix = (String)iter.next();
                final String uri = (String) prefixUriMap.get(prefix);
                iter.remove();

                // If we havent called startPrefixMapping for root node if we wanted to avoid xmlns=""
                // We aren't going to call endPrefixMapping neither
                if (!isRootNodeDefaultNs(prefix, uri)) {
//                    System.out.println(">>>"+XMLOutput.this.hashCode()+">NamespaceStack.popNamespaces() prefix="+prefix);
                    contentHandler.endPrefixMapping(prefix);
                }
            }
        }

        public void pushNamespace(String prefix, String uri) throws SAXException {
            Map prefixUriMap;

            if (prefix == null) {
                prefix = "";
            }
            if (uri == null) {
                uri = "";
            }

            if ("xml".equals(prefix)) {
                // We should ignore setting 'xml' prefix
                // As declared in java of ContentHandler#startPrefixMapping
                return;
            }

            // Lets find out if we already declared this same prefix,
            // if not declare in current depth map (the first of list)
            // and call contentHandler.startPrefixMapping(prefix, uri);
            boolean isNew = true;
            for (final Iterator iter = nsStack.iterator(); iter.hasNext();) {
                prefixUriMap = (Map) iter.next();
                if (prefixUriMap.containsKey(prefix)) {
                    if (uri.equals(prefixUriMap.get(prefix))) {
                        // Its an active namespace already
                        // System.out.println(">>>"+XMLOutput.this.hashCode()+">NamespaceStack.pushNamespace() IS NOT NEW prefix="+prefix+",uri="+uri);
                        isNew = false;
                    }
                    // We found it in stack
                    // If it was exactly the same, we won't bother
                    break;
                }
            }

            if (isNew) {
                // not declared sometime before
                prefixUriMap = (Map) nsStack.get(0); // Current depth map
                // Sanity check: Don't let two prefixes for different uris in
                // same depth
                if (prefixUriMap.containsKey(prefix)) {
                    if (!uri.equals(prefixUriMap.get(prefix))) {
                        throw new SAXException("Cannot set same prefix to different URI in same node: trying to add prefix \""
                                + prefix + "\" for uri \""+uri+"\" whereas the declared ones are " + prefixUriMap);
                    }
                } else {
                    prefixUriMap.put(prefix, uri);

                    // To avoid setting xmlns="" for top node (not very nice :D)
                    // We need to specifically check this condition
                    if (!isRootNodeDefaultNs(prefix, uri)) {
//                        System.out.println(">>>"+XMLOutput.this.hashCode()+">NamespaceStack.pushNamespace() prefix="+prefix+",uri="+uri);
                        contentHandler.startPrefixMapping(prefix, uri);
                    }
                }
            }
        }
    }

    protected static final String[] LEXICAL_HANDLER_NAMES =
     {
        "http://xml.org/sax/properties/lexical-handler",
        "http://xml.org/sax/handlers/LexicalHandler" };

    /** Empty attributes. */
    private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(XMLOutput.class);

    /** The default for escaping of text. */
    private static final boolean DEFAULT_ESCAPE_TEXT = false;

    /**
     * returns an XMLOutput object that will discard all
     * tag-generated XML events.  Useful when tag output is not expected
     * or not significant.
     *
     * @return a no-op XMLOutput
     */
    public static XMLOutput createDummyXMLOutput() {
        return new XMLOutput(new DefaultHandler());
    }

    /**
     * Creates a text based XMLOutput which converts all XML events into
     * text and writes to the underlying OutputStream.
     */
    public static XMLOutput createXMLOutput(final OutputStream out) throws UnsupportedEncodingException {
        return createXMLOutput(out, DEFAULT_ESCAPE_TEXT);
    }

    /**
     * Creates a text based XMLOutput which converts all XML events into
     * text and writes to the underlying OutputStream.
     *
     * @param out is the output stream to write
     * @param escapeText is whether or not text output will be escaped. This must be true
     * if the underlying output is XML or could be false if the underlying output is textual.
     * @throws UnsupportedEncodingException if the underlying write could not
     *   be created.
     */
    public static XMLOutput createXMLOutput(final OutputStream out, final boolean escapeText)
            throws UnsupportedEncodingException {
        final XMLWriter xmlWriter = new XMLWriter(out);
        xmlWriter.setEscapeText(escapeText);
        return createXMLOutput(xmlWriter);
    }

    /**
     * Creates a text based XMLOutput which converts all XML events into
     * text and writes to the underlying Writer.
     */
    public static XMLOutput createXMLOutput(final Writer writer) {
        return createXMLOutput(writer, DEFAULT_ESCAPE_TEXT);
    }

    /**
     * Creates a text based XMLOutput which converts all XML events into
     * text and writes to the underlying Writer.
     *
     * @param writer is the writer to output to
     * @param escapeText is whether or not text output will be escaped. This must be true
     *   if the underlying output is XML or could be false if the underlying output is textual.
     */
    public static XMLOutput createXMLOutput(final Writer writer, final boolean escapeText) {
        final XMLWriter xmlWriter = new XMLWriter(writer);
        xmlWriter.setEscapeText(escapeText);
        return createXMLOutput(xmlWriter);
    }

    /**
     * Creates an XMLOutput from an existing SAX XMLReader.
     */
    public static XMLOutput createXMLOutput(final XMLReader xmlReader) {
        final XMLOutput output = new XMLOutput(xmlReader.getContentHandler());

        // isn't it lovely what we've got to do to find the LexicalHandler... ;-)
        for (final String element : LEXICAL_HANDLER_NAMES) {
            try {
                final Object value = xmlReader.getProperty(element);
                if (value instanceof LexicalHandler) {
                    output.setLexicalHandler((LexicalHandler) value);
                    break;
                }
            } catch (final Exception e) {
                // ignore any unsupported-operation exceptions
                if (log.isDebugEnabled()) {
                    log.debug("error setting lexical handler properties", e);
                }
            }
        }
        return output;
    }

    // Implementation methods
    //-------------------------------------------------------------------------
    /**
     * Factory method to create a new XMLOutput from an XMLWriter
     */
    protected static XMLOutput createXMLOutput(final XMLWriter xmlWriter) {
        final XMLOutput answer = new XMLOutput() {
            @Override
            public void close() throws IOException {
                xmlWriter.close();
            }
        };
        answer.setContentHandler(xmlWriter);
        answer.setLexicalHandler(xmlWriter);
        return answer;
    }

    /** The SAX ContentHandler that output goes to. */
    private ContentHandler contentHandler;

    // Static helper methods
    //-------------------------------------------------------------------------

    /** The SAX LexicalHandler that output goes to. */
    private LexicalHandler lexicalHandler;

    /** Stack of known namespaces. */
    private final NamespaceStack namespaceStack = new NamespaceStack();

    public XMLOutput() {
    }

    /** The XML-output will relay the SAX events to the indicated
     * contentHandler.
     * @param contentHandler
     */
    public XMLOutput(final ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
        // often classes will implement LexicalHandler as well
        if (contentHandler instanceof LexicalHandler) {
            this.lexicalHandler = (LexicalHandler) contentHandler;
        }
    }

    /** The XML-output will relay the SAX events to the indicated
     * content-handler lexical-handler.
     * @param contentHandler
     * @param lexicalHandler
     */
    public XMLOutput(
        final ContentHandler contentHandler,
        final LexicalHandler lexicalHandler) {
        this.contentHandler = contentHandler;
        this.lexicalHandler = lexicalHandler;
    }

    /**
     * Receive notification of character data.
     *
     * <p>The Parser will call this method to report each chunk of
     * character data.  SAX parsers may return all contiguous character
     * data in a single chunk, or they may split it into several
     * chunks; however, all of the characters in any single event
     * must come from the same external entity so that the Locator
     * provides useful information.</p>
     *
     * <p>The application must not attempt to read from the array
     * outside of the specified range.</p>
     *
     * <p>Individual characters may consist of more than one Java
     * {@code char} value.  There are two important cases where this
     * happens, because characters can't be represented in just sixteen bits.
     * In one case, characters are represented in a <em>Surrogate Pair</em>,
     * using two special Unicode values. Such characters are in the so-called
     * "Astral Planes", with a code point above U+FFFF.  A second case involves
     * composite characters, such as a base character combining with one or
     * more accent characters.</p>
     *
     * <p> Your code should not assume that algorithms using
     * {@code char}-at-a-time idioms will be working in character
     * units; in some cases they will split characters.  This is relevant
     * wherever XML permits arbitrary characters, such as attribute values,
     * processing instruction data, and comments as well as in data reported
     * from this method.  It's also generally relevant whenever Java code
     * manipulates internationalized text; the issue isn't unique to XML.</p>
     *
     * <p>Note that some parsers will report whitespace in element
     * content using the {@link #ignorableWhitespace ignorableWhitespace}
     * method rather than this one (validating parsers <em>must</em>
     * do so).</p>
     *
     * @param ch The characters from the XML document.
     * @param start The start position in the array.
     * @param length The number of characters to read from the array.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see #ignorableWhitespace
     * @see org.xml.sax.Locator
     */
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        contentHandler.characters(ch, start, length);
    }

    // Extra helper methods provided for tag authors
    //-------------------------------------------------------------------------

    /**
     * Provides a useful hook that implementations
     * can use to close the
     * underlying OutputStream or Writer.
     *
     * @throws IOException
     */
    public void close() throws IOException {
    }

    /**
     * Report an XML comment anywhere in the document.
     *
     * <p>This callback will be used for comments inside or outside the
     * document element, including comments in the external DTD
     * subset (if read).  Comments in the DTD must be properly
     * nested inside start/endDTD and start/endEntity events (if
     * used).</p>
     *
     * @param ch An array holding the characters in the comment.
     * @param start The starting position in the array.
     * @param length The number of characters to use from the array.
     * @throws SAXException The application may raise an exception.
     */
    @Override
    public void comment(final char ch[], final int start, final int length) throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.comment(ch, start, length);
        }
    }

    /**
     * Report the end of a CDATA section.
     *
     * @throws SAXException The application may raise an exception.
     * @see #startCDATA
     */
    @Override
    public void endCDATA() throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.endCDATA();
        }
    }

    /**
     * Receive notification of the end of a document.
     *
     * <p>The SAX parser will invoke this method only once, and it will
     * be the last method invoked during the parse.  The parser shall
     * not invoke this method until it has either abandoned parsing
     * (because of an unrecoverable error) or reached the end of
     * input.</p>
     *
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see #startDocument
     */
    @Override
    public void endDocument() throws SAXException {
        contentHandler.endDocument();
    }

    /**
     * Report the end of DTD declarations.
     *
     * <p>This method is intended to report the end of the
     * DOCTYPE declaration; if the document has no DOCTYPE declaration,
     * this method will not be invoked.</p>
     *
     * @throws SAXException The application may raise an exception.
     * @see #startDTD
     */
    @Override
    public void endDTD() throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.endDTD();
        }
    }

    /**
     * Helper method for outputting an end element event
     * for an element in no namespace.
     */
    public void endElement(final String localName) throws SAXException {
        endElement("", localName, localName);
    }

    // ContentHandler interface
    //-------------------------------------------------------------------------

    /**
     * Receive notification of the end of an element.
     *
     * <p>The SAX parser will invoke this method at the end of every
     * element in the XML document; there will be a corresponding
     * {@link #startElement startElement} event for every endElement
     * event (even when the element is empty).</p>
     *
     * <p>For information on the names, see startElement.</p>
     *
     * @param uri The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param qName The qualified XML 1.0 name (with prefix), or the
     *        empty string if qualified names are not available.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     */
    @Override
    public void endElement(final String uri, final String localName, final String qName)
        throws SAXException {
        contentHandler.endElement(uri, localName, qName);
        // Inform namespaceStack to return to previous depth
        namespaceStack.decreaseLevel();
        namespaceStack.popNamespaces();
    }

    /**
     * Report the end of an entity.
     *
     * @param name The name of the entity that is ending.
     * @throws SAXException The application may raise an exception.
     * @see #startEntity
     */
    @Override
    public void endEntity(final String name) throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.endEntity(name);
        }
    }

    /**
     * End the scope of a prefix-URI mapping.
     *
     * <p>See {@link #startPrefixMapping startPrefixMapping} for
     * details.  These events will always occur immediately after the
     * corresponding {@link #endElement endElement} event, but the order of
     * {@link #endPrefixMapping endPrefixMapping} events is not otherwise
     * guaranteed.</p>
     *
     * @param prefix The prefix that was being mapped.
     *  This is the empty string when a default mapping scope ends.
     * @throws org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see #startPrefixMapping
     * @see #endElement
     */
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        // End prefix mapping was already called after endElement
        // contentHandler.endPrefixMapping(prefix);
    }

    /** Flushes the underlying stream if {@link XMLWriter}
     * or {@link XMLOutput}.
     *
     * @throws IOException
     */
    public void flush() throws IOException {
        if (contentHandler instanceof XMLWriter) {
            ((XMLWriter)contentHandler).flush();
        } else if (contentHandler instanceof XMLOutput) {
            ((XMLOutput)contentHandler).flush();
        }
    }

    // Properties
    //-------------------------------------------------------------------------
    /**
     * @return the SAX ContentHandler to use to pipe SAX events into
     */
    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    /**
     * @return the SAX LexicalHandler to use to pipe SAX events into
     */
    public LexicalHandler getLexicalHandler() {
        return lexicalHandler;
    }

    /**
     * Receive notification of ignorable whitespace in element content.
     *
     * <p>Validating Parsers must use this method to report each chunk
     * of whitespace in element content (see the W3C XML 1.0 recommendation,
     * section 2.10): non-validating parsers may also use this method
     * if they are capable of parsing and using content models.</p>
     *
     * <p>SAX parsers may return all contiguous whitespace in a single
     * chunk, or they may split it into several chunks; however, all of
     * the characters in any single event must come from the same
     * external entity, so that the Locator provides useful
     * information.</p>
     *
     * <p>The application must not attempt to read from the array
     * outside of the specified range.</p>
     *
     * @param ch The characters from the XML document.
     * @param start The start position in the array.
     * @param length The number of characters to read from the array.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see #characters
     */
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length)
        throws SAXException {
        contentHandler.ignorableWhitespace(ch, start, length);
    }

    /** Pass data through the pipeline.
      * By default, this call is ignored.
      * Subclasses are invited to use this as a way for children tags to
      * pass data to their parent.
      *
      * @param object the data to pass
      * @throws SAXException The application may raise an exception.
      */
    public void objectData(final Object object) throws SAXException {
        if (contentHandler instanceof XMLOutput) {
            ((XMLOutput) contentHandler).objectData(object);
        } else if (object!=null) {
            final String output=object.toString();
            write(output);
        } else {
            // we could have a "configurable null-toString"...
            write("null");
        }
    }

    /**
     * Receive notification of a processing instruction.
     *
     * <p>The Parser will invoke this method once for each processing
     * instruction found: note that processing instructions may occur
     * before or after the main document element.</p>
     *
     * <p>A SAX parser must never report an XML declaration (XML 1.0,
     * section 2.8) or a text declaration (XML 1.0, section 4.3.1)
     * using this method.</p>
     *
     * <p>Like {@link #characters characters()}, processing instruction
     * data may have characters that need more than one {@code char}
     * value.</p>
     *
     * @param target The processing instruction target.
     * @param data The processing instruction data, or null if
     *        none was supplied.  The data does not include any
     *        whitespace separating it from the target.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     */
    @Override
    public void processingInstruction(final String target, final String data)
        throws SAXException {
        contentHandler.processingInstruction(target, data);
    }

    /**
     * Sets the SAX ContentHandler to pipe SAX events into
     *
     * @param contentHandler is the new ContentHandler to use.
     *      This value cannot be null.
     */
    public void setContentHandler(final ContentHandler contentHandler) {
        this.contentHandler = Objects.requireNonNull(contentHandler, "contentHandler");
    }

    /**
     * Receive an object for locating the origin of SAX document events.
     *
     * <p>SAX parsers are strongly encouraged (though not absolutely
     * required) to supply a locator: if it does so, it must supply
     * the locator to the application by invoking this method before
     * invoking any of the other methods in the ContentHandler
     * interface.</p>
     *
     * <p>The locator allows the application to determine the end
     * position of any document-related event, even if the parser is
     * not reporting an error.  Typically, the application will
     * use this information for reporting its own errors (such as
     * character content that does not match an application's
     * business rules).  The information returned by the locator
     * is probably not sufficient for use with a search engine.</p>
     *
     * <p>Note that the locator will return correct information only
     * during the invocation of the events in this interface.  The
     * application should not attempt to use it at any other time.</p>
     *
     * @param locator An object that can return the location of
     *                any SAX document event.
     * @see org.xml.sax.Locator
     */
    @Override
    public void setDocumentLocator(final Locator locator) {
        contentHandler.setDocumentLocator(locator);
    }

    // Lexical Handler interface
    //-------------------------------------------------------------------------

    /**
     * Sets the SAX LexicalHandler to pipe SAX events into
     *
     * @param lexicalHandler is the new LexicalHandler to use.
     *      This value can be null.
     */
    public void setLexicalHandler(final LexicalHandler lexicalHandler) {
        this.lexicalHandler = lexicalHandler;
    }

    /**
     * Receive notification of a skipped entity.
     * This is not called for entity references within markup constructs
     * such as element start tags or markup declarations.  (The XML
     * recommendation requires reporting skipped external entities.
     * SAX also reports internal entity expansion/non-expansion, except
     * within markup constructs.)
     *
     * <p>The Parser will invoke this method each time the entity is
     * skipped.  Non-validating processors may skip entities if they
     * have not seen the declarations (because, for example, the
     * entity was declared in an external DTD subset).  All processors
     * may skip external entities, depending on the values of the
     * {@code http://xml.org/sax/features/external-general-entities}
     * and the
     * {@code http://xml.org/sax/features/external-parameter-entities}
     * properties.</p>
     *
     * @param name The name of the skipped entity.  If it is a
     *        parameter entity, the name will begin with '%', and if
     *        it is the external DTD subset, it will be the string
     *        "[dtd]".
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     */
    @Override
    public void skippedEntity(final String name) throws SAXException {
        contentHandler.skippedEntity(name);
    }

    /**
     * Report the start of a CDATA section.
     *
     * <p>The contents of the CDATA section will be reported through
     * the regular {@link org.xml.sax.ContentHandler#characters
     * characters} event; this event is intended only to report
     * the boundary.</p>
     *
     * @throws SAXException The application may raise an exception.
     * @see #endCDATA
     */
    @Override
    public void startCDATA() throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.startCDATA();
        }
    }

    /**
     * Receive notification of the beginning of a document.
     *
     * <p>The SAX parser will invoke this method only once, before any
     * other event callbacks (except for {@link #setDocumentLocator
     * setDocumentLocator}).</p>
     *
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see #endDocument
     */
    @Override
    public void startDocument() throws SAXException {
        contentHandler.startDocument();
    }

    /**
     * Report the start of DTD declarations, if any.
     *
     * <p>This method is intended to report the beginning of the
     * DOCTYPE declaration; if the document has no DOCTYPE declaration,
     * this method will not be invoked.</p>
     *
     * <p>All declarations reported through
     * {@link org.xml.sax.DTDHandler DTDHandler} or
     * {@link org.xml.sax.ext.DeclHandler DeclHandler} events must appear
     * between the startDTD and {@link #endDTD endDTD} events.
     * Declarations are assumed to belong to the internal DTD subset
     * unless they appear between {@link #startEntity startEntity}
     * and {@link #endEntity endEntity} events.  Comments and
     * processing instructions from the DTD should also be reported
     * between the startDTD and endDTD events, in their original
     * order of (logical) occurrence; they are not required to
     * appear in their correct locations relative to DTDHandler
     * or DeclHandler events, however.</p>
     *
     * <p>Note that the start/endDTD events will appear within
     * the start/endDocument events from ContentHandler and
     * before the first
     * {@link org.xml.sax.ContentHandler#startElement startElement}
     * event.</p>
     *
     * @param name The document type name.
     * @param publicId The declared public identifier for the
     *        external DTD subset, or null if none was declared.
     * @param systemId The declared system identifier for the
     *        external DTD subset, or null if none was declared.
     *        (Note that this is not resolved against the document
     *        base URI.)
     * @throws SAXException The application may raise an
     *            exception.
     * @see #endDTD
     * @see #startEntity
     */
    @Override
    public void startDTD(final String name, final String publicId, final String systemId)
        throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.startDTD(name, publicId, systemId);
        }
    }

    /**
     * Helper method for outputting a start element event
     * for an element in no namespace.
     */
    public void startElement(final String localName) throws SAXException {
        startElement("", localName, localName, EMPTY_ATTRIBUTES);
    }

    /**
     * Helper method for outputting a start element event
     * for an element in no namespace.
     */
    public void startElement(final String localName, final Attributes attributes) throws SAXException {
        startElement("", localName, localName, attributes);
    }

    /**
     * Receive notification of the beginning of an element.
     *
     * <p>The Parser will invoke this method at the beginning of every
     * element in the XML document; there will be a corresponding
     * {@link #endElement endElement} event for every startElement event
     * (even when the element is empty). All of the element's content will be
     * reported, in order, before the corresponding endElement
     * event.</p>
     *
     * <p>This event allows up to three name components for each
     * element:</p>
     *
     * <ol>
     * <li>the Namespace URI;</li>
     * <li>the local name; and</li>
     * <li>the qualified (prefixed) name.</li>
     * </ol>
     *
     * <p>Any or all of these may be provided, depending on the
     * values of the <var>http://xml.org/sax/features/namespaces</var>
     * and the <var>http://xml.org/sax/features/namespace-prefixes</var>
     * properties:</p>
     *
     * <ul>
     * <li>the Namespace URI and local name are required when
     * the namespaces property is <var>true</var> (the default), and are
     * optional when the namespaces property is <var>false</var> (if one is
     * specified, both must be);</li>
     * <li>the qualified name is required when the namespace-prefixes property
     * is <var>true</var>, and is optional when the namespace-prefixes property
     * is <var>false</var> (the default).</li>
     * </ul>
     *
     * <p>Note that the attribute list provided will contain only
     * attributes with explicit values (specified or defaulted):
     * #IMPLIED attributes will be omitted.  The attribute list
     * will contain attributes used for Namespace declarations
     * (xmlns* attributes) only if the
     * {@code http://xml.org/sax/features/namespace-prefixes}
     * property is true (it is false by default, and support for a
     * true value is optional).</p>
     *
     * <p>Like {@link #characters characters()}, attribute values may have
     * characters that need more than one {@code char} value.  </p>
     *
     * @param uri The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param qName The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     * @param atts The attributes attached to the element.  If
     *        there are no attributes, it shall be an empty
     *        Attributes object.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see #endElement
     * @see org.xml.sax.Attributes
     */
    @Override
    public void startElement(
        final String uri,
        final String localName,
        final String qName,
        final Attributes atts)
        throws SAXException {

        int idx = qName.indexOf(':');
        String attNsPrefix = "";
        if (idx >= 0) {
            attNsPrefix = qName.substring(0, idx);
        }
        namespaceStack.pushNamespace(attNsPrefix, uri);
        for (int i = 0; i < atts.getLength(); i++) {
            final String attQName = atts.getQName(i);
            // An attribute only has an namespace if has a prefix
            // If not, stays in namespace of containing node
            idx = attQName.indexOf(':');
            if (idx >= 0) {
                attNsPrefix = attQName.substring(0, idx);
                final String attUri = atts.getURI(i);
                namespaceStack.pushNamespace(attNsPrefix, attUri);
            }
        }

        contentHandler.startElement(uri, localName, qName, atts);
        // Inform namespaceStack of a new depth
        namespaceStack.increaseLevel();
    }

    /**
     * Report the beginning of some internal and external XML entities.
     *
     * <p>The reporting of parameter entities (including
     * the external DTD subset) is optional, and SAX2 drivers that
     * report LexicalHandler events may not implement it; you can use the
     * <code
     * >http://xml.org/sax/features/lexical-handler/parameter-entities</code>
     * feature to query or control the reporting of parameter entities.</p>
     *
     * <p>General entities are reported with their regular names,
     * parameter entities have '%' prepended to their names, and
     * the external DTD subset has the pseudo-entity name "[dtd]".</p>
     *
     * <p>When a SAX2 driver is providing these events, all other
     * events must be properly nested within start/end entity
     * events.  There is no additional requirement that events from
     * {@link org.xml.sax.ext.DeclHandler DeclHandler} or
     * {@link org.xml.sax.DTDHandler DTDHandler} be properly ordered.</p>
     *
     * <p>Note that skipped entities will be reported through the
     * {@link org.xml.sax.ContentHandler#skippedEntity skippedEntity}
     * event, which is part of the ContentHandler interface.</p>
     *
     * <p>Because of the streaming event model that SAX uses, some
     * entity boundaries cannot be reported under any
     * circumstances:</p>
     *
     * <ul>
     * <li>general entities within attribute values</li>
     * <li>parameter entities within declarations</li>
     * </ul>
     *
     * <p>These will be silently expanded, with no indication of where
     * the original entity boundaries were.</p>
     *
     * <p>Note also that the boundaries of character references (which
     * are not really entities anyway) are not reported.</p>
     *
     * <p>All start/endEntity events must be properly nested.
     *
     * @param name The name of the entity.  If it is a parameter
     *        entity, the name will begin with '%', and if it is the
     *        external DTD subset, it will be "[dtd]".
     * @throws SAXException The application may raise an exception.
     * @see #endEntity
     * @see org.xml.sax.ext.DeclHandler#internalEntityDecl
     * @see org.xml.sax.ext.DeclHandler#externalEntityDecl
     */
    @Override
    public void startEntity(final String name) throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.startEntity(name);
        }
    }

    /**
     * Begin the scope of a prefix-URI Namespace mapping.
     *
     * <p>The information from this event is not necessary for
     * normal Namespace processing: the SAX XML reader will
     * automatically replace prefixes for element and attribute
     * names when the {@code http://xml.org/sax/features/namespaces}
     * feature is <var>true</var> (the default).</p>
     *
     * <p>There are cases, however, when applications need to
     * use prefixes in character data or in attribute values,
     * where they cannot safely be expanded automatically; the
     * start/endPrefixMapping event supplies the information
     * to the application to expand prefixes in those contexts
     * itself, if necessary.</p>
     *
     * <p>Note that start/endPrefixMapping events are not
     * guaranteed to be properly nested relative to each other:
     * all startPrefixMapping events will occur immediately before the
     * corresponding {@link #startElement startElement} event,
     * and all {@link #endPrefixMapping endPrefixMapping}
     * events will occur immediately after the corresponding
     * {@link #endElement endElement} event,
     * but their order is not otherwise
     * guaranteed.</p>
     *
     * <p>There should never be start/endPrefixMapping events for the
     * "xml" prefix, since it is predeclared and immutable.</p>
     *
     * @param prefix The Namespace prefix being declared.
     *  An empty string is used for the default element namespace,
     *  which has no prefix.
     * @param uri The Namespace URI the prefix is mapped to.
     * @throws org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see #endPrefixMapping
     * @see #startElement
     */
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        namespaceStack.pushNamespace(prefix, uri);
        // contentHandler.startPrefixMapping(prefix, uri) will be called if needed
        // in pushNamespace
    }

    @Override
    public String toString() {
        return super.toString()
            + "[contentHandler="
            + contentHandler
            + ";lexicalHandler="
            + lexicalHandler
            + "]";
    }

    /**
     * Outputs the given String as a piece of valid text in the
     * XML event stream.
     * Any special XML characters should come out properly escaped.
     */
    public void write(final String text) throws SAXException {
        final char[] ch = text.toCharArray();
        characters(ch, 0, ch.length);
    }

    /**
     * Outputs the given String as a piece of CDATA in the
     * XML event stream.
     */
    public void writeCDATA(final String text) throws SAXException {
        startCDATA();
        final char[] ch = text.toCharArray();
        characters(ch, 0, ch.length);
        endCDATA();
    }

    /**
     * Outputs a comment to the XML stream.
     */
    public void writeComment(final String text) throws SAXException {
        final char[] ch = text.toCharArray();
        comment(ch, 0, ch.length);
    }
}
