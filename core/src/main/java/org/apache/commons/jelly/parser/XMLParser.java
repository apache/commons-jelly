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
package org.apache.commons.jelly.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.expression.CompositeExpression;
import org.apache.commons.jelly.expression.ConstantExpression;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.expression.ExpressionFactory;
import org.apache.commons.jelly.expression.jexl.JexlExpressionFactory;
import org.apache.commons.jelly.impl.CompositeTextScriptBlock;
import org.apache.commons.jelly.impl.ExpressionScript;
import org.apache.commons.jelly.impl.ScriptBlock;
import org.apache.commons.jelly.impl.StaticTag;
import org.apache.commons.jelly.impl.StaticTagScript;
import org.apache.commons.jelly.impl.TagScript;
import org.apache.commons.jelly.impl.TextScript;
import org.apache.commons.jelly.util.ClassLoaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

/** <p><code>XMLParser</code> parses the XML Jelly format.
 * The SAXParser and XMLReader portions of this code come from Digester.</p>
 */
public class XMLParser extends DefaultHandler {

    /**
     * Share the Jelly properties across parsers
     */
    private static Properties jellyProperties;

    /**
     * The SAXParserFactory that is created the first time we need it.
     */
    protected static SAXParserFactory factory = null;

    /** JellyContext which is used to locate tag libraries*/
    private JellyContext context = new JellyContext();

    /** The expression factory used to evaluate tag attributes */
    private ExpressionFactory expressionFactory;

    /** The current script block */
    private ScriptBlock script;

    /** The current, parent tagScript */
    private TagScript tagScript;

    /** The stack of body scripts. */
    private final ArrayDeque scriptStack = new ArrayDeque();

    /** The stack of tagScripts - use ArrayList as it allows null. */
    private final ArrayList tagScriptStack = new ArrayList();

    /** The current text buffer where non-custom tags get written */
    private StringBuffer textBuffer;

    /** Do we allow our doctype definitions to call out to external entities? */
    private boolean allowDtdToCallExternalEntities = false;

    /**
     * The class loader to use for instantiating application objects.
     * If not specified, the context class loader, or the class loader
     * used to load XMLParser itself, is used, based on the value of the
     * <code>useContextClassLoader</code> variable.
     */
    protected ClassLoader classLoader = null;

    /**
     * Do we want to use the Context ClassLoader when loading classes
     * for instantiating new objects?  Default is {@code false}.
     */
    protected boolean useContextClassLoader = false;

    /**
     * The application-supplied error handler that is notified when parsing
     * warnings, errors, or fatal errors occur.
     */
    protected ErrorHandler errorHandler = null;

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
    protected Map namespaces = new HashMap();

    /** The Map of the namespace prefix -&gt; URIs defined for the current element */
    private Map elementNamespaces;

    /**
     * The name of the file being parsed that is passed to the TagScript objects
     * for error reporting
     */
    private String fileName;

    /**
     * Do we want to use a validating parser?
     */
    protected boolean validating = false;

    /** Flag to indicate if this object has been configured */
    private boolean configured;

    /**
     * when not null, set the default namespace for
     * unprefixed elements via the DefaultNamespaceFilter
     * class
     */
    private String defaultNamespaceURI = null;

    /**
     * The Log to which logging calls will be made.
     */
    private Log log = LogFactory.getLog(XMLParser.class);

    /**
     * Constructs  a new XMLParser with default properties.
     */
    public XMLParser() {
    }

    /**
    * Constructs s a new XMLParser, with the boolean
     * allowDtdToCallExternalEntities being passed in. If this is set to false,
     * the XMLParser will be created with:
     * XMLReader spf = XMLReaderFactory.createXMLReader();
     * spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
     * spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
     * spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",false);
     * as given by
     * https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#XMLReader
     */
    public XMLParser(final boolean allowDtdToCallExternalEntities) {
        this.allowDtdToCallExternalEntities = allowDtdToCallExternalEntities;
    }

    /**
   * Constructs ts a new XMLParser, allowing a SAXParser to be passed in.  This
     * allows XMLParser to be used in environments which are unfriendly to
     * JAXP1.1 (such as WebLogic 6.0).  Thanks for the request to change go to
     * James House (james@interobjective.com).  This may help in places where
     * you are able to load JAXP 1.1 classes yourself.
     */
    public XMLParser(final SAXParser parser) {
        this.parser = parser;
    }

