/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/define/DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 * $Revision: 1.7 $
 * $Date: 2002/05/17 15:18:12 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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
 * $Id: DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.swing;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Creates a Swing Action which on invocation will execute the body of this tag.
 * The Action is then output as a variable for reuse if the 'var' attribute is specified
 * otherwise the action is added to the parent JellySwing widget.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.7 $
 */
public class ActionTag extends UseBeanTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ActionTag.class);

    public ActionTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws JellyTagException {
        Map attributes = getAttributes();
        String var = (String) attributes.get( "var" );
        Object classObject = attributes.remove( "class" );
        
        // this method could return null in derived classes
        Class theClass = null;
        try {
           theClass = convertToClass(classObject);
        } catch (ClassNotFoundException e) {
            throw new JellyTagException(e);
        }
        
        Object bean = newInstance(theClass, attributes, output);
        setBean(bean);
        
        setBeanProperties(bean, attributes);
        
        processBean(var, bean);
    }

    
    // Properties
    //-------------------------------------------------------------------------                    

    /**
     * @return the Action object for this tag
     */
    public Action getAction() {
        return (Action) getBean();
    }
    
    
    // Implementation methods
    //-------------------------------------------------------------------------                    


    /**
     * An existing Action could be specified via the 'action' attribute or an action class 
     * may be specified via the 'class' attribute, otherwise a default Action class is created.
     */
    protected Class convertToClass(Object classObject) throws MissingAttributeException, ClassNotFoundException {
        if (classObject == null) {
            return null;
        }
        else {
            return super.convertToClass(classObject);
        }
    }
    
    /**
     * An existing Action could be specified via the 'action' attribute or an action class 
     * may be specified via the 'class' attribute, otherwise a default Action class is created.
     */
    protected Object newInstance(Class theClass, Map attributes, final XMLOutput output) throws JellyTagException {
        Action action = (Action) attributes.remove( "action" );
        if ( action == null ) {
            if (theClass != null ) {
                
                try {
                    return theClass.newInstance();
                } catch (InstantiationException e) {
                    throw new JellyTagException(e);
                } catch (IllegalAccessException e) {
                    throw new JellyTagException(e);
                }
                
            }
            else {
                action = new AbstractAction() {
                    public void actionPerformed(ActionEvent event) {
                        context.setVariable( "event", event );
                        try {
                            invokeBody(output);
                        }
                        catch (Exception e) {
                            log.error( "Caught: " + e, e );
                        }
                    }
                };
            }
        }
        return action;
    }
    

    /**
     * Either defines a variable or adds the current component to the parent
     */    
    protected void processBean(String var, Object bean) throws JellyTagException {
        if (var != null) {
            context.setVariable(var, bean);
        }
        else {
            ComponentTag tag = (ComponentTag) findAncestorWithClass( ComponentTag.class );
            if ( tag != null ) {
                tag.setAction((Action) bean);
            }
            else {
                throw new JellyTagException( "Either the 'var' attribute must be specified to export this Action or this tag must be nested within a JellySwing widget tag" );
            }
        }
    }
    
        
    /**
     * Perform the strange setting of Action properties using its custom API
     */
    protected void setBeanProperties(Object bean, Map attributes) throws JellyTagException {
        Action action = getAction();
        for ( Iterator iter = attributes.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            String name = (String) entry.getKey();
            
            // typically standard Action names start with upper case, so lets upper case it            
            name = capitalize(name);
            Object value = entry.getValue();
            
            action.putValue( name, value );
        }
    }


    protected String capitalize(String text) {
        char ch = text.charAt(0);
        if ( Character.isUpperCase( ch ) ) {
            return text;
        }
        StringBuffer buffer = new StringBuffer(text.length());
        buffer.append( Character.toUpperCase( ch ) );
        buffer.append( text.substring(1) );
        return buffer.toString();
    }
        
}
