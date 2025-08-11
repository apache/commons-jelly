package org.apache.commons.jelly.tags.ant;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

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

import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.JavaEnvUtils;

/** Implements the basic {@link org.apache.commons.jelly.tags.ant.PropsHandler} functionality
 *  against an existing map.
 *
 * <p>
 * If extending {@code DefaultPropsHandler}, you can
 * implement {@code setProperty}, {@code getProperty},
 * and {@code getProperties} to provide a complete
 * implementation of {@code PropsHandler}.
 *
 */
public class DefaultPropsHandler implements PropsHandler {

    /** A map of all of the properties. */
    protected Map properties;

    /** A history of the properties marked as user properties. */
    protected Map userProperties = new HashMap();

    /** A history of the properties marked as inherited properties. */
    protected Map inheritedProperties = new HashMap();

    /** Initializes hte object with a blank set of properties.
     */
    public DefaultPropsHandler() {
        this.properties = new HashMap();
    }

    /** Initializes the object with a given {@code Map}
     * implementation.
     *
     * @param properties The {@code Map} to use to store and retrieve properties.
     */
    public DefaultPropsHandler(final Map properties) {
        this.properties = properties;
    }

    /**
     * @see PropsHandler#copyInheritedProperties(Project)
     */
    @Override
    public void copyInheritedProperties(final Project other) {
        final Hashtable inheritedProps = this.getInheritedProperties();

        final Enumeration e = inheritedProps.keys();
        while (e.hasMoreElements()) {
            final String name = e.nextElement().toString();
            if (other.getUserProperty(name) != null) {
                continue;
            }
            final Object value = inheritedProps.get(name);
            other.setInheritedProperty(name, value.toString());
        }
    }

    /**
     * @see PropsHandler#copyUserProperties(Project)
     */
    @Override
    public void copyUserProperties(final Project other) {
        final Hashtable userProps = this.getUserProperties();
        final Hashtable inheritedProps = this.getInheritedProperties();

        final Enumeration e = userProps.keys();
        while (e.hasMoreElements()) {
            final Object name = e.nextElement();
            if (inheritedProps.contains(name)) {
                continue;
            }
            final Object value = userProps.get(name);
            other.setUserProperty(name.toString(), value.toString());
        }
    }

    public Hashtable getInheritedProperties() {
        return new Hashtable(this.inheritedProperties);
    }

    /**
     * @see PropsHandler#getProperties()
     */
    @Override
    public Hashtable getProperties() {
        return new Hashtable(this.properties);
    }

    /**
     * @see PropsHandler#getProperty(String)
     */
    @Override
    public String getProperty(final String key) {
        if (key == null) {
            return null;
        }
        return (String) this.properties.get(key);
    }

    /**
     * @see PropsHandler#getUserProperties()
     */
    @Override
    public Hashtable getUserProperties() {
        return new Hashtable(this.userProperties);
    }

    /**
     * @see PropsHandler#getUserProperty(String)
     */
    @Override
    public String getUserProperty(final String key) {
        if (key == null) {
            return null;
        }
        return (String) this.userProperties.get(key);
    }

    /**
     * @see PropsHandler#setInheritedProperty(String, String)
     */
    @Override
    public void setInheritedProperty(final String key, final String value) {
        this.inheritedProperties.put(key, value);
        this.setUserProperty(key, value);
    }

    /**
     * @see PropsHandler#setJavaVersionProperty
     */
    @Override
    public void setJavaVersionProperty() {
        final String javaVersion = JavaEnvUtils.getJavaVersion();
        this.setPropertyIfUndefinedByUser("ant.java.version", javaVersion);
    }

    /**
     * @see PropsHandler#setNewProperty(String, String)
     */
    @Override
    public void setNewProperty(final String key, final String value) {
        if (this.getProperty(key) == null) {
            this.setProperty(key, value);
        }
    }

    /**
     * @see PropsHandler#setProperty(String, String)
     */
    @Override
    public void setProperty(final String key, final String value) {
        this.properties.put(key, value);
    }

    /**
     * @see PropsHandler#setPropertyIfUndefinedByUser(String, String)
     */
    @Override
    public void setPropertyIfUndefinedByUser(final String key, final String value) {
        if (!this.getUserProperties().contains(key)) {
            this.setProperty(key, value);
        }
    }

    /**
     * @see PropsHandler#setSystemProperties
     */
    @Override
    public void setSystemProperties() {
        final Properties systemProps = System.getProperties();
        final Enumeration e = systemProps.keys();
        while (e.hasMoreElements()) {
            final Object name = e.nextElement();
            final String value = systemProps.get(name).toString();
            this.setPropertyIfUndefinedByUser(name.toString(), value);
        }
    }

    /**
     * @see PropsHandler#setUserProperty(String, String)
     */
    @Override
    public void setUserProperty(final String key, final String value) {
        this.userProperties.put(key, value);
        this.setProperty(key, value);
    }
}