    /**
  * Constructs cts a new XMLParser, allowing an XMLReader to be passed in.  This
     * allows XMLParser to be used in environments which are unfriendly to
     * JAXP1.1 (such as WebLogic 6.0).  Note that if you use this option you
     * have to configure namespace and validation support yourself, as these
     * properties only affect the SAXParser and empty constructor.
     */
    public XMLParser(final XMLReader reader) {
        this.reader = reader;
    }

    /**
     * Adds the given Expression object to the current Script.
     */
    protected void addExpressionScript(final ScriptBlock script, final Expression expression) {
        if ( expression instanceof ConstantExpression ) {
            final ConstantExpression constantExpression
                = (ConstantExpression) expression;
            final Object value = constantExpression.getValue();
            if ( value != null ) {
                script.addScript(new TextScript( value.toString() ));
            }
        }
        else
        if ( expression instanceof CompositeExpression ) {
            final CompositeTextScriptBlock newBlock = new CompositeTextScriptBlock();
            script.addScript(newBlock);

            final CompositeExpression compositeExpression
                = (CompositeExpression) expression;
            for (Object o : compositeExpression.getExpressions()) {
                addExpressionScript(newBlock, (Expression) o);
            }
        }
        else {
            script.addScript(new ExpressionScript(expression));
        }
    }

    /**
     * Adds the text to the current script block parsing any embedded
     * expressions into ExpressionScript objects.
     */
    protected void addTextScript(final String text) throws JellyException {
    	final Expression expression = createExpression(tagScript, "", text);
        addExpressionScript(script, expression);
    }

    /**
     * Process notification of character data received from the body of
     * an XML element.
     *
     * @param buffer The characters from the XML document
     * @param start Starting offset into the buffer
     * @param length Number of characters from the buffer
     * @throws SAXException if a parsing error is to be reported
     */
    @Override
    public void characters(final char buffer[], final int start, final int length)
        throws SAXException {
        textBuffer.append(buffer, start, length);
    }

    /**
     * This method is called only once before parsing occurs
     * which allows tag libraries to be registered and so forth
     */
    protected void configure() {
        // load the properties file of libraries available
        final Properties properties = getJellyProperties();
        for (final Iterator iter = properties.entrySet().iterator(); iter.hasNext();) {
            final Map.Entry entry = (Map.Entry) iter.next();
            final String uri = (String) entry.getKey();
            final String className = (String) entry.getValue();
            final String libraryURI = "jelly:" + uri;

            // don't overload any Mock Tags already
            if ( ! context.isTagLibraryRegistered(libraryURI) ) {
                context.registerTagLibrary(libraryURI, className);
            }
        }
    }

    /**
     * Configure a newly created TagScript instance before any Expressions are created;
     * see configureTagScript(TagScript) for StaticTags
     * @param tagLibrary the TagLibrary that created the TagScript
     * @param aTagScript the TagScript that's just been created
     */
    protected void configureTagScript(final TagLibrary tagLibrary,  final TagScript aTagScript) {
        // Set the TagLibrary that created the script
        aTagScript.setTagLibrary(tagLibrary);

        configureTagScript(aTagScript);
    }

    /**
     * Configure a newly created TagScript instance before any Expressions are created
     * @param aTagScript
     */
    protected void configureTagScript(final TagScript aTagScript) {
        // set parent relationship...
        aTagScript.setParent(this.tagScript);

        // set the namespace Map
        if ( elementNamespaces != null ) {
            aTagScript.setTagNamespacesMap( elementNamespaces );
            elementNamespaces = null;
        }
    }

    protected Expression createConstantExpression(
        final String tagName,
        final String attributeName,
        final String attributeValue)  {
        return new ConstantExpression(attributeValue);
    }

    /**
     * Creates an expression, using the ExpressionFactory returned by getExpressionFactory();
     * the default implementation defers to the TagLibrary to create the Expression
     * @param attributeName
     * @param value
     * @return The parsed exception.
     * @throws JellyException
     */
    public Expression createExpression(final TagScript script, final String attributeName, final String value) throws JellyException {
    	final ExpressionFactory factory = getExpressionFactory(script);
    	final TagLibrary tagLibrary = script.getTagLibrary();
    	if (tagLibrary != null) {
            return tagLibrary.createExpression(factory, script, attributeName, value);
        }
       	return CompositeExpression.parse(value, factory);
    }

