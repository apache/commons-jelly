/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/define/DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 * $Revision: 1.7 $
 * $Date: 2002/05/17 15:18:12 $
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
 * $Id: DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.swing;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;

import org.apache.commons.beanutils.BeanUtils;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.MapTagSupport;
import org.apache.commons.jelly.XMLOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Creates a Swing Action and attaches it to the parent component. The This tag creates a Swing component and adds it to its parent tag, optionally declaring this
 * component as a variable if the <i>var</i> attribute is specified.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.7 $
 */
public class ActionTag extends MapTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ActionTag.class);

    /** the current action instance */
    private Action action;

    public ActionTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(final XMLOutput output) throws Exception {

        Map map = getAttributes();
        action = (Action) map.remove( "action" );
        Object classObject = map.remove("className");
        String var = (String) map.remove( "var" );
        
        if ( action == null ) {
            Class actionClass = null;
            if ( classObject != null ) {
                if ( classObject instanceof Class ) {
                    actionClass = (Class) classObject;
                }
                else {
                    String name = classObject.toString();
                    try {
                        actionClass = Class.forName( name );
                    }
                    catch (Throwable t) {
                        throw new JellyException( "Could not find class: " + name + " for this <action> tag. Exception: " +t, t );
                    }
                }
                if ( actionClass != null ) {
                    action = (Action) actionClass.newInstance();
                }
            }
        }
        if ( action == null ) {
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
        
        // now lets set the properties on the action object
        for ( Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            String name = (String) entry.getKey();
            
            // typically standard Action names start with upper case, so lets upper case it            
            name = capitalize(name);
            Object value = entry.getValue();
            
            action.putValue( name, value );
        }

        if ( var != null ) {
            context.setVariable( var, action );
        }
        

        // now lets add this action to its parent if we have one
        ComponentTag tag = (ComponentTag) findAncestorWithClass( ComponentTag.class );
        if ( tag != null ) {
            tag.setAction(action);
        }
    }
    
    // Properties
    //-------------------------------------------------------------------------                    

    /**
     * @return the Action object for this tag
     */
    public Action getAction() {
        return action;
    }
    
    
    // Implementation methods
    //-------------------------------------------------------------------------                    
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
