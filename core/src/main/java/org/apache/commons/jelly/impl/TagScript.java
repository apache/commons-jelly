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
package org.apache.commons.jelly.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.beanutils2.ConvertUtils;
import org.apache.commons.beanutils2.ConvertingWrapDynaBean;
import org.apache.commons.beanutils2.DynaBean;
import org.apache.commons.beanutils2.DynaProperty;
import org.apache.commons.jelly.CompilableTag;
import org.apache.commons.jelly.DynaTag;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.LocationAware;
import org.apache.commons.jelly.NamespaceAwareTag;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

final class ExpressionAttribute {
    String name;
    String prefix;

    String nsURI;
    Expression exp;
    public ExpressionAttribute(final String name, final Expression exp) {
        this(name, "", "", exp);
    }
    public ExpressionAttribute(final String name, final String prefix, final String nsURI, final Expression exp) {
        this.name = name;
        this.prefix = prefix;
        this.nsURI = nsURI;
        this.exp = exp;
    }
}

/**
 * <p><code>TagScript</code> is a Script that evaluates a custom tag.</p>
 *
 * <strong>Note</strong> that this class should be re-entrant and used
 * concurrently by multiple threads.
 */
public class TagScript implements Script {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TagScript.class);

    /**
     * @return a new TagScript based on whether
     * the given Tag class is a bean tag or DynaTag
     */
    public static TagScript newInstance(final Class tagClass) {
        final TagFactory factory = new DefaultTagFactory(tagClass);
        return new TagScript(factory);
    }

    /** The attribute expressions that are created */
    protected Map attributes = new Hashtable();

    /** The optional namespaces Map of prefix to URI of this single Tag */
    private Map tagNamespacesMap;

    /**
     * The optional namespace context mapping all prefixes to URIs in scope
     * at the point this tag is used.
     * This Map is only created lazily if it is required by the NamespaceAwareTag.
     */
    private Map namespaceContext;

    /** The Jelly file which caused the problem */
    private String fileName;

    /** The qualified element name which caused the problem */
    private String elementName;

    /** The local (non-namespaced) tag name */
    private String localName;

    /** The line number of the tag */
    private int lineNumber = -1;

    /** The column number of the tag */
    private int columnNumber = -1;

    /** The TagLibrary that we belong to */
    private TagLibrary tagLibrary;

    /** The factory of Tag instances */
    private TagFactory tagFactory;

    /** The body script used for this tag */
    private Script tagBody;

    /** The parent TagScript */
    private TagScript parent;

    /** The SAX attributes */
    private Attributes saxAttributes;

    /** The url of the script when parsed */
    private URL scriptURL = null;

    /** A synchronized WeakHashMap from the current Thread (key) to a Tag object (value).
     */
    private final Map threadLocalTagCache = Collections.synchronizedMap(new WeakHashMap());

    public TagScript() {
    }

    public TagScript(final TagFactory tagFactory) {
        this.tagFactory = tagFactory;
    }

    /** Add an initialization attribute for the tag.
     * This method must be called after the setTag() method
     */
    public void addAttribute(final String name, final Expression expression) {
        if (log.isDebugEnabled()) {
            log.debug("adding attribute name: " + name + " expression: " + expression);
        }
        attributes.put(name, new ExpressionAttribute(name, expression));
    }

    /** Add an initialization attribute for the tag.
     * This method must be called after the setTag() method
     */
    public void addAttribute(String name, final String prefix, final String nsURI, final Expression expression) {
        if (log.isDebugEnabled()) {
            log.debug("adding attribute name: " + name + " expression: " + expression);
        }
        if (name.indexOf(':') == -1) {
            name = prefix + ':' + name;
        }
        attributes.put(name, new ExpressionAttribute(name, prefix, nsURI, expression));
    }

    protected void applyLocation(final LocationAware locationAware) {
        if (locationAware.getLineNumber() == -1) {
            locationAware.setColumnNumber(columnNumber);
            locationAware.setLineNumber(lineNumber);
        }
        if ( locationAware.getFileName() == null ) {
            locationAware.setFileName( fileName );
        }
        if ( locationAware.getElementName() == null ) {
            locationAware.setElementName( elementName );
        }
    }

    /**
     * Flushes the current cached tag so that it will be created, lazily, next invocation
     */
    protected void clearTag() {
        final Thread t = Thread.currentThread();
        threadLocalTagCache.put(t, null);
    }

    /**
     * Compiles the tags body
     */
    @Override
    public Script compile() throws JellyException {
        if (tagBody != null) {
            tagBody = tagBody.compile();
        }
        return this;
    }

    /**
     * Compiles a newly created tag if required, sets its parent and body.
     */
    protected void configureTag(final Tag tag, final JellyContext context) throws JellyException {
    	tag.setTagLib(tagLibrary);
        if (tag instanceof CompilableTag) {
            ((CompilableTag) tag).compile();
        }
        Tag parentTag = null;
        if ( parent != null ) {
            parentTag = parent.getTag(context);
        }
        tag.setParent( parentTag );
        tag.setBody( tagBody );

        if (tag instanceof NamespaceAwareTag) {
            final NamespaceAwareTag naTag = (NamespaceAwareTag) tag;
            naTag.setNamespaceContext(getNamespaceContext());
        }
        if (tag instanceof LocationAware) {
            applyLocation((LocationAware) tag);
        }
    }

    /**
     * Converts the given value to the required type.
     *
     * @param value is the value to be converted. This will not be null
     * @param requiredType the type that the value should be converted to
     */
    protected Object convertType(final Object value, final Class requiredType)
        throws JellyException {
        if (requiredType.isInstance(value)) {
            return value;
        }
        if (value instanceof String) {
            return ConvertUtils.convert((String) value, requiredType);
        }
        return value;
    }

    // Script interface
    //-------------------------------------------------------------------------

    /**
     * Creates a new Jelly exception, adorning it with location information
     */
    protected JellyException createJellyException(final String reason) {
        return new JellyException(
            reason, fileName, elementName, columnNumber, lineNumber
        );
    }

    /**
     * Creates a new Jelly exception, adorning it with location information
     */
    protected JellyException createJellyException(final String reason, final Exception cause) {
        if (cause instanceof JellyException) {
            return (JellyException) cause;
        }

        if (cause instanceof InvocationTargetException) {
            return new JellyException(
                reason,
                ((InvocationTargetException) cause).getTargetException(),
                fileName,
                elementName,
                columnNumber,
                lineNumber);
        }
        return new JellyException(
            reason, cause, fileName, elementName, columnNumber, lineNumber
        );
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Factory method to create a new Tag instance.
     * The default implementation is to delegate to the TagFactory
     */
    protected Tag createTag() throws JellyException {
        if ( tagFactory != null) {
            return tagFactory.createTag(localName, getSaxAttributes());
        }
        return null;
    }

    /**
     * End the new namespace prefixes mapped for the current element
     */
    protected void endNamespacePrefixes(final XMLOutput output) throws SAXException {
        if ( tagNamespacesMap != null ) {
            for ( final Iterator iter = tagNamespacesMap.keySet().iterator(); iter.hasNext(); ) {
                final String prefix = (String) iter.next();
                output.endPrefixMapping(prefix);
            }
        }
    }

    /**
     * @return the column number of the tag
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * @return the element name which caused the problem
     */
    public String getElementName() {
        return elementName;
    }

	/**
     * @return the Jelly file which caused the problem
     */
    public String getFileName() {
        return fileName;
    }

	/**
     * Strips off the name of a script to create a new context URL
     * FIXME: Copied from JellyContext
     */
    private URL getJellyContextURL(final URL url) throws MalformedURLException {
        String text = url.toString();
        final int idx = text.lastIndexOf('/');
        text = text.substring(0, idx + 1);
        return new URL(text);
    }

    /**
     * @return the line number of the tag
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Returns the local, non namespaced XML name of this tag
     * @return String
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * Returns the namespace context of this tag. This is all the prefixes
     * in scope in the document where this tag is used which are mapped to
     * their namespace URIs.
     *
     * @return a Map with the keys are namespace prefixes and the values are
     * namespace URIs.
     */
    public synchronized Map getNamespaceContext() {
        if (namespaceContext == null) {
            if (parent != null) {
                namespaceContext = getParent().getNamespaceContext();
                if (tagNamespacesMap != null && !tagNamespacesMap.isEmpty()) {
                    // create a new child context
                    final Hashtable newContext = new Hashtable(namespaceContext.size()+1);
                    newContext.putAll(namespaceContext);
                    newContext.putAll(tagNamespacesMap);
                    namespaceContext = newContext;
                }
            }
            else {
                namespaceContext = tagNamespacesMap;
                if (namespaceContext == null) {
                    namespaceContext = new Hashtable();
                }
            }
        }
        return namespaceContext;
    }

    /**
     * Returns the parent.
     * @return TagScript
     */
    public TagScript getParent() {
        return parent;
    }

    /**
     * Returns the SAX attributes of this tag
     * @return Attributes
     */
    public Attributes getSaxAttributes() {
        return saxAttributes;
    }

    /**
     * @return the tag to be evaluated, creating it lazily if required.
     */
    public Tag getTag(final JellyContext context) throws JellyException {
        final Thread t = Thread.currentThread();
        Tag tag = (Tag) threadLocalTagCache.get(t);
        if (tag == null) {
            tag = createTag();
            if (tag != null) {
                threadLocalTagCache.put(t, tag);
                configureTag(tag, context);
            }
        }
        return tag;
    }

    /**
     * Returns the tagBody.
     * @return Script
     */
    public Script getTagBody() {
        return tagBody;
    }
    /**
     * Returns the Factory of Tag instances.
     * @return the factory
     */
    public TagFactory getTagFactory() {
        return tagFactory;
    }

    /**
	 * @return the tagLibrary
	 */
	public TagLibrary getTagLibrary() {
		return tagLibrary;
	}

    /**
     * A helper method to handle this non-Jelly exception.
     * This method will rethrow the exception, wrapped in a JellyException
     * while adding line number information etc.
     *
     * Is this method wise?
     */
    protected void handleException(final Error e) throws Error, JellyTagException {
        if (log.isTraceEnabled()) {
            log.trace( "Caught exception: " + e, e );
        }

        if (e instanceof LocationAware) {
            applyLocation((LocationAware) e);
        }

        throw new JellyTagException(e, fileName, elementName, columnNumber, lineNumber);
    }

    /**
     * A helper method to handle this non-Jelly exception.
     * This method will rethrow the exception, wrapped in a JellyException
     * while adding line number information etc.
     */
    protected void handleException(final Exception e) throws JellyTagException {
        if (log.isTraceEnabled()) {
            log.trace( "Caught exception: " + e, e );
        }

        if (e instanceof LocationAware) {
            applyLocation((LocationAware) e);
        }

        if ( e instanceof JellyException ) {
            e.fillInStackTrace();
        }

        if ( e instanceof InvocationTargetException) {
            throw new JellyTagException( ((InvocationTargetException)e).getTargetException(),
                                      fileName,
                                      elementName,
                                      columnNumber,
                                      lineNumber );
        }

        throw new JellyTagException(e, fileName, elementName, columnNumber, lineNumber);
    }

    /**
     * A helper method to handle this Jelly exception.
     * This method adorns the JellyException with location information
     * such as adding line number information etc.
     */
    protected void handleException(final JellyException e) throws JellyTagException {
        if (log.isTraceEnabled()) {
            log.trace( "Caught exception: " + e, e );
        }

        applyLocation(e);

        throw new JellyTagException(e);
    }

    /**
     * A helper method to handle this Jelly exception.
     * This method adorns the JellyException with location information
     * such as adding line number information etc.
     */
    protected void handleException(final JellyTagException e) throws JellyTagException {
        if (log.isTraceEnabled()) {
            log.trace( "Caught exception: " + e, e );
        }

        applyLocation(e);

        throw e;
    }

    /** Evaluates the body of a tag */
    @Override
    public void run(final JellyContext context, final XMLOutput output) throws JellyTagException {
        final URL rootURL = context.getRootURL();
        final URL currentURL = context.getCurrentURL();
        if ( ! context.isCacheTags() ) {
            clearTag();
        }
        try {
            final Tag tag = getTag(context);
            if ( tag == null ) {
                return;
            }
            tag.setContext(context);
            setContextURLs(context);

            if ( tag instanceof DynaTag ) {
                final DynaTag dynaTag = (DynaTag) tag;

                // ### probably compiling this to 2 arrays might be quicker and smaller
                for (final Iterator iter = attributes.entrySet().iterator(); iter.hasNext();) {
                    final Map.Entry entry = (Map.Entry) iter.next();
                    final String name = (String) entry.getKey();
                    final Expression expression = ((ExpressionAttribute) entry.getValue()).exp;

                    final Class type = dynaTag.getAttributeType(name);
                    Object value = null;
                    if (type != null && type.isAssignableFrom(Expression.class) && !type.isAssignableFrom(Object.class)) {
                        value = expression;
                    }
                    else {
                        value = expression.evaluateRecurse(context);
                    }
                    dynaTag.setAttribute(name, value);
                }
            }
            else {
                // treat the tag as a bean
                final DynaBean dynaBean = new ConvertingWrapDynaBean( tag );
                for (final Iterator iter = attributes.entrySet().iterator(); iter.hasNext();) {
                    final Map.Entry entry = (Map.Entry) iter.next();
                    final String name = (String) entry.getKey();
                    final Expression expression = ((ExpressionAttribute) entry.getValue()).exp;

                    final DynaProperty property = dynaBean.getDynaClass().getDynaProperty(name);
                    if (property == null) {
                        throw new JellyException("This tag does not understand the '" + name + "' attribute" );
                    }
                    final Class type = property.getType();

                    Object value = null;
                    if (type.isAssignableFrom(Expression.class) && !type.isAssignableFrom(Object.class)) {
                        value = expression;
                    }
                    else {
                        value = expression.evaluateRecurse(context);
                    }
                    dynaBean.set(name, value);
                }
            }

            tag.doTag(output);
            if (output != null) {
                output.flush();
            }
        }
        catch (final JellyTagException e) {
            handleException(e);
        } catch (final JellyException e) {
            handleException(e);
        } catch (final IOException | RuntimeException e) {
            handleException(e);
        }
        catch (final Error e) {
           /*
            * Not sure if we should be converting errors to exceptions,
            * but not trivial to remove because JUnit tags throw
            * Errors in the normal course of operation.  Hmm...
            */
            handleException(e);
        } finally {
            context.setRootURL(rootURL);
            context.setCurrentURL(currentURL);
        }

    }

    /**
     * Sets the column number of the tag
     */
    public void setColumnNumber(final int columnNumber) {
        this.columnNumber = columnNumber;
    }

    /**
     * Sets the context's root and current URL if not present
     * @param context
     * @throws JellyTagException
     */
    protected void setContextURLs(final JellyContext context) throws JellyTagException {
        if ((context.getCurrentURL() == null || context.getRootURL() == null) && scriptURL != null)
        {
            if (context.getRootURL() == null) {
                context.setRootURL(scriptURL);
            }
            if (context.getCurrentURL() == null) {
                context.setCurrentURL(scriptURL);
            }
        }
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the element name which caused the problem
     */
    public void setElementName(final String elementName) {
        this.elementName = elementName;
    }


    /**
     * Sets the Jelly file which caused the problem
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
        try
        {
            this.scriptURL = getJellyContextURL(new URL(fileName));
        } catch (final MalformedURLException e) {
            log.debug("error setting script url", e);
        }
    }

    /**
     * Sets the line number of the tag
     */
    public void setLineNumber(final int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Sets the local, non namespaced name of this tag.
     * @param localName The localName to set
     */
    public void setLocalName(final String localName) {
        this.localName = localName;
    }

    /**
     * Configures this TagScript from the SAX Locator, setting the column
     * and line numbers
     */
    public void setLocator(final Locator locator) {
        setLineNumber( locator.getLineNumber() );
        setColumnNumber( locator.getColumnNumber() );
    }

    /**
     * Sets the parent.
     * @param parent The parent to set
     */
    public void setParent(final TagScript parent) {
        this.parent = parent;
    }

    /**
     * Sets the SAX attributes of this tag
     * @param saxAttributes The saxAttributes to set
     */
    public void setSaxAttributes(final Attributes saxAttributes) {
        this.saxAttributes = saxAttributes;
    }

    /**
     * Allows the script to set the tag instance to be used, such as in a StaticTagScript
     * when a StaticTag is switched with a DynamicTag
     */
    protected void setTag(final Tag tag, final JellyContext context) {
        final Thread t = Thread.currentThread();
        threadLocalTagCache.put(t, tag);
    }

    /**
     * Sets the tagBody.
     * @param tagBody The tagBody to set
     */
    public void setTagBody(final Script tagBody) {
        this.tagBody = tagBody;
    }

    /**
     * Sets the Factory of Tag instances.
     * @param tagFactory The factory to set
     */
    public void setTagFactory(final TagFactory tagFactory) {
        this.tagFactory = tagFactory;
    }

    /**
	 * @param tagLibrary the tagLibrary to set
	 */
	public void setTagLibrary(final TagLibrary tagLibrary) {
		this.tagLibrary = tagLibrary;
	}

    /**
     * Sets the optional namespaces prefix to URI map of
     * the namespaces attached to this Tag
     */
    public void setTagNamespacesMap(Map tagNamespacesMap) {
        // lets check that this is a thread-safe map
        if ( ! (tagNamespacesMap instanceof Hashtable) ) {
            tagNamespacesMap = new Hashtable( tagNamespacesMap );
        }
        this.tagNamespacesMap = tagNamespacesMap;
    }

    /**
     * Output the new namespace prefixes used for this element
     */
    protected void startNamespacePrefixes(final XMLOutput output) throws SAXException {
        if ( tagNamespacesMap != null ) {
            for ( final Iterator iter = tagNamespacesMap.entrySet().iterator(); iter.hasNext(); ) {
                final Map.Entry entry = (Map.Entry) iter.next();
                final String prefix = (String) entry.getKey();
                final String uri = (String) entry.getValue();
                output.startPrefixMapping(prefix, uri);
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + "[tag=" + elementName + ";at=" + lineNumber + ":" + columnNumber + "]";
    }
}
