/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/parser/XMLParser.java,v 1.13 2002/04/26 12:28:56 jstrachan Exp $
 * $Revision: 1.13 $
 * $Date: 2002/04/26 12:28:56 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * $Id: XMLParser.java,v 1.13 2002/04/26 12:28:56 jstrachan Exp $
 */
package org.apache.commons.jelly.parser;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.apache.commons.collections.ArrayStack;

import org.apache.commons.jelly.Context;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.impl.ScriptBlock;
import org.apache.commons.jelly.impl.StaticTag;
import org.apache.commons.jelly.impl.DynaTagScript;
import org.apache.commons.jelly.impl.TagScript;
import org.apache.commons.jelly.impl.TextScript;
import org.apache.commons.jelly.expression.ConstantExpression;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.expression.ExpressionFactory;
import org.apache.commons.jelly.expression.jexl.JexlExpressionFactory;
import org.apache.commons.jelly.tags.core.CoreTagLibrary;
import org.apache.commons.jelly.tags.xml.XMLTagLibrary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;


/** <p><code>XMLParser</code> parses the XML Jelly format.
 * The SAXParser and XMLReader portions of this code come from Digester.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.13 $
 */
public class XMLParser extends DefaultHandler {

    /** Context which is used to locate tag libraries*/
    private Context context = new Context();
    
    /** the expression factory used to evaluate tag attributes */
    private ExpressionFactory expressionFactory;
    
    /** The current script block */
    private ScriptBlock script;
    
    /** The current tagScript */
    private TagScript tagScript;

    /** The current parent tag */
    private Tag parentTag;
    
    /** The stack of body scripts. */
    private ArrayStack scriptStack = new ArrayStack();
    
    /** The stack of tagScripts - use ArrayList as it allows null. */
    private ArrayList tagScriptStack = new ArrayList();
    
    /** The current text buffer where non-custom tags get written */
    private StringBuffer textBuffer;
    
    /**
     * The class loader to use for instantiating application objects.
     * If not specified, the context class loader, or the class loader
     * used to load XMLParser itself, is used, based on the value of the
     * <code>useContextClassLoader</code> variable.
     */
    protected ClassLoader classLoader = null;
    
    
    
    /**
     * The application-supplied error handler that is notified when parsing
     * warnings, errors, or fatal errors occur.
     */
    protected ErrorHandler errorHandler = null;
    
    
    /**
     * The SAXParserFactory that is created the first time we need it.
     */
    protected static SAXParserFactory factory = null;
    
    /**
     * The SAXParser we will use to parse the input stream.
     */
    protected SAXParser parser = null;
    
    /**
     * The XMLReader used to parse digester rules.
     */
    protected XMLReader reader = null;
    
    
    /**
     * The Locator associated with our parser.
     */
    protected Locator locator = null;
    
    
    /**
     * Registered namespaces we are currently processing.  The key is the
     * namespace prefix that was declared in the document.  The value is an
     * ArrayStack of the namespace URIs this prefix has been mapped to --
     * the top Stack element is the most current one.  (This architecture
     * is required because documents can declare nested uses of the same
     * prefix for different Namespace URIs).
     */
    protected HashMap namespaces = new HashMap();
    
    
    /**
     * Do we want to use the Context ClassLoader when loading classes
     * for instantiating new objects?  Default is <code>false</code>.
     */
    protected boolean useContextClassLoader = false;
    
    
    /**
     * Do we want to use a validating parser?
     */
    protected boolean validating = false;
    
    
    /** Flag to indicate if this object has been configured */
    private boolean configured;
    
    /**
     * The Log to which logging calls will be made.
     */
    private Log log = LogFactory.getLog( XMLParser.class );
    
    
    /**
     * Construct a new XMLParser with default properties.
     */
    public XMLParser() {
    }
    
    /**
     * Construct a new XMLParser, allowing a SAXParser to be passed in.  This
     * allows XMLParser to be used in environments which are unfriendly to
     * JAXP1.1 (such as WebLogic 6.0).  Thanks for the request to change go to
     * James House (james@interobjective.com).  This may help in places where
     * you are able to load JAXP 1.1 classes yourself.
     */
    public XMLParser(SAXParser parser) {
        this.parser = parser;
    }
    
