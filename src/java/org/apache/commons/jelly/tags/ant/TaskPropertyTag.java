/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/core/IfTag.java,v 1.6 2002/05/17 15:18:08 jstrachan Exp $
 * $Revision: 1.6 $
 * $Date: 2002/05/17 15:18:08 $
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
 * $Id: IfTag.java,v 1.6 2002/05/17 15:18:08 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.ant;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.ConvertingWrapDynaBean;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;

import org.apache.commons.jelly.CompilableTag;
import org.apache.commons.jelly.DynaBeanTagSupport;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Reference;

/** 
 * A tag which configures a property bean of a Task, 
 * such as a &lt;classpath&gt; element inside a &lt;java&gt; element.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.6 $
 */
public class TaskPropertyTag extends DynaBeanTagSupport implements CompilableTag {

    private static final Class[] emptyParameterTypes = {};
    private static final Object[] emptyParameters = {};
    
    /** the name of the property */
    private String name;
    
    public TaskPropertyTag() {
    }

    public TaskPropertyTag(String name) {
        setName(name);
    }

    // DynaTag interface
    //------------------------------------------------------------------------- 
    public void setAttribute(String name, Object value) {
        if ( name.equals( "refid" ) ) {
            if ( value instanceof String ) {
                value = new Reference( (String) value );
            }
        }
        super.setAttribute(name, value);
    }

    // CompilableTag interface
    //------------------------------------------------------------------------- 
    public void compile() throws Exception {
        TaskTag tag = (TaskTag) findAncestorWithClass( TaskTag.class );
        if ( tag == null ) {
            throw new JellyException( "You should only use Ant DataType tags within an Ant Task" );
        }        
        
        Task task = tag.getTask();
        Class taskClass = task.getClass();
        String methodName = "create" + name.substring(0,1).toUpperCase() + name.substring(1);
        Method method = taskClass.getMethod( methodName, emptyParameterTypes );
        if ( method == null ) {
            throw new JellyException( 
                "Cannot create Task property: " + name + " of Ant task: " + task 
                + " as no method called: " + methodName + " could be found" 
            );
        }            
        
        Object propertyBean = method.invoke( task, emptyParameters );
        if ( propertyBean == null ) {
            throw new JellyException( "No property: " + name + " of task: " + task + " was returned." );
        }
        
        setDynaBean( new ConvertingWrapDynaBean(propertyBean) );
    }
    
    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws Exception {
        // do nothing; just configuring the underlying property bean of the Task is enough
    }
    
    // Properties
    //-------------------------------------------------------------------------                
    
    /** 
     * @return the name of the Task property bean 
     */
    public String getName() {
        return name;
    }
    
    /** 
     * Sets the name of the Task property bean
     */
    public void setName(String name) {
        this.name = name;
    }    
}
