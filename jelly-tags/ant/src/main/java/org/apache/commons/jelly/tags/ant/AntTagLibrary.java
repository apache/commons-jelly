/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly.tags.ant;

import org.apache.commons.beanutils2.ConvertUtils;
import org.apache.commons.beanutils2.Converter;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.impl.TagScript;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.NoBannerLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Reference;
import org.xml.sax.Attributes;

/**
 * A Jelly custom tag library that allows Ant tasks to be called from inside Jelly.
 */
public class AntTagLibrary extends TagLibrary {

    public static final String PROJECT_CONTEXT_HANDLE = "org.apache.commons.jelly.ant.Project";

    static {

        // register standard converters for Ant types

        ConvertUtils.register(
                new Converter() {
                    @Override
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
                    @Override
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
     * A helper method to create a new project
     *
     * #### this method could move to an AntUtils class.
     */
    public static Project createProject(final JellyContext context) {
        final GrantProject project = new GrantProject();
        project.setPropsHandler(new JellyPropsHandler(context));

        final BuildLogger logger = new NoBannerLogger();

        logger.setMessageOutputLevel( org.apache.tools.ant.Project.MSG_INFO );
        logger.setOutputPrintStream( System.out );
        logger.setErrorPrintStream( System.err);

        project.addBuildListener( logger );

        project.init();
        project.getBaseDir();
        if (context.getCurrentURL() != null) {
            project.setProperty("ant.file",
                    context.getCurrentURL().toExternalForm());
        }

        return project;
    }

    /**
     * A helper method which will attempt to find a project in the current context
     * or install one if need be.
     *
     * #### this method could move to an AntUtils class.
     */
    public static Project getProject(final JellyContext context) {
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
    public static void setProject(final JellyContext context, final Project project) {
        context.setVariable( PROJECT_CONTEXT_HANDLE, project );
    }

    /**
     * @return a new TagScript for any custom, statically defined tags, like 'fileScanner'
     */
    public TagScript createCustomTagScript(final String name, final Attributes attributes) {
        // custom Ant tags
        if ( name.equals("fileScanner") ) {
            return new TagScript(
                (name1, attributes1) -> new FileScannerTag(new FileScanner())
            );
        }
        if ( name.equals("setProperty") ) {
            return new TagScript(
                (name1, attributes1) -> new SetPropertyTag()
            );
        }
        return null;
    }

    /**
     * A helper method which creates an AntTag instance for the given element name
     */
    @Override
    public Tag createTag(final String name, final Attributes attributes) throws JellyException {
        final AntTag tag = new AntTag( name );
        if ( name.equals( "echo" ) ) {
            tag.setTrim(false);
        }
        return tag;
    }

    /** Creates a new script to execute the given tag name and attributes */
    @Override
    public TagScript createTagScript(final String name, final Attributes attributes) throws JellyException {
        TagScript answer = createCustomTagScript(name, attributes);
        if ( answer == null ) {
            answer = new TagScript(
                AntTagLibrary.this::createTag
            );
        }
        return answer;
    }

}
