/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/JellyContext.java,v 1.10 2002/04/26 12:20:12 jstrachan Exp $
 * $Revision: 1.10 $
 * $Date: 2002/04/26 12:20:12 $
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
 * $Id: JellyContext.java,v 1.10 2002/04/26 12:20:12 jstrachan Exp $
 */

package org.apache.commons.jelly.task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.jelly.JellyContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.tools.ant.Project;

/** <p><code>AntJellyContext</code> represents the Jelly context from inside Ant.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.10 $
  */

public class AntJellyContext extends JellyContext {

    /** The Ant project which contains the variables */
    private Project project;

    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog(AntJellyContext.class);

    public AntJellyContext(Project project, JellyContext parentJellyContext) {
        super( parentJellyContext );
        this.project = project;
    }
    
    /** @return the value of the given variable name */
    public Object getVariable(String name) {
        // look in parent first
        Object answer = super.getVariable(name);
        if (answer == null) {
            answer = project.getProperty(name);
        }
        
        if ( log.isDebugEnabled() ) {
            log.debug( "Looking up variable: " + name + " answer: " + answer );
        }
        
        return answer;
    }
    
    /** Sets the value of the given variable name */
    public void setVariable(String name, Object value) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Setting variable: " + name + " to: " + value );
        }
        
        super.setVariable( name, value );
        
        // only export string values back to Ant?
        if ( value instanceof String ) {
            project.setProperty(name, (String) value);
        }
    }
    
    /** Removes the given variable */
    public void removeVariable(String name) {
        super.removeVariable( name );
        project.setProperty(name, null);        
    }
    
    /** 
     * @return an Iterator over the current variable names in this
     * context 
     */
    public Iterator getVariableNames() {
        return getVariables().keySet().iterator();
    }
    
    /**
     * @return the Map of variables in this scope
     */
    public Map getVariables() {
        // we should add all the Project's properties
        Map map = new HashMap( project.getProperties() );
        
        // override any local properties
        map.putAll( super.getVariables() );
        return map;
    }
    
    /**
     * Sets the Map of variables to use
     */
    
    public void setVariables(Map variables) {
        super.setVariables(variables);
        
        // export any Ant properties
        for ( Iterator iter = variables.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            if ( value instanceof String ) {
                project.setProperty(key, (String)value);
            }
        }
    }
    

    // Implementation methods
    //-------------------------------------------------------------------------                
    
    /**
     * Factory method to create a new child of this context
     */
    protected JellyContext createChildContext() {
        return new AntJellyContext(project, this);
    }

}