    /**
     * Construct a new XMLParser, allowing an XMLReader to be passed in.  This
     * allows XMLParser to be used in environments which are unfriendly to
     * JAXP1.1 (such as WebLogic 6.0).  Note that if you use this option you
     * have to configure namespace and validation support yourself, as these
     * properties only affect the SAXParser and emtpy constructor.
     */
    public XMLParser(XMLReader reader) {
        this.reader = reader;
    }


    /**
     * Parse the content of the specified file using this XMLParser.  Returns
     * the root element from the object stack (if any).
     *
     * @param file File containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Script parse(File file) throws IOException, SAXException {
        ensureConfigured();
        getXMLReader().parse(new InputSource(new FileReader(file)));
        return script;
    }
    
    
    /**
     * Parse the content of the specified input source using this XMLParser.
     * Returns the root element from the object stack (if any).
     *
     * @param input Input source containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Script parse(InputSource input) throws IOException, SAXException {
        ensureConfigured();
        getXMLReader().parse(input);
        return script;
        
    }
    
    
    /**
     * Parse the content of the specified input stream using this XMLParser.
     * Returns the root element from the object stack (if any).
     *
     * @param input Input stream containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Script parse(InputStream input) throws IOException, SAXException {
        ensureConfigured();
        getXMLReader().parse(new InputSource(input));
        return script;        
    }
    
    
    /**
     * Parse the content of the specified reader using this XMLParser.
     * Returns the root element from the object stack (if any).
     *
     * @param reader Reader containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Script parse(Reader reader) throws IOException, SAXException {
        ensureConfigured();
        getXMLReader().parse(new InputSource(reader));
        return script;
        
    }
    
    
    /**
     * Parse the content of the specified URI using this XMLParser.
     * Returns the root element from the object stack (if any).
     *
     * @param uri URI containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Script parse(String uri) throws IOException, SAXException {
        ensureConfigured();
        getXMLReader().parse(uri);
        return script;
        
    }
    
    
    
    /**
     * Return the currently mapped namespace URI for the specified prefix,
     * if any; otherwise return <code>null</code>.  These mappings come and
     * go dynamically as the document is parsed.
     *
     * @param prefix Prefix to look up
     */
    public String findNamespaceURI(String prefix) {
        ArrayStack stack = (ArrayStack) namespaces.get(prefix);
        if (stack == null) {
            return (null);
        }
        try {
            return ((String) stack.peek());
        }
        catch (EmptyStackException e) {
            return (null);
        }
    }
    

    
    // Properties
    //-------------------------------------------------------------------------                    
    
    public Context getContext() {
        return context;
    }
    
    public void setContext(Context context) {
        this.context = context;
    }
        
