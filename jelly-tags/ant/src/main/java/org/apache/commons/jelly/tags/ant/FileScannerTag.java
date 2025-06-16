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

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * A tag which creates a new FileScanner bean instance that can be used to
 * iterate over fileSets
 */
public class FileScannerTag extends TagSupport implements TaskSource {

    /** The file walker that gets created */
    private final FileScanner fileScanner;

    /** The variable exported */
    private String var;

    public FileScannerTag(final FileScanner fileScanner) {
        this.fileScanner = fileScanner;
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        fileScanner.setProject(AntTagLibrary.getProject(context));

        fileScanner.clear();

        // run the body first to configure the task via nested
        invokeBody(output);

        // output the fileScanner
        if ( var == null ) {
            throw new MissingAttributeException( "var" );
        }
        context.setVariable( var, fileScanner );

    }

    /**
     * @return the Ant task
     */
    public FileScanner getFileScanner() {
        return fileScanner;
    }

    // TaskSource interface
    //-------------------------------------------------------------------------
    @Override
    public Object getTaskObject() {
        return fileScanner;
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Allows nested tags to set a property on the task object of this tag
     */
    @Override
    public void setTaskProperty(final String name, final Object value) throws JellyTagException {
        try {
            BeanUtils.setProperty( fileScanner, name, value );
        }
        catch (final IllegalAccessException | InvocationTargetException ex) {
            throw new JellyTagException(ex);
        }
    }

    /** Sets the name of the variable exported by this tag */
    public void setVar(final String var) {
        this.var = var;
    }

}
