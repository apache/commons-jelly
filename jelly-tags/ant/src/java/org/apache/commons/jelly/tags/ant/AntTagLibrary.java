/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/xml/XMLTagLibrary.java,v 1.6 2002/05/17 18:04:00 jstrachan Exp $
 * $Revision: 1.6 $
 * $Date: 2002/05/17 18:04:00 $
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
 * $Id: XMLTagLibrary.java,v 1.6 2002/05/17 18:04:00 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.ant;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.impl.TagScript;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DataType;

import org.xml.sax.Attributes;

/** 
 * A Jelly custom tag library that allows Ant tasks to be called from inside Jelly.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.6 $
 */
public class AntTagLibrary extends TagLibrary {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(AntTagLibrary.class);
    
    /** the Ant Project for this tag library */
    private Project project;
        
        
    public AntTagLibrary() {
    }

    public AntTagLibrary(Project project) {
        this.project = project;
    }

    /** Creates a new script to execute the given tag name and attributes */
    public TagScript createTagScript(String name, Attributes attributes) throws Exception {
        Project project = getProject();
        Class type = (Class) project.getTaskDefinitions().get(name);
        if ( type != null ) {            
            Task task = (Task) type.newInstance();
            task.setProject(project);
            task.setTaskName(name);
            Tag tag = new TaskTag( task );
            return TagScript.newInstance(tag);
        }
        Object dataType = null;
        type = (Class) project.getDataTypeDefinitions().get(name);
        if ( type != null ) {            
            dataType = type.newInstance();
        }
        else {
            dataType = project.createDataType(name);
        }
        if ( dataType != null ) {
            DataTypeTag tag = new DataTypeTag( name, dataType );
            tag.getDynaBean().set( "project", project );
            return TagScript.newInstance(tag);
        }
        Tag tag = new TaskPropertyTag( name );
        return TagScript.newInstance(tag);
    }

    
    // Properties
    //-------------------------------------------------------------------------                
    
    /**
     * @return the Ant Project for this tag library.
     */
    public Project getProject() {
        return project;
    }
    
    /**
     * Sets the Ant Project for this tag library.
     */
    public void setProject(Project project) {
        this.project = project;
    }

}
