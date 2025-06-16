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

package org.apache.commons.jelly.tags.jmx;

import java.util.ArrayList;
import java.util.List;

import javax.management.ObjectName;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.CollectionTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Registers a JavaBean or JMX MBean with a server.
 */
public class OperationTag extends TagSupport implements CollectionTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(OperationTag.class);

    private String name;
    private Object arguments;
    private List argList = null;
    private String[] parameters;

    public OperationTag() {
    }

    // CollectionTag interface
    //-------------------------------------------------------------------------
    @Override
    public void addItem(final Object value) {
        if (argList == null) {
            argList = new ArrayList();
        }
        argList.add(value);
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (name == null) {
            throw new MissingAttributeException("name");
        }

        final RegisterTag registerTag = (RegisterTag) findAncestorWithClass(RegisterTag.class);
        if (registerTag == null) {
            throw new JellyTagException("This class must be nested inside a <register> tag");
        }
        final Object bean = null;
        try {
            invokeBody(output);

            final ObjectName objectName = registerTag.getName();
            registerTag.getServer().invoke(objectName, getName(), getArgumentArray(), getParameters());
        }
        catch (final JellyTagException e) {
            throw e;
        }
        catch (final Exception e) {
            throw new JellyTagException("Failed to register bean: " + bean, e);
        }
        finally {
            argList = null;
        }
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Converts the argument property into an Object[] or converts the list of
     * added argument objects (added via child tags) to an Object[] or
     * return an empty argument array.
     */
    protected Object[] getArgumentArray() {
        final Object arg = getArguments();
        if (arg != null) {
            if (arg instanceof Object[]) {
                return (Object[]) arg;
            }
            return new Object[] { arg };
        }
        if (argList != null) {
            return argList.toArray();
        }
        return new Object[0];
    }

    /**
     * @return Object
     */
    public Object getArguments() {
        return arguments;
    }

    /**
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * @return String[]
     */
    public String[] getParameters() {
        return parameters;
    }

    /**
     * Sets the arguments.
     * @param arguments The arguments to set
     */
    public void setArguments(final Object arguments) {
        this.arguments = arguments;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the parameters.
     * @param parameters The parameters to set
     */
    public void setParameters(final String[] parameters) {
        this.parameters = parameters;
    }
}