    /**
     * Called to create an instance of the ExpressionFactory (@see getExpressionFactory)
     *
     * @return A new JexlExpressionFactory.
     */
    protected ExpressionFactory createExpressionFactory() {
        return new JexlExpressionFactory();
    }

    /**
     * Create a SAX exception which also understands about the location in
     * the digester file where the exception occurs
     *
     * @return the new exception
     */
    protected SAXException createSAXException(final Exception e) {
        return createSAXException(e.getMessage(), e);
    }

    /**
     * Create a SAX exception which also understands about the location in
     * the digester file where the exception occurs
     *
     * @return the new exception
     */
    protected SAXException createSAXException(final String message) {
        return createSAXException(message, null);
    }

    /**
     * Create a SAX exception which also understands about the location in
     * the file where the exception occurs
     *
     * @return the new exception
     */
    protected SAXException createSAXException(final String message, final Exception e) {
        log.warn("Underlying exception: " + e);
        if (e != null) {
            e.printStackTrace();
        }
        if (locator != null) {
            final String error =
                "Error at ("
                    + locator.getLineNumber()
                    + ", "
                    + locator.getColumnNumber()
                    + "): "
                    + message;
            if (e != null) {
                return new SAXParseException(error, locator, e);
            }
            return new SAXParseException(error, locator);
        }
        log.error("No Locator!");
        if (e != null) {
            return new SAXException(message, e);
        }
        return new SAXException(message);
    }

    /**
     * Factory method to create a static Tag that represents some static content.
     */
    protected TagScript createStaticTag(
        final String namespaceURI,
        final String localName,
        final String qName,
        final Attributes list)
        throws SAXException {
        try {
            //StaticTag tag = new StaticTag( namespaceURI, localName, qName );
            final StaticTagScript script = new StaticTagScript(
                (name, attributes) -> new StaticTag( namespaceURI, localName, qName )
            );
            configureTagScript(script);

            // now iterate through through the expressions
            final int size = list.getLength();
            for (int i = 0; i < size; i++) {
                final String attributeValue = list.getValue(i);
                final Expression expression = createExpression(script, null, attributeValue);
//                Expression expression = CompositeExpression.parse(
//                        attributeValue, getExpressionFactory()
//                    );
                final String attrQName = list.getQName(i);
                final int p = attrQName.indexOf(':');
                final String prefix = p>=0 ?
                        attrQName.substring(0, p):
                        "";
                script.addAttribute(list.getLocalName(i),
                        prefix, list.getURI(i), expression);
            }
            return script;
        }
        catch (final Exception e) {
            log.warn(
                "Could not create static tag for URI: "
                    + namespaceURI
                    + " tag name: "
                    + localName,
                e);
            throw createSAXException(e);
        }
    }

    /**
     * Factory method to create new Tag script for the given namespaceURI and name or
     * return null if this is not a custom Tag.
     */
    protected TagScript createTag(
        final String namespaceURI,
        final String localName,
        final Attributes list)
        throws SAXException {
        try {
            // use the URI to load a taglib
            TagLibrary taglib = context.getTagLibrary(namespaceURI);
            if (taglib == null && namespaceURI != null && namespaceURI.startsWith("jelly:")) {
                final String uri = namespaceURI.substring(6);
                // try to find the class on the classpath
                try {
                    final Class taglibClass = getClassLoader().loadClass(uri);
                    taglib = (TagLibrary) taglibClass.getConstructor().newInstance();
                    context.registerTagLibrary(namespaceURI, taglib);
                }
                catch (final ClassNotFoundException e) {
                    throw createSAXException("Could not load class: " + uri + " so taglib instantiation failed", e);
                }
                catch (final IllegalAccessException e) {
                    throw createSAXException("Constructor for class is not accessible: " + uri + " so taglib instantiation failed", e);
                }
                catch (final InstantiationException e) {
                    throw createSAXException("Class could not be instantiated: " + uri + " so taglib instantiation failed", e);
                }
                catch (final ClassCastException e) {
                    throw createSAXException("Class is not a TagLibrary: " + uri + " so taglib instantiation failed", e);
                }
            }
            if (taglib != null) {
                final TagScript script = taglib.createTagScript(localName, list);
                if ( script != null ) {
                    configureTagScript(taglib, script);

                    // clone the attributes to keep them around after this parse
                    script.setSaxAttributes(new AttributesImpl(list));

                    // now iterate through through the expressions
                    final int size = list.getLength();
                    for (int i = 0; i < size; i++) {
                        final String attributeName = list.getLocalName(i);
                        // Fix for JELLY-184 where the xmlns attributes have a blank name and cause
                        //	an exception later on
                        if (attributeName.length() == 0) {
                            continue;
                        }
                        final String attributeValue = list.getValue(i);
                        Expression expression =
                            createExpression(script,
                                attributeName,
                                attributeValue);
//                        Expression expression =
//                            taglib.createExpression(
//                                getExpressionFactory(),
//                                script,
//                                attributeName,
//                                attributeValue);
                        if (expression == null) {
                            expression = createConstantExpression(localName, attributeName, attributeValue);
                        }
                        script.addAttribute(attributeName, expression);
                    }
                } else if (!taglib.isAllowUnknownTags()) {
                    throw new JellyException("Unrecognized tag called " + localName + " in TagLibrary " + namespaceURI);
                }
                return script;
            }
            return null;
        }
        catch (final Exception e) {
            log.warn(
                "Could not create taglib or URI: " + namespaceURI + " tag name: " + localName,
                e);
            throw createSAXException(e);
        }
    }

