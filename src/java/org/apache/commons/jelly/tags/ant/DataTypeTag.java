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

import org.apache.commons.beanutils.ConvertingWrapDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import org.apache.commons.jelly.DynaBeanTagSupport;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DataType;

/** 
 * A tag which manages a DataType used to configure a Task
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.6 $
 */
public class DataTypeTag extends DynaBeanTagSupport {

    /** the name of the DataType */
    private String name;
    
    /** the Ant DataType */
    private Object dataType;

    public DataTypeTag() {
    }

    public DataTypeTag(String name, Object dataType) {
        this.name = name;
        this.dataType = dataType;
        setDynaBean( new ConvertingWrapDynaBean(dataType) );
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws Exception {
        TaskSource tag = (TaskSource) findAncestorWithClass( TaskSource.class );
        if ( tag == null ) {
            throw new JellyException( "You should only use Ant DataType tags within an Ant Task" );
        }        
        
        Object task = tag.getTaskObject();
        Object dataType = getDataType();

        // now we need to configure the task with the data type
        
        // first try setting a property on the DynaBean wrapper of the task
        DynaBean dynaBean = tag.getDynaBean();
        DynaClass dynaClass = dynaBean.getDynaClass();
        DynaProperty dynaProperty = dynaClass.getDynaProperty(name);
        if ( dynaProperty != null ) {
            // lets set the bean property
            dynaBean.set( name, dataType );
        }
        else {
            // lets invoke the addFoo() method instead
            String methodName = "add" + name.substring(0,1).toUpperCase() + name.substring(1);
            
            System.out.println( "About to invoke method: " + methodName );
            
            Class taskClass = task.getClass();
            Class[] parameterTypes = new Class[] { dataType.getClass() };
            Method method = taskClass.getMethod( methodName, parameterTypes );
            if ( method == null ) {
                throw new JellyException( 
                    "Cannot add dataType: " + dataType + " to Ant task: " + task 
                    + " as no method called: " + methodName + " could be found" 
                );
            }
            
            Object[] parameters = new Object[] { dataType };
            method.invoke( task, parameters );
        }
        
        
                
        // run the body first to configure any nested DataType instances
        getBody().run(context, output);
    }
    
    // Properties
    //-------------------------------------------------------------------------                
    
    /** 
     * @return the name of the DataType 
     */
    public String getName() {
        return name;
    }
    
    /** 
     * Sets the name of the DataType 
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /** 
     * @return the Ant dataType
     */
    public Object getDataType() {
        return dataType;
    }
    
    /** 
     * Sets the Ant dataType
     */
    public void setDataType(Object dataType) {
        this.dataType = dataType;
        setDynaBean( new ConvertingWrapDynaBean(dataType) );
    }
    
}
