/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/xml/XMLTagLibrary.java,v 1.6 2002/05/17 18:04:00 jstrachan Exp $
 * $Revision: 1.6 $
 * $Date: 2002/05/17 18:04:00 $
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.grant.GrantProject;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.impl.TagFactory;
import org.apache.commons.jelly.impl.TagScript;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.NoBannerLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Reference;

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
    
    public static final String PROJECT_CONTEXT_HANDLE = "org.apache.commons.jelly.ant.Project";

    static {

        // register standard converters for Ant types
               
        
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


    /**
     * A helper method which will attempt to find a project in the current context
     * or install one if need be.
     * 
     * #### this method could move to an AntUtils class.
     */
    public static Project getProject(JellyContext context) {
        Project project = (Project) context.findVariable( PROJECT_CONTEXT_HANDLE );
        if ( project == null ) {
            project = createProject(context);
            context.setVariable( PROJECT_CONTEXT_HANDLE , project );
        }
        return project;
    }

    /**
     * Sets the Ant Project to be used for this JellyContext.
     * 
     * #### this method could move to an AntUtils class.
     */
    public static void setProject(JellyContext context, Project project) {
        context.setVariable( PROJECT_CONTEXT_HANDLE, project );
    }

    /**
     * A helper method to create a new project
     * 
     * #### this method could move to an AntUtils class.
     */    
    public static Project createProject(JellyContext context) {
        GrantProject project = new GrantProject();
        project.setPropsHandler(new JellyPropsHandler(context));

        BuildLogger logger = new NoBannerLogger();

        logger.setMessageOutputLevel( org.apache.tools.ant.Project.MSG_INFO );
        logger.setOutputPrintStream( System.out );
        logger.setErrorPrintStream( System.err);

        project.addBuildListener( logger );
        
        project.init();
        project.getBaseDir();

        return project;
    }


    /** Creates a new script to execute the given tag name and attributes */
    public TagScript createTagScript(String name, Attributes attributes) throws JellyException {
        TagScript answer = createCustomTagScript(name, attributes);
        if ( answer == null ) {
            answer = new TagScript(
                new TagFactory() {
                    public Tag createTag(String name, Attributes attributes) throws JellyException {
                        return AntTagLibrary.this.createTag(name, attributes);
                    }
                }
            );
        }
        return answer;
    }

    /** 
     * @return a new TagScript for any custom, statically defined tags, like 'fileScanner'
     */
    public TagScript createCustomTagScript(String name, Attributes attributes) throws JellyException {
        // custom Ant tags
        if ( name.equals("fileScanner") ) {      
            return new TagScript(
                new TagFactory() {
                    public Tag createTag(String name, Attributes attributes) throws JellyException {
                        return new FileScannerTag(new FileScanner());
                    }
                }
            );      
        }
        if ( name.equals("setProperty") ) {      
            return new TagScript(
                new TagFactory() {
                    public Tag createTag(String name, Attributes attributes) throws JellyException {
                        return new SetPropertyTag();
                    }
                }
            );      
        }
        return null;
    }

    /**
     * A helper method which creates an AntTag instance for the given element name
     */
    public Tag createTag(String name, Attributes attributes) throws JellyException {
        AntTag tag = new AntTag( name );
        if ( name.equals( "echo" ) ) {
            tag.setTrim(false);
        }
        return tag;
    }


}