    /**
     * Process notification of the end of the document being reached.
     *
     * @throws SAXException if a parsing error is to be reported
     */
    @Override
    public void endDocument() throws SAXException {
        textBuffer = null;
    }

    /**
     * Process notification of the end of an XML element being reached.
     *
     * @param namespaceURI The Namespace URI, or the empty string if the
     *   element has no Namespace URI or if Namespace processing is not
     *   being performed.
     * @param localName The local name (without prefix), or the empty
     *   string if Namespace processing is not being performed.
     * @param qName The qualified XML 1.0 name (with prefix), or the
     *   empty string if qualified names are not available.
     * @throws SAXException if a parsing error is to be reported
     */
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName)
        throws SAXException {
        try {
            tagScript = (TagScript) tagScriptStack.remove(tagScriptStack.size() - 1);
            if (tagScript != null) {
                if (textBuffer.length() > 0) {
                    addTextScript(textBuffer.toString());
                    textBuffer.setLength(0);
                }
                script = (ScriptBlock) scriptStack.pop();
            }
            else {
                textBuffer.append("</");
                textBuffer.append(qName);
                textBuffer.append(">");
            }

            // now lets set the parent tag variable
            if ( tagScriptStack.isEmpty() ) {
                tagScript = null;
            }
            else {
                tagScript = (TagScript) tagScriptStack.get(tagScriptStack.size() - 1);
            }
        } catch (final Exception e) {
            log.error( "Caught exception: " + e, e );
            throw new SAXException( "Runtime Exception: " + e, e );
        }
    }

    /**
     * Process notification that a namespace prefix is going out of scope.
     *
     * @param prefix Prefix that is going out of scope
     * @throws SAXException if a parsing error is to be reported
     */
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        // Deregister this prefix mapping
        final ArrayDeque stack = (ArrayDeque) namespaces.get(prefix);
        if (stack == null) {
            return;
        }
        try {
            stack.pop();
            if (stack.isEmpty()) {
                namespaces.remove(prefix);
            }
        }
        catch (final EmptyStackException e) {
            throw createSAXException("endPrefixMapping popped too many times");
        }
    }

    // Implementation methods
    //-------------------------------------------------------------------------
    /**
     * If this object has not been configured then register the default
     * namespaces
     */
    private void ensureConfigured() {
        if (!configured) {
            configure();
            configured = true;
        }
    }

    /**
     * Forward notification of a parsing error to the application supplied
     * error handler, if any, otherwise throw a SAXException with the error.
     *
     * @param exception The error information
     * @throws SAXException if a parsing exception occurs
     */
    @Override
    public void error(final SAXParseException exception) throws SAXException {
        log.error(
            "Parse Error at line "
                + exception.getLineNumber()
                + " column "
                + exception.getColumnNumber()
                + ": "
                + exception.getMessage(),
            exception);
        if (errorHandler == null) {
            throw exception;
        }
        errorHandler.error(exception);
    }

    /**
     * Forward notification of a fatal parsing error to the application
     * supplied error handler, if any, otherwise throw a SAXException with the error.
     *
     * @param exception The fatal error information
     * @throws SAXException if a parsing exception occurs
     */
    @Override
    public void fatalError(final SAXParseException exception) throws SAXException {
        log.error(
            "Parse Fatal Error at line "
                + exception.getLineNumber()
                + " column "
                + exception.getColumnNumber()
                + ": "
                + exception.getMessage(),
            exception);
        if (errorHandler == null) {
            throw exception;
        }
        errorHandler.fatalError(exception);
    }

    /**
     * Return the currently mapped namespace URI for the specified prefix,
     * if any; otherwise return {@code null}.  These mappings come and
     * go dynamically as the document is parsed.
     *
     * @param prefix Prefix to look up
     * @return The matching prefix.
     */
    public String findNamespaceURI(final String prefix) {
        final ArrayDeque stack = (ArrayDeque) namespaces.get(prefix);
        if (stack == null) {
            return null;
        }
        try {
            return (String) stack.peek();
        }
        catch (final EmptyStackException e) {
            return null;
        }
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
        return ClassLoaderUtils.getClassLoader(classLoader, useContextClassLoader, getClass());
    }

    public JellyContext getContext() {
        return context;
    }

    /**
     * @return the current context URI as a String or null if there is no
     * current context defined on the JellyContext
     */
    protected String getCurrentURI() {
        final URL url = this.getContext().getCurrentURL();
        return url != null ? url.toString() : null;
    }

    /**
     * Return the error handler for this XMLParser.
     */
    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    /**
     * Locates an expression factory by looking at the TagLibrary for the specified
     * tag and falling back by creating one.  If the current tag is a StaticTag (IE
     * has no TagLibrary) then continues up the stack looking for a valid one.
     * @param tagScript
     * @return the expression factory used to evaluate tag attributes
     */
    public ExpressionFactory getExpressionFactory(final TagScript tagScript) {
    	// Check the tag library
    	TagLibrary tagLibrary = null;
    	if (tagScript != null) {
            tagLibrary = tagScript.getTagLibrary();
        }

    	// If the tagScript is a StaticTag, then go up the stack looking for a
    	//	tagScript that belongs to a real TagLibrary
    	if (tagLibrary == null && tagScript instanceof StaticTagScript) {
            for (int i = tagScriptStack.size() - 1; i > -1; i--) {
    			final TagScript script = (TagScript)tagScriptStack.get(i);
    			tagLibrary = script.getTagLibrary();
    			if (tagLibrary != null) {
                    break;
                }
    			if (!(script instanceof StaticTagScript)) {
                    break;
                }
    		}
        }
    	if (tagLibrary != null) {
    		final ExpressionFactory factory = tagLibrary.getExpressionFactory();
    		if (factory != null) {
                return factory;
            }
    	}
        if (expressionFactory == null) {
            expressionFactory = createExpressionFactory();
        }
        return expressionFactory;
    }

    /**
     * A helper method which loads the static Jelly properties once on startup
     */
    protected synchronized Properties getJellyProperties() {
        if (jellyProperties == null) {
            jellyProperties = new Properties();

            InputStream in = null;
            final URL url =
                getClassLoader().getResource("org/apache/commons/jelly/jelly.properties");
            if (url != null) {
                log.debug("Loading Jelly default tag libraries from: " + url);
                try {
                    in = url.openStream();
                    jellyProperties .load(in);
                }
                catch (final IOException e) {
                    log.error("Could not load jelly properties from: " + url + ". Reason: " + e, e);
                }
                finally {
                    try {
                        in.close();
                    }
                    catch (final Exception e) {
                        if (log.isDebugEnabled()) {
                            log.debug("error closing jelly.properties", e);
                        }
                    }
                }
            }
        }
        return jellyProperties;
    }

    /**
     * Return the current Logger associated with this instance of the XMLParser
     */
    public Log getLogger() {
        return log;
    }

    /**
     * Return the SAXParser we will use to parse the input stream.  If there
     * is a problem creating the parser, return {@code null}.
     */
    public SAXParser getParser() {
        // Return the parser we already created (if any)
        if (parser != null) {
            return parser;
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
                return parser;
            }
            catch (final Exception e) {
                log.error("XMLParser.getParser: ", e);
                return null;
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
    @Deprecated
    public XMLReader getReader() {
        try {
            return getXMLReader();
        }
        catch (final SAXException e) {
            log.error("Cannot get XMLReader", e);
            return null;
        }
    }

    /**
     * Returns the script that has just been created if this class is used
     * as a SAX ContentHandler and passed into some XML processor or parser.
     *
     * @return the ScriptBlock created if SAX events are piped into this class,
     * which must include a startDocument() and endDocument()
     */
    public ScriptBlock getScript() {
        return script;
    }

    /**
     * Return the boolean as to whether the context classloader should be used.
     */
    public boolean getUseContextClassLoader() {
        return useContextClassLoader;
    }

    /**
     * Return the validating parser flag.
     */
    public boolean getValidating() {
        return this.validating;
    }

    /**
     * Return the XMLReader to be used for parsing the input document.
     *
     * @throws SAXException if no XMLReader can be instantiated
     */
    public synchronized XMLReader getXMLReader() throws SAXException {
        if (reader == null) {
            reader = getParser().getXMLReader();
            if (!allowDtdToCallExternalEntities) {
                reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
                reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            }
            if (this.defaultNamespaceURI != null) {
                reader = new DefaultNamespaceFilter(this.defaultNamespaceURI, reader);
            }
        }
        //set up the parse
        reader.setContentHandler(this);
        reader.setDTDHandler(this);
        //reader.setEntityResolver(this);
        reader.setErrorHandler(this);

        return reader;
    }

    /**
     * Process notification of ignorable whitespace received from the body of
     * an XML element.
     *
     * @param buffer The characters from the XML document
     * @param start Starting offset into the buffer
     * @param len Number of characters from the buffer
     * @throws SAXException if a parsing error is to be reported
     */
    @Override
    public void ignorableWhitespace(final char buffer[], final int start, final int len)
        throws SAXException {
        // No processing required
    }

    /**
     * Gets the internal state for allowance for DTDs to call external resources.
     *
     * @return true if we are allowed to make external calls to entities during XML
     * parsing by custom declared resources in the DTD.
     */
    public boolean isAllowDtdToCallExternalEntities() {
        return allowDtdToCallExternalEntities;
    }

    /**
     * Receive notification of a notation declaration event.
     *
     * @param name The notation name
     * @param publicId The public identifier (if any)
     * @param systemId The system identifier (if any)
     */
    @Override
    public void notationDecl(final String name, final String publicId, final String systemId) {
    }

    /**
     * Parse the content of the specified file using this XMLParser.  Returns
     * the root element from the object stack (if any).
     *
     * @param file File containing the XML data to be parsed
     * @return The script.
     * @throws IOException if an input/output error occurs
     * @throws SAXException if a parsing exception occurs
     */
    public Script parse(final File file) throws IOException, SAXException {
        return parse(file.toURI().toURL());
    }

    /**
     * Parse the content of the specified input source using this XMLParser.
     * Returns the root element from the object stack (if any).
     *
     * @param input Input source containing the XML data to be parsed
     * @return The script.
     * @throws IOException if an input/output error occurs
     * @throws SAXException if a parsing exception occurs
     */
    public Script parse(final InputSource input) throws IOException, SAXException {
        ensureConfigured();
        this.fileName = input.getSystemId();
        getXMLReader().parse(input);
        return script;
    }

    /**
     * Parse the content of the specified input stream using this XMLParser.
     * Returns the root element from the object stack (if any).
     * (Note: if reading a File or URL, use one of the URL-based
     * parse methods instead.  This method will not be able
     * to resolve any relative paths inside a DTD.)
     *
     * @param input  Input stream containing the XML data to be parsed
     * @return The script.
     * @throws IOException
     *                   if an input/output error occurs
     * @throws SAXException
     *                   if a parsing exception occurs
     */
    public Script parse(final InputStream input) throws IOException, SAXException {
        ensureConfigured();
        this.fileName = getCurrentURI();
        getXMLReader().parse(new InputSource(input));
        return script;
    }

    /**
     * Parse the content of the specified reader using this XMLParser.
     * Returns the root element from the object stack (if any).
     * (Note: if reading a File or URL, use one of the URL-based
     * parse methods instead.  This method will not be able
     * to resolve any relative paths inside a DTD.)
     *
     * @param reader Reader containing the XML data to be parsed
     * @return The script.
     * @throws IOException
     *                   if an input/output error occurs
     * @throws SAXException
     *                   if a parsing exception occurs
     */
    public Script parse(final Reader reader) throws IOException, SAXException {
        ensureConfigured();
        this.fileName = getCurrentURI();
        getXMLReader().parse(new InputSource(reader));
        return script;
    }

    /**
     * Parse the content of the specified URI using this XMLParser.
     * Returns the root element from the object stack (if any).
     *
     * @param uri URI containing the XML data to be parsed
     * @return The script.
     * @throws IOException if an input/output error occurs
     * @throws SAXException if a parsing exception occurs
     */
    public Script parse(final String uri) throws IOException, SAXException {
        ensureConfigured();
        this.fileName = uri;
        getXMLReader().parse(uri);
        return script;
    }

    /**
     * Parse the content of the specified file using this XMLParser.  Returns
     * the root element from the object stack (if any).
     *
     * @param url URL containing the XML data to be parsed
     * @return The script.
     * @throws IOException if an input/output error occurs
     * @throws SAXException if a parsing exception occurs
     */
    public Script parse(final URL url) throws IOException, SAXException {
        ensureConfigured();
        this.fileName = url.toString();

        final InputSource source = new InputSource(url.toString());

        getXMLReader().parse(source);
        return script;
    }

    /**
     * Process notification of a processing instruction that was encountered.
     *
     * @param target The processing instruction target
     * @param data The processing instruction data (if any)
     * @throws SAXException if a parsing error is to be reported
     */
    @Override
    public void processingInstruction(final String target, final String data)
        throws SAXException {
        // No processing is required
    }

    /**
     * Sets the boolean
     * allowDtdToCallExternalEntities. If this is set to false,
     * the XMLParser will be created with:
     * XMLReader spf = XMLReaderFactory.createXMLReader();
     * spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
     * spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
     * spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",false);
     * as given by
     * https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#XMLReader
     */
    public void setAllowDtdToCallExternalEntities(final boolean allowDtdToCallExternalEntities) {
        this.allowDtdToCallExternalEntities = allowDtdToCallExternalEntities;
    }

    /**
     * Sets the class loader to be used for instantiating application objects
     * when required.
     *
     * @param classLoader The new class loader to use, or {@code null}
     *  to revert to the standard rules
     */
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setContext(final JellyContext context) {
        this.context = context;
    }

    /**
     * Sets the jelly namespace to use for unprefixed elements.
     * Will be overridden by an explicit namespace in the
     * XML document.
     *
     * @param namespace jelly namespace to use (e.g. 'jelly:core')
     */
    public void setDefaultNamespaceURI(final String namespace) {
        this.defaultNamespaceURI = namespace;
    }

    /**
     * Sets the document locator associated with our parser.
     *
     * @param locator The new locator
     */
    @Override
    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;
    }

    /**
     * Sets the error handler for this XMLParser.
     *
     * @param errorHandler The new error handler
     */
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /** Sets the expression factory used to evaluate tag attributes */
    public void setExpressionFactory(final ExpressionFactory expressionFactory) {
        this.expressionFactory = expressionFactory;
    }

    /**
     * Sets the current logger for this XMLParser.
     */
    public void setLogger(final Log log) {
        this.log = log;
    }

    /**
     * Determine whether to use the Context ClassLoader (the one found by
     * calling <code>Thread.currentThread().getContextClassLoader()</code>)
     * to resolve/load classes.  If not
     * using Context ClassLoader, then the class-loading defaults to
     * using the calling-class' ClassLoader.
     *
     * @param use determines whether to use JellyContext ClassLoader.
     */
    public void setUseContextClassLoader(final boolean use) {
        useContextClassLoader = use;
    }

    /**
     * Sets the validating parser flag.  This must be called before
     * <code>parse()</code> is called the first time.
     *
     * @param validating The new validating parser flag.
     */
    public void setValidating(final boolean validating) {
        this.validating = validating;
    }

    /**
     * Process notification of a skipped entity.
     *
     * @param name Name of the skipped entity
     * @throws SAXException if a parsing error is to be reported
     */
    @Override
    public void skippedEntity(final String name) throws SAXException {
        // No processing required
    }

    // ContentHandler interface
    //-------------------------------------------------------------------------
    /**
     * Process notification of the beginning of the document being reached.
     *
     * @throws SAXException if a parsing error is to be reported
     */
    @Override
    public void startDocument() throws SAXException {
        script = new ScriptBlock();
        textBuffer = new StringBuffer();
        tagScript = null;
        scriptStack.clear();
        tagScriptStack.clear();
    }

    /**
     * Process notification of the start of an XML element being reached.
     *
     * @param namespaceURI The Namespace URI, or the empty string if the
     *   element has no Namespace URI or if Namespace processing is not
     *   being performed.
     * @param localName The local name (without prefix), or the empty
     *   string if Namespace processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty
     *   string if qualified names are not available.\
     * @param list The attributes attached to the element. If there are
     *   no attributes, it shall be an empty Attributes object.
     * @throws SAXException if a parsing error is to be reported
     */
    @Override
    public void startElement(
        String namespaceURI,
        final String localName,
        final String qName,
        final Attributes list)
        throws SAXException {

        try {
            // add check to ensure namespace URI is "" for no namespace
            if ( namespaceURI == null ) {
                namespaceURI = "";
            }

            // if this is a tag then create a script to run it
            // otherwise pass the text to the current body
            TagScript newTagScript = createTag(namespaceURI, localName, list);
            if (newTagScript == null) {
                newTagScript = createStaticTag(namespaceURI, localName, qName, list);
            }
            tagScript = newTagScript;
            tagScriptStack.add(tagScript);
            if (tagScript != null) {
                // set the line number details
                if ( locator != null ) {
                    tagScript.setLocator(locator);
                }
                // sets the file name element names
                if (fileName != null) {
                    tagScript.setFileName(fileName);
                }
                tagScript.setElementName(qName);
                tagScript.setLocalName(localName);

                if (textBuffer.length() > 0) {
                    addTextScript(textBuffer.toString());
                    textBuffer.setLength(0);
                }
                script.addScript(tagScript);
                // start a new body
                scriptStack.push(script);
                script = new ScriptBlock();
                tagScript.setTagBody(script);
            }
            else {
                // XXXX: might wanna handle empty elements later...
                textBuffer.append("<");
                textBuffer.append(qName);
                final int size = list.getLength();
                for (int i = 0; i < size; i++) {
                    textBuffer.append(" ");
                    textBuffer.append(list.getQName(i));
                    textBuffer.append("=");
                    textBuffer.append("\"");
                    textBuffer.append(list.getValue(i));
                    textBuffer.append("\"");
                }
                textBuffer.append(">");
            }
        }
        catch (final SAXException e) {
            throw e;
        }
        catch (final Exception e) {
            log.error( "Caught exception: " + e, e );
            throw new SAXException( "Runtime Exception: " + e, e );
        }
    }

    /**
     * Process notification that a namespace prefix is coming in to scope.
     *
     * @param prefix Prefix that is being declared
     * @param namespaceURI Corresponding namespace URI being mapped to
     * @throws SAXException if a parsing error is to be reported
     */
    @Override
    public void startPrefixMapping(final String prefix, final String namespaceURI)
        throws SAXException {
        // Register this prefix mapping
        ArrayDeque stack = (ArrayDeque) namespaces.get(prefix);
        if (stack == null) {
            stack = new ArrayDeque();
            namespaces.put(prefix, stack);
        }
        stack.push(namespaceURI);

        if ( elementNamespaces == null ) {
            elementNamespaces = new HashMap();
        }
        elementNamespaces.put(prefix, namespaceURI);
    }

    /**
     * Receive notification of an unparsed entity declaration event.
     *
     * @param name The unparsed entity name
     * @param publicId The public identifier (if any)
     * @param systemId The system identifier (if any)
     * @param notation The name of the associated notation
     */
    @Override
    public void unparsedEntityDecl(
        final String name,
        final String publicId,
        final String systemId,
        final String notation) {
    }
    /**
     * Forward notification of a parse warning to the application supplied
     * error handler (if any).  Unlike XMLParser.error(SAXParseException) and
     * XMLParser.fatalError(SAXParseException), this implementation will
     * NOT throw a SAXException by default if no error handler is supplied.
     *
     * @param exception The warning information
     * @throws SAXException if a parsing exception occurs
     */
    @Override
    public void warning(final SAXParseException exception) throws SAXException {
        log.error(
            "Parse Warning at line "
                + exception.getLineNumber()
                + " column "
                + exception.getColumnNumber()
                + ": "
                + exception.getMessage(),
            exception);
        if (errorHandler != null) {
            errorHandler.warning(exception);
        }
    }
}
