/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/ant/Attic/AntTagSupport.java,v 1.3 2002/06/25 18:00:09 jstrachan Exp $
 * $Revision: 1.3 $
 * $Date: 2002/06/25 18:00:09 $
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
 * $Id: AntTagSupport.java,v 1.3 2002/06/25 18:00:09 jstrachan Exp $
 */

package org.apache.commons.jelly.tags.ant;

import org.apache.commons.jelly.DynaBeanTagSupport;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.types.DataType;

import org.apache.commons.beanutils.MethodUtils;

import java.lang.reflect.Method;

public abstract class AntTagSupport extends DynaBeanTagSupport {

    private Project project;

    public AntTagSupport() {
        
    }
    public AntTagSupport(Project project) {
        this.project = project;
    }

    public void setAntProject(Project project) {
        this.project = project;
    }

    public Project getAntProject() {
        return this.project;
    }

    public Object createNestedObject(String name) throws Exception {

        Object object = getObject();

        IntrospectionHelper ih = IntrospectionHelper.getHelper( object.getClass() );

        Object dataType = null;

        try {
            dataType = ih.createElement( getAntProject(), object, name );
        } catch (Exception e) {
            dataType = null;
            e.printStackTrace();
        }

        return dataType;
    }

    public Task createTask(String taskName) throws Exception {
        return createTask( taskName,
                           (Class) getAntProject().getTaskDefinitions().get( taskName ) );
    }

    public Task createTask(String taskName,
                           Class taskType) throws Exception {
        if (taskType == null) {
            return null;
        }

        Object o = taskType.newInstance();
        Task task = null;
        if( o instanceof Task ) {
            task=(Task)o;
        } else {
            TaskAdapter taskA=new TaskAdapter();
            taskA.setProxy( o );
            task=taskA;
        }

        task.setProject(getAntProject());
        task.setTaskName(taskName);

        return task;
    }

    public void setAttribute(String name, Object value) throws Exception {

        Object obj = null;

        try {
            obj = getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        if ( obj == null ) {
            return;
        }
        
        IntrospectionHelper ih = IntrospectionHelper.getHelper( obj.getClass() );

        if ( value instanceof String ) {
            try {
                ih.setAttribute( getAntProject(), obj, name.toLowerCase(), (String) value );
                return;
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }

        try {
            ih.storeElement( getAntProject(), obj, value, name );
        } catch (Exception e) {
            // e.printStackTrace();

            // let any exceptions bubble up from here
            
            super.setAttribute( name, value );
        }
    }
    
    public abstract Object getObject() throws Exception;
}
