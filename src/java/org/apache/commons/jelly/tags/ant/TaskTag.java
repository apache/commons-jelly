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
import org.apache.commons.beanutils.MethodUtils;

import org.apache.commons.jelly.DynaBeanTagSupport;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.CompilableTag;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.types.DataType;

/** 
 * A tag which invokes an Ant Task
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.6 $
 */
public class TaskTag extends AntTagSupport implements TaskSource { 

    /** argumment types */
    private static final Class[] addTaskParamTypes = { String.class };
    
    /** the type of the Ant task */
    private Class taskType;

    /** the Ant task */
    private Task task;

    /** the name of the Ant task */
    private String taskName;

    public TaskTag() {
    }

    public TaskTag(Project project, Class taskType, String taskName) {
        super( project );
        this.taskType = taskType;
        this.taskName = taskName;
    }

    public TaskTag(Project project, String taskName) {
        super( project );
        this.taskName = taskName;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public Class getTaskType() {
        return this.taskType;
    }

    public void setTaskType(Class taskType) {
        this.taskType = taskType;
    }
    
    /**
     * Override this method as it is called before the tag is configured with its attributes.
     * We will create a new task object and let the DynaBean configure it.
     */ 
    public void setContext(JellyContext context) throws Exception {
        super.setContext(context);
        task = null;
        
        // force task to be recreated
        getTask();                
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws Exception {
        Task task = getTask();                
        // setDynaBean( new ConvertingWrapDynaBean( task ) );

		// if the task has an addText()
		Method method = MethodUtils.getAccessibleMethod(
            taskType, "addText", addTaskParamTypes
        );            

		if (method != null) {
            String text = getBodyText();

			Object[] args = { text };
			method.invoke(task, args);
		} else {
            getBody().run(context, output);
        }
        
        task.perform();   
    }

    // DataTypeCreator interface
    //------------------------------------------------------------------------- 

    public DataType createDataType(String name) throws Exception {

        IntrospectionHelper helper = IntrospectionHelper.getHelper( getTask().getClass() );

        return (DataType) helper.createElement( getAntProject(),
                                                getTask(),
                                                name );
    }

    
    // TaskSource interface
    //------------------------------------------------------------------------- 
    public Object getTaskObject() throws Exception {
        return getTask();
    }

    public Object getObject() throws Exception {
        return getTask();
    }
    
    // Properties
    //-------------------------------------------------------------------------                
    
    /** 
     * @return the Ant task
     */
    public Task getTask() throws Exception {

        if ( this.task == null ) {
            if ( getTaskType() == null ) {
                setTaskType( (Class) getAntProject().getTaskDefinitions().get( getTaskName() ) );
            }
            
            this.task = createTask( getTaskName(),
                                    getTaskType() );
            setDynaBean( new ConvertingWrapDynaBean( this.task ) );
        }
        return this.task;
    }

    /** 
     * Sets the Ant task
     */
    public void setTask(Task task) {
        this.task = task;
        setDynaBean( new ConvertingWrapDynaBean( this.task ) );
    }
    

    public String toString() {
        try
        {
            return "[TaskTag: task=" + getTask().getClass().getName() + "]";
        }
        catch (Exception e) {
            return "[TaskTag: unknown: " + e.getLocalizedMessage() + " ]";
        }
    }

    public void beforeSetAttributes() throws Exception {
        getTask();
    }

}
