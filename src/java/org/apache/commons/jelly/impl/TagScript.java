/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/impl/TagScript.java,v 1.22 2002/10/08 20:22:13 werken Exp $
 * $Revision: 1.22 $
 * $Date: 2002/10/08 20:22:13 $
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
 * $Id: TagScript.java,v 1.22 2002/10/08 20:22:13 werken Exp $
 */
package org.apache.commons.jelly.impl;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertingWrapDynaBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;

import org.apache.commons.jelly.CompilableTag;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.DynaTag;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/** 
 * <p><code>TagScript</code> is a Script that evaluates a custom tag.</p>
 * 
 * <b>Note</b> that this class should be re-entrant and used
 * concurrently by multiple threads.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.22 $
 */
public class TagScript implements Script {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TagScript.class);

    /** 
     * Thread local storage for the tag used by the current thread.
     * This allows us to pool tag instances, per thread to reduce object construction
     * over head, if we need it.
     * 
     * Note that we could use the stack and create a new tag for each invocation
     * if we made a slight change to the Script API to pass in the parent tag.
     */
    private ThreadLocal tagHolder = new ThreadLocal();

    /** The attribute expressions that are created */
    protected Map attributes = new Hashtable();
    
    /** the optional namespaces Map of prefix -> URI */
    private Map namespacesMap;
    
    /** the Jelly file which caused the problem */
    private String fileName;

    /** the tag name which caused the problem */
    private String elementName;

    /** the line number of the tag */
    private int lineNumber = -1;
    
    /** the column number of the tag */
    private int columnNumber = -1;

    /** the factory of Tag instances */
    private TagFactory tagFactory;
    
    /** the body script used for this tag */
    private Script tagBody;
    
    /** the parent TagScript */
    private TagScript parent;
    
    /** 
     * @return a new TagScript based on whether 
     * the given Tag class is a bean tag or DynaTag 
     */
    public static TagScript newInstance(Class tagClass) {
        TagFactory factory = new DefaultTagFactory(tagClass);
        return new TagScript(factory);
    }
    
    public TagScript() {
    }

    public TagScript(TagFactory tagFactory) {
        this.tagFactory = tagFactory;
    }
    
    public String toString() {
        return super.toString() + "[tag=" + elementName + ";at=" + lineNumber + ":" + columnNumber + "]";
    }

    /**
     * Compiles the tags body
     */
    public Script compile() throws Exception {
        if (tagBody != null) {
            tagBody = tagBody.compile();
        }
        return this;
    }
    
    /**
     * Sets the optional namespaces prefix -> URI map
     */
    public void setNamespacesMap(Map namespacesMap) {
        // lets check that this is a thread-safe map
        if ( ! (namespacesMap instanceof Hashtable) ) {
            namespacesMap = new Hashtable( namespacesMap );
        }
        this.namespacesMap = namespacesMap;
    }
        
    /**
     * Configures this TagScript from the SAX Locator, setting the column
     * and line numbers
     */
    public void setLocator(Locator locator) {
        setLineNumber( locator.getLineNumber() );
        setColumnNumber( locator.getColumnNumber() );
    }

    
    /** Add an initialization attribute for the tag.
     * This method must be called after the setTag() method 
     */
    public void addAttribute(String name, Expression expression) {
        if (log.isDebugEnabled()) {
            log.debug("adding attribute name: " + name + " expression: " + expression);
        }
        attributes.put(name, expression);
    }
    
    // Script interface
    //-------------------------------------------------------------------------                

    /** Evaluates the body of a tag */
    public void run(JellyContext context, XMLOutput output) throws Exception {
        if ( ! context.isCacheTags() ) {
            clearTag();
        }
        try {
            Tag tag = getTag();
            if ( tag == null ) {
                return;
            }
            tag.setContext(context);
    
            if ( tag instanceof DynaTag ) {        
                DynaTag dynaTag = (DynaTag) tag;
        
                // ### probably compiling this to 2 arrays might be quicker and smaller
                for (Iterator iter = attributes.entrySet().iterator(); iter.hasNext();) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String name = (String) entry.getKey();
                    Expression expression = (Expression) entry.getValue();

                    Class type = dynaTag.getAttributeType(name);
                    Object value = null;        
                    if (type != null && type.isAssignableFrom(Expression.class) && !type.isAssignableFrom(Object.class)) {
                        value = expression;
                    }
                    else {
                        value = expression.evaluate(context);
                    }
                    dynaTag.setAttribute(name, value);
                }
            }
            else {
                // treat the tag as a bean
                DynaBean dynaBean = new ConvertingWrapDynaBean( tag );
                for (Iterator iter = attributes.entrySet().iterator(); iter.hasNext();) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String name = (String) entry.getKey();
                    Expression expression = (Expression) entry.getValue();

                    DynaProperty property = dynaBean.getDynaClass().getDynaProperty(name);
                    if (property == null) {
                        throw new JellyException("This tag does not understand the '" + name + "' attribute" );
                    }
                    Class type = property.getType();

                    Object value = null;        
                    if (type.isAssignableFrom(Expression.class) && !type.isAssignableFrom(Object.class)) {
                        value = expression;
                    }
                    else {
                        value = expression.evaluate(context);
                    }
                    dynaBean.set(name, value);
                }
            }
        
            tag.doTag(output);
        } 
        catch (JellyException e) {
            handleException(e);
        }
        catch (Exception e) {
            handleException(e);
        }
    }
    
    
    // Properties
    //-------------------------------------------------------------------------                

    /** 
     * @return the tag to be evaluated, creating it lazily if required.
     */
    public Tag getTag() throws Exception {
        Tag tag = (Tag) tagHolder.get();
        if ( tag == null ) {
            tag = createTag();
            if ( tag != null ) {
                configureTag(tag);
                tagHolder.set(tag);
            }
        }
        return tag;
    }

    /**
     * Returns the Factory of Tag instances.
     * @return the factory
     */
    public TagFactory getTagFactory() {
        return tagFactory;
    }

    /**
     * Sets the Factory of Tag instances.
     * @param tagFactory The factory to set
     */
    public void setTagFactory(TagFactory tagFactory) {
        this.tagFactory = tagFactory;
    }

    /**
     * Returns the parent.
     * @return TagScript
     */
    public TagScript getParent() {
        return parent;
    }

    /**
     * Returns the tagBody.
     * @return Script
     */
    public Script getTagBody() {
        return tagBody;
    }

    /**
     * Sets the parent.
     * @param parent The parent to set
     */
    public void setParent(TagScript parent) {
        this.parent = parent;
    }

    /**
     * Sets the tagBody.
     * @param tagBody The tagBody to set
     */
    public void setTagBody(Script tagBody) {
        this.tagBody = tagBody;
    }

    /** 
     * @return the Jelly file which caused the problem 
     */
    public String getFileName() {
        return fileName;
    }

    /** 
     * Sets the Jelly file which caused the problem 
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    

    /** 
     * @return the element name which caused the problem
     */
    public String getElementName() {
        return elementName;
    }

    /** 
     * Sets the element name which caused the problem
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }
    /** 
     * @return the line number of the tag 
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /** 
     * Sets the line number of the tag 
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /** 
     * @return the column number of the tag 
     */
    public int getColumnNumber() {
        return columnNumber;
    }
    
    /** 
     * Sets the column number of the tag 
     */
    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------      

    /**
     * Factory method to create a new Tag instance. 
     * The default implementation is to delegate to the TagFactory
     */
    protected Tag createTag() throws Exception {    
        if ( tagFactory != null) {
            return tagFactory.createTag();
        }
        return null;
    }


    /**
     * Compiles a newly created tag if required, sets its parent and body.
     */
    protected void configureTag(Tag tag) throws Exception {
        if (tag instanceof CompilableTag) {
            ((CompilableTag) tag).compile();
        }
        Tag parentTag = null;
        if ( parent != null ) {
            parentTag = parent.getTag();
        }
        tag.setParent( parentTag );
        tag.setBody( tagBody );
    }
     
    /**
     * Flushes the current cached tag so that it will be created, lazily, next invocation
     */
    protected void clearTag() {
        tagHolder.set(null);
    }
    
    /** 
     * Allows the script to set the tag instance to be used, such as in a StaticTagScript
     * when a StaticTag is switched with a DynamicTag
     */
    protected void setTag(Tag tag) {
        tagHolder.set(tag);
    }

    /**
     * Output the new namespace prefixes used for this element
     */    
    protected void startNamespacePrefixes(XMLOutput output) throws SAXException {
        if ( namespacesMap != null ) {
            for ( Iterator iter = namespacesMap.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                String prefix = (String) entry.getKey();
                String uri = (String) entry.getValue();
                output.startPrefixMapping(prefix, uri);
            }
        }
    }
    
    /**
     * End the new namespace prefixes mapped for the current element
     */    
    protected void endNamespacePrefixes(XMLOutput output) throws SAXException {
        if ( namespacesMap != null ) {
            for ( Iterator iter = namespacesMap.keySet().iterator(); iter.hasNext(); ) {
                String prefix = (String) iter.next();
                output.endPrefixMapping(prefix);
            }
        }
    }
    
    /** 
     * Converts the given value to the required type. 
     *
     * @param value is the value to be converted. This will not be null
     * @param requiredType the type that the value should be converted to
     */
    protected Object convertType(Object value, Class requiredType)
        throws Exception {
        if (requiredType.isInstance(value)) {
            return value;
        }
        if (value instanceof String) {
            return ConvertUtils.convert((String) value, requiredType);
        }
        return value;
    }
    
    /**
     * Creates a new Jelly exception, adorning it with location information
     */
    protected JellyException createJellyException(String reason) {
        return new JellyException( 
            reason, fileName, elementName, columnNumber, lineNumber
        );
    }
    
    /**
     * Creates a new Jelly exception, adorning it with location information
     */
    protected JellyException createJellyException(String reason, Exception cause) {
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
    
    /**
     * A helper method to handle this Jelly exception.
     * This method adorns the JellyException with location information
     * such as adding line number information etc.
     */
    protected void handleException(JellyException e) throws Exception {
        //e.printStackTrace();
        // log.error( "Caught exception: " + e, e );

        if (e.getLineNumber() == -1) {
            e.setColumnNumber(columnNumber);
            e.setLineNumber(lineNumber);
        }
        if ( e.getFileName() == null ) {
            e.setFileName( fileName );
        }
        if ( e.getElementName() == null ) {
            e.setElementName( elementName );
        }
        throw e;
    }
    
    /**
     * A helper method to handle this non-Jelly exception.
     * This method will rethrow the exception, wrapped in a JellyException
     * while adding line number information etc.
     */
    protected void handleException(Exception e) throws Exception {
        //e.printStackTrace();
        // log.error( "Caught exception: " + e, e );

        if ( e instanceof JellyException ) {
            e.fillInStackTrace();
            throw e;
        }

        if ( e instanceof InvocationTargetException) {
            throw new JellyException( ((InvocationTargetException)e).getTargetException(),
                                      fileName,
                                      elementName,
                                      columnNumber,
                                      lineNumber );
        }

        throw new JellyException(e, fileName, elementName, columnNumber, lineNumber);            
    }
    
}
