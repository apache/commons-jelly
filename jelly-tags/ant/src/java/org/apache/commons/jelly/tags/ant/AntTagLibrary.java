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

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.ConvertUtils;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.impl.TagScript;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.NoBannerLogger;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;

import org.xml.sax.Attributes;

/** 
 * A Jelly custom tag library that allows Ant tasks to be called from inside Jelly.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @version $Revision: 1.6 $
 */
public class AntTagLibrary extends TagLibrary {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(AntTagLibrary.class);
    
    /** the Ant Project for this tag library */
    private Project project;

    static {

        // register standard converters for Ant types
               
        ConvertUtils.register(
            new Converter() {
                public Object convert(Class type, Object value) {
                    if ( value instanceof File ) {
                        return (File) value;
                    }
                    else if ( value != null ) {
                        String text = value.toString();
                        return new File( text );
                    }
                    return null;
                }
            },
            File.class
            );
        
        ConvertUtils.register(
            new Converter() {
                public Object convert(Class type, Object value) {
                    if ( value instanceof Reference ) {
                        return (Reference) value;
                    }
                    else if ( value != null ) {
                        String text = value.toString();
                        return new Reference( text );
                    }
                    return null;
                }
            },
            Reference.class
            );
        
        ConvertUtils.register(
            new Converter() {
                public Object convert(Class type, Object value) {
                    if ( value instanceof EnumeratedAttribute ) {
                        return (EnumeratedAttribute) value;
                    }
                    else if ( value instanceof String ) {
                        FormatterElement.TypeAttribute attr = new FormatterElement.TypeAttribute();
                        attr.setValue( (String) value );
                        return attr;
                    }
                    return null;
                }
                
            },
            FormatterElement.TypeAttribute.class
            );
    }

        
    public AntTagLibrary() {
        this.project = createProject();
    }

    public AntTagLibrary(Project project) {
        this.project = project;
    }


    /**
     * A helper method which will attempt to find a project in the current context
     * or install one if need be.
     * 
     * #### this method could move to an AntUtils class.
     */
    public static Project getProject(JellyContext context) {
        Project project = (Project) context.findVariable( "org.apache.commons.jelly.ant.Project" );
        if ( project == null ) {
            project = createProject();
            context.setVariable( "org.apache.commons.jelly.ant.Project", project );
        }
        return project;
    }

    /**
     * A helper method to create a new project
     * 
     * #### this method could move to an AntUtils class.
     */    
    public static Project createProject() {
        Project project = new Project();

        BuildLogger logger = new NoBannerLogger();

        logger.setMessageOutputLevel( org.apache.tools.ant.Project.MSG_INFO );
        logger.setOutputPrintStream( System.out );
        logger.setErrorPrintStream( System.err);

        project.addBuildListener( logger );
        
        project.init();
        return project;
    }


    /** Creates a new script to execute the given tag name and attributes */
    public TagScript createTagScript(String name, Attributes attributes) throws Exception {

        Project project = getProject();
        
        // custom Ant tags
        if ( name.equals("fileScanner") ) {            
            Tag tag = new FileScannerTag(new FileScanner(project));
            return TagScript.newInstance(tag);
        }
        
        // is it an Ant task?
        Class type = (Class) project.getTaskDefinitions().get(name);
        if ( type != null ) {            
            TaskTag tag = new TaskTag( project, type, name );
            tag.setTrim( true );

            if ( name.equals( "echo" ) ) {
                tag.setTrim(false);
            }
            return TagScript.newInstance(tag);
        }
        
        /*
        // an Ant DataType?
        DataType dataType = null;
        type = (Class) project.getDataTypeDefinitions().get(name);
        
        if ( type != null ) {            

            try {
                java.lang.reflect.Constructor ctor = null;
                boolean noArg = false;
                // DataType can have a "no arg" constructor or take a single 
                // Project argument.
                try {
                    ctor = type.getConstructor(new Class[0]);
                    noArg = true;
                } catch (NoSuchMethodException nse) {
                    ctor = type.getConstructor(new Class[] { Project.class });
                    noArg = false;
                }
                
                if (noArg) {
                    dataType = (DataType) ctor.newInstance(new Object[0]);
                } else {
                    dataType = (DataType) ctor.newInstance(new Object[] {project});
                }
                dataType.setProject( project );

            } catch (Throwable t) {
                t.printStackTrace();
                // ignore 
            }
        }
        if ( dataType != null ) {
            DataTypeTag tag = new DataTypeTag( name, dataType );
            tag.setAntProject( getProject() );
            tag.getDynaBean().set( "project", project );
            return TagScript.newInstance(tag);
        }
        */
        
        // Since ant resolves so many dynamically loaded/created
        // things at run-time, we can make virtually no assumptions
        // as to what this tag might be.  
        Tag tag = new OtherAntTag( project,
                                   name );

        return TagScript.newInstance( tag );
    }

    public TagScript createRuntimeTaskTagScript(String taskName, Attributes attributes) throws Exception {
        TaskTag tag = new TaskTag( project, taskName );
        return TagScript.newInstance( tag );
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