    /**
     * Return the class loader to be used for instantiating application objects
     * when required.  This is determined based upon the following rules:
     * <ul>
     * <li>The class loader set by <code>setClassLoader()</code>, if any</li>
     * <li>The thread context class loader, if it exists and the
     *     <code>useContextClassLoader</code> property is set to true</li>
     * <li>The class loader used to load the XMLParser class itself.
     * </ul>
     */
    public ClassLoader getClassLoader() {
        if (this.classLoader != null) {
            return (this.classLoader);
        }
        if (this.useContextClassLoader) {
            ClassLoader classLoader =
            Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                return (classLoader);
            }
        }
        return (this.getClass().getClassLoader());
    }
    
    
    /**
     * Set the class loader to be used for instantiating application objects
     * when required.
     *
     * @param classLoader The new class loader to use, or <code>null</code>
     *  to revert to the standard rules
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    
    
    /**
     * Return the error handler for this XMLParser.
     */
    public ErrorHandler getErrorHandler() {
        return (this.errorHandler);
    }
    
    
    /**
     * Set the error handler for this XMLParser.
     *
     * @param errorHandler The new error handler
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    
    /**
     * Return the current Logger associated with this instance of the XMLParser
     */
    public Log getLogger() {
        return log;
    }
    
    
    /**
     * Set the current logger for this XMLParser.
     */
    public void setLogger(Log log) {
        this.log = log;
    }

    /** @return the expression factory used to evaluate tag attributes */
    public ExpressionFactory getExpressionFactory() {
        if ( expressionFactory == null ) {
            expressionFactory = createExpressionFactory();
        }
        return expressionFactory;
    }
    
    /** Sets the expression factory used to evaluate tag attributes */
    public void setExpressionFactory(ExpressionFactory expressionFactory) {
        this.expressionFactory = expressionFactory;
    }
    
    /**
     * Return the SAXParser we will use to parse the input stream.  If there
     * is a problem creating the parser, return <code>null</code>.
     */
    public SAXParser getParser() {
        // Return the parser we already created (if any)
        if (parser != null) {
            return (parser);
        }
        
        // Create and return a new parser
        synchronized (this) {
            try {
                if (factory == null) {
                    factory = SAXParserFactory.newInstance();
                }
                factory.setNamespaceAware(true);
                factory.setValidating(validating);
                parser = factory.newSAXParser();
                return (parser);
            }
            catch (Exception e) {
                log.error("XMLParser.getParser: ", e);
                return (null);
            }
        }
    }
    
    
    /**
     * By setting the reader in the constructor, you can bypass JAXP and
     * be able to use digester in Weblogic 6.0.
     *
     * @deprecated Use getXMLReader() instead, which can throw a
     *  SAXException if the reader cannot be instantiated
     */
    public XMLReader getReader() {
        try {
            return (getXMLReader());
        }
        catch (SAXException e) {
            log.error("Cannot get XMLReader", e);
            return (null);
        }
    }
    
    
    /**
     * Return the XMLReader to be used for parsing the input document.
     *
     * @exception SAXException if no XMLReader can be instantiated
     */
    public synchronized XMLReader getXMLReader() throws SAXException {
        if (reader == null) {
            reader = getParser().getXMLReader();
        }
        
        //set up the parse
        reader.setContentHandler(this);
        reader.setDTDHandler(this);
        //reader.setEntityResolver(this);
        reader.setErrorHandler(this);
        return reader;
    }
    
    
    
    /**
     * Return the validating parser flag.
     */
    public boolean getValidating() {
        return (this.validating);
    }
    
    
    /**
     * Set the validating parser flag.  This must be called before
     * <code>parse()</code> is called the first time.
     *
     * @param validating The new validating parser flag.
     */
    public void setValidating(boolean validating) {
        this.validating = validating;
    }
    
    
    /**
     * Return the boolean as to whether the context classloader should be used.
     */
    public boolean getUseContextClassLoader() {
        return useContextClassLoader;
    }
    
    
    /**
     * Determine whether to use the Context ClassLoader (the one found by
     * calling <code>Thread.currentThread().getContextClassLoader()</code>)
     * to resolve/load classes that are defined in various rules.  If not
     * using Context ClassLoader, then the class-loading defaults to
     * using the calling-class' ClassLoader.
     *
     * @param boolean determines whether to use Context ClassLoader.
     */
    public void setUseContextClassLoader(boolean use) {
        useContextClassLoader = use;
    }
    
    
    // ContentHandler interface
    //-------------------------------------------------------------------------
    
    /**
     * Process notification of the beginning of the document being reached.
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void startDocument() throws SAXException {
        script = new ScriptBlock();
        textBuffer = new StringBuffer();
        tagScript = null;
        parentTag = null;
        scriptStack.clear();
        tagScriptStack.clear();
    }
    
    
    /**
     * Process notification of the end of the document being reached.
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void endDocument() throws SAXException {
        textBuffer = null;
    }
    
    /**
     * Process notification of the start of an XML element being reached.
     *
     * @param uri The Namespace URI, or the empty string if the element
     *   has no Namespace URI or if Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty
     *   string if Namespace processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty
     *   string if qualified names are not available.\
     * @param list The attributes attached to the element. If there are
     *   no attributes, it shall be an empty Attributes object.
     * @exception SAXException if a parsing error is to be reported
     */
    public void startElement(
        String namespaceURI, String localName, String qName, Attributes list
    ) throws SAXException {
        
        // if this is a tag then create a script to run it
        // otherwise pass the text to the current body
        tagScript = createTag( namespaceURI, localName, list );
        if ( tagScript == null ) {
            tagScript = createStaticTag( namespaceURI, localName, qName, list );
        }
        tagScriptStack.add( tagScript );

        if ( tagScript != null ) {            
            // set parent relationship...
            Tag tag = tagScript.getTag();
            tag.setParent( parentTag );
            parentTag = tag;

            if ( textBuffer.length() > 0 ) {
                script.addScript( new TextScript( textBuffer.toString() ) );
                textBuffer.setLength(0);
            }

            script.addScript( tagScript );

            // start a new body
            scriptStack.push( script );
            script = new ScriptBlock();
            tag.setBody( script );            
        }
        else {
            // XXXX: might wanna handle empty elements later...

            textBuffer.append( "<" );
            textBuffer.append( qName );
            int size = list.getLength();
            for ( int i = 0; i < size; i++ ) {
                textBuffer.append( " " );
                textBuffer.append( list.getQName(i) );
                textBuffer.append( "=" );
                textBuffer.append( "\"" );
                textBuffer.append( list.getValue(i) );
                textBuffer.append( "\"" );
            }
            textBuffer.append( ">" );
        }
    }
    
    /**
     * Process notification of character data received from the body of
     * an XML element.
     *
     * @param buffer The characters from the XML document
     * @param start Starting offset into the buffer
     * @param length Number of characters from the buffer
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void characters(char buffer[], int start, int length) throws SAXException {
        textBuffer.append(buffer, start, length);
    }
    
    
    
    /**
     * Process notification of the end of an XML element being reached.
     *
     * @param uri - The Namespace URI, or the empty string if the
     *   element has no Namespace URI or if Namespace processing is not
     *   being performed.
     * @param localName - The local name (without prefix), or the empty
     *   string if Namespace processing is not being performed.
     * @param qName - The qualified XML 1.0 name (with prefix), or the
     *   empty string if qualified names are not available.
     * @exception SAXException if a parsing error is to be reported
     */
    public void endElement(
        String namespaceURI, String localName, String qName
    ) throws SAXException {        
        
        tagScript = (TagScript) tagScriptStack.remove( tagScriptStack.size() - 1 );
        if ( tagScript != null ) {
            if ( textBuffer.length() > 0 ) {
                script.addScript( new TextScript( textBuffer.toString() ) );
                textBuffer.setLength(0);
            }
            script = (ScriptBlock) scriptStack.pop();
        }
        else {
            textBuffer.append( "</" );
            textBuffer.append( qName );
            textBuffer.append( ">" );
        }
    }
    
    
    /**
     * Process notification that a namespace prefix is going out of scope.
     *
     * @param prefix Prefix that is going out of scope
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void endPrefixMapping(String prefix) throws SAXException {
        
        // Deregister this prefix mapping
        ArrayStack stack = (ArrayStack) namespaces.get(prefix);
        if (stack == null) {
            return;
        }
        try {
            stack.pop();
            if (stack.empty()) {
                namespaces.remove(prefix);
            }
        }
        catch (EmptyStackException e) {
            throw createSAXException("endPrefixMapping popped too many times");
        }
        
    }
    
    
    /**
     * Process notification of ignorable whitespace received from the body of
     * an XML element.
     *
     * @param buffer The characters from the XML document
     * @param start Starting offset into the buffer
     * @param length Number of characters from the buffer
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void ignorableWhitespace(
        char buffer[], int start, int len
    ) throws SAXException {
        ;	// No processing required
        
    }
    
    
    /**
     * Process notification of a processing instruction that was encountered.
     *
     * @param target The processing instruction target
     * @param data The processing instruction data (if any)
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void processingInstruction(String target, String data)
    throws SAXException {
        ;	// No processing is required
    }
    
    
    /**
     * Set the document locator associated with our parser.
     *
     * @param locator The new locator
     */
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }
    
    
    /**
     * Process notification of a skipped entity.
     *
     * @param name Name of the skipped entity
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void skippedEntity(String name) throws SAXException {
        ; // No processing required
    }
    
    
    /**
     * Process notification that a namespace prefix is coming in to scope.
     *
     * @param prefix Prefix that is being declared
     * @param namespaceURI Corresponding namespace URI being mapped to
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void startPrefixMapping(String prefix, String namespaceURI)
    throws SAXException {
        
        // Register this prefix mapping
        ArrayStack stack = (ArrayStack) namespaces.get(prefix);
        if (stack == null) {
            stack = new ArrayStack();
            namespaces.put(prefix, stack);
        }
        stack.push(namespaceURI);
    }
    
    
    // DTDHandler interface
    //-------------------------------------------------------------------------
    
    
    /**
     * Receive notification of a notation declaration event.
     *
     * @param name The notation name
     * @param publicId The public identifier (if any)
     * @param systemId The system identifier (if any)
     */
    public void notationDecl(String name, String publicId, String systemId) {
    }
    
    
    /**
     * Receive notification of an unparsed entity declaration event.
     *
     * @param name The unparsed entity name
     * @param publicId The public identifier (if any)
     * @param systemId The system identifier (if any)
     * @param notation The name of the associated notation
     */
    public void unparsedEntityDecl(
        String name, String publicId, String systemId, String notation
    ) {
    }
    
    // ErrorHandler interface
    //-------------------------------------------------------------------------
    
    
    /**
     * Forward notification of a parsing error to the application supplied
     * error handler (if any).
     *
     * @param exception The error information
     *
     * @exception SAXException if a parsing exception occurs
     */
    public void error(SAXParseException exception) throws SAXException {
        
        log.error("Parse Error at line " + exception.getLineNumber() +
        " column " + exception.getColumnNumber() + ": " +
        exception.getMessage(), exception);
        if (errorHandler != null) {
            errorHandler.error(exception);
        }
        
    }
    
    
    /**
     * Forward notification of a fatal parsing error to the application
     * supplied error handler (if any).
     *
     * @param exception The fatal error information
     *
     * @exception SAXException if a parsing exception occurs
     */
    public void fatalError(SAXParseException exception) throws SAXException {
        
        log.error("Parse Fatal Error at line " + exception.getLineNumber() +
        " column " + exception.getColumnNumber() + ": " +
        exception.getMessage(), exception);
        if (errorHandler != null) {
            errorHandler.fatalError(exception);
        }
        
    }
    
    
    /**
     * Forward notification of a parse warning to the application supplied
     * error handler (if any).
     *
     * @param exception The warning information
     *
     * @exception SAXException if a parsing exception occurs
     */
    public void warning(SAXParseException exception) throws SAXException {
        
        log.error("Parse Warning at line " + exception.getLineNumber() +
        " column " + exception.getColumnNumber() + ": " +
        exception.getMessage(), exception);
        if (errorHandler != null) {
            errorHandler.warning(exception);
        }
        
    }
    
    
    
    
    
    
    
    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * If this object has not been configured then register the default
     * namespaces
     */
    private void ensureConfigured() {
        if ( ! configured ) {
            configure();
            configured = true;
        }
    }
    
    /**
     * This method is called only once before parsing occurs
     * which allows tag libraries to be registered and so forth
     */
    protected void configure() {
        // load the properties file of libraries available
        InputStream in = null;
        URL url = getClassLoader().getResource( "org/apache/commons/jelly/jelly.properties" );
        if ( url != null ) {
            log.debug( "Loading Jelly default tag libraries from: " + url );
            
            Properties properties = new Properties();
            try {
                in = url.openStream();
                properties.load( in );
                for ( Iterator iter = properties.entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String uri = (String) entry.getKey();
                    String className = (String) entry.getValue();
                    context.registerTagLibrary( "jelly:" + uri, className, getClassLoader() );
                }
            }
            catch (IOException e) {
                log.error( "Could not load jelly properties from: " + url + ". Reason: " + e, e );
            }
            finally {
                try {
                    in.close();
                }
                catch (Exception e) {
                    // ignore
                }
            }
        }
    }
        
    
    /**
     * Factory method to create new Tag script for the given namespaceURI and name or
     * return null if this is not a custom Tag.
     */
    protected TagScript createTag( String namespaceURI, String localName, Attributes list ) throws SAXException {
        try {
            // use the URI to load a taglib
            TagLibrary taglib = context.getTagLibrary( namespaceURI );
            if ( taglib == null ) {
                if ( namespaceURI != null && namespaceURI.startsWith( "jelly:" ) ) {
                    String uri = namespaceURI.substring(6);
                    // try to find the class on the claspath
                    try {
                        Class taglibClass = getClassLoader().loadClass( uri );
                        taglib = (TagLibrary) taglibClass.newInstance();
                        
                        context.registerTagLibrary( namespaceURI, taglib );
                    }
                    catch (ClassNotFoundException e) {
                        log.warn( "Could not load class: " + uri + " so disabling the taglib" );
                    }
                }
            }
            if ( taglib != null ) {
                TagScript script = taglib.createTagScript( localName, list );
                
                // now iterate through through the expressions
                int size = list.getLength();
                for ( int i = 0; i < size; i++ ) {
                    String attributeName = list.getLocalName(i);
                    String attributeValue = list.getValue(i);
                    Expression expression = taglib.createExpression( getExpressionFactory(), localName, attributeName, attributeValue );
                    if ( expression == null ) {
                        expression = createConstantExpression( localName, attributeName, attributeValue );
                    }
                    script.addAttribute( attributeName, expression );
                }
                return script;
                
            }
            return null;
        }
        catch (Exception e) {
            log.warn( "Could not create taglib or URI: " + namespaceURI + " tag name: " + localName, e );
            throw createSAXException(e);
        }
        catch (Throwable e) {
            log.warn( "Could not create taglib or URI: " + namespaceURI + " tag name: " + localName, e );
            return null;
        }
    }
    
    /**
     * Factory method to create a static Tag that represents some static content.
     */
    protected TagScript createStaticTag( String namespaceURI, String localName, String qName, Attributes list ) throws SAXException {
        try {
            StaticTag tag = new StaticTag( namespaceURI, localName, qName );
            
            DynaTagScript script = new DynaTagScript( tag );
                
            // now iterate through through the expressions
            int size = list.getLength();
            for ( int i = 0; i < size; i++ ) {
                String attributeName = list.getLocalName(i);
                String attributeValue = list.getValue(i);
                Expression expression = getExpressionFactory().createExpression( attributeValue );
                if ( expression == null ) {
                    expression = createConstantExpression( localName, attributeName, attributeValue );
                }
                script.addAttribute( attributeName, expression );
            }
            return script;
        }
        catch (Exception e) {
            log.warn( "Could not create static tag for URI: " + namespaceURI + " tag name: " + localName, e );
            throw createSAXException(e);
        }
    }
    
    protected Expression createConstantExpression( String tagName, String attributeName, String attributeValue ) throws Exception {
        return new ConstantExpression( attributeValue );
    }
        
    protected ExpressionFactory createExpressionFactory() {
        return new JexlExpressionFactory();
    }
    
    
    /**
     * Create a SAX exception which also understands about the location in
     * the file where the exception occurs
     *
     * @return the new exception
     */
    protected SAXException createSAXException(String message, Exception e) {
        log.warn( "Underlying exception: " + e );
        e.printStackTrace();
        if (locator != null) {
            String error = "Error at (" + locator.getLineNumber() + ", "
            + locator.getColumnNumber() + ": " + message;
            if (e != null) {
                return new SAXParseException(error, locator, e);
            } 
            else {
                return new SAXParseException(error, locator);
            }
        }
        log.error("No Locator!");
        if (e != null) {
            return new SAXException(message, e);
        } 
        else {
            return new SAXException(message);
        }
    }
    
    /**
     * Create a SAX exception which also understands about the location in
     * the digester file where the exception occurs
     *
     * @return the new exception
     */
    protected SAXException createSAXException(Exception e) {
        return createSAXException(e.getMessage(), e);
    }
    
    /**
     * Create a SAX exception which also understands about the location in
     * the digester file where the exception occurs
     *
     * @return the new exception
     */
    protected SAXException createSAXException(String message) {
        return createSAXException(message, null);
    }
}
