package org.apache.commons.jelly.tags.ant;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.JavaEnvUtils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/** Implements the basic {@link org.apache.commons.jelly.tags.ant.PropsHandler} functionality
 *  against an existing map.
 * 
 * <p>
 * If extending <code>DefaultPropsHandler</code>, you can 
 * implement <code>setProperty</code>, <code>getProperty</code>,
 * and <code>getProperties</code> to provide a complete
 * implementation of <code>PropsHandler</code>.
 * 
 *  @author <a href="mailto:stephenh@chase3000.com">Stephen Haberman</a>
 *  @version $Revision$
 */
public class DefaultPropsHandler implements PropsHandler {

    /** A map of all of the properties. */
    protected Map properties;

    /** A history of the properties marked as user properties. */
    protected Map userProperties = new HashMap();

    /** A history of the properties makred as inherited properties. */
    protected Map inheritedProperties = new HashMap();
    
    /** Initializes hte object with a blank set of properties.
     */
    public DefaultPropsHandler() {
        this.properties = new HashMap();
    }

    /** Initializes the object with a given <code>Map</code>
     * implementation.
     *
     * @param properties The <code>Map</code> to use to store and retrieve properties. 
     */
    public DefaultPropsHandler(Map properties) {
        this.properties = properties;
    }

    /**
     * @see PropsHandler#setProperty(String, String)
     */
    public void setProperty(String key, String value) {
        this.properties.put(key, value);
    }

    /**
     * @see PropsHandler#setUserProperty(String, String)
     */
    public void setUserProperty(String key, String value) {
        this.userProperties.put(key, value);
        this.setProperty(key, value);
    }

    /**
     * @see PropsHandler#setNewProperty(String, String)
     */
    public void setNewProperty(String key, String value) {
        if (this.getProperty(key) == null) {
            this.setProperty(key, value);
        }
    }

    /**
     * @see PropsHandler#setInheritedProperty(String, String)
     */
    public void setInheritedProperty(String key, String value) {
        this.inheritedProperties.put(key, value);
        this.setUserProperty(key, value);
    }

    /**
     * @see PropsHandler#setPropertyIfUndefinedByUser(String, String)
     */
    public void setPropertyIfUndefinedByUser(String key, String value) {
        if (!this.getUserProperties().contains(key)) {
            this.setProperty(key, value);
        }
    }

    /**
     * @see PropsHandler#getProperty(String)
     */
    public String getProperty(String key) {
        if (key == null) {
            return null;
        }
        return (String) this.properties.get(key);
    }

    /**
     * @see PropsHandler#getUserProperty(String)
     */
    public String getUserProperty(String key) {
        if (key == null) {
            return null;
        }
        return (String) this.userProperties.get(key);
    }

    /**
     * @see PropsHandler#getProperties()
     */
    public Hashtable getProperties() {
        return new Hashtable(this.properties);
    }

    /**
     * @see PropsHandler#getUserProperties()
     */
    public Hashtable getUserProperties() {
        return new Hashtable(this.userProperties);
    }
    
    public Hashtable getInheritedProperties() {
        return new Hashtable(this.inheritedProperties);
    }
    
    /**
     * @see PropsHandler#copyUserProperties(Project)
     */
    public void copyUserProperties(Project other) {
        Hashtable userProps = this.getUserProperties();
        Hashtable inheritedProps = this.getInheritedProperties();
        
        Enumeration e = userProps.keys();
        while (e.hasMoreElements()) {
            Object name = e.nextElement();
            if (inheritedProps.contains(name)) {
                continue;
            }
            Object value = userProps.get(name);
            other.setUserProperty(name.toString(), value.toString());
        }
    }

    /**
     * @see PropsHandler#copyInheritedProperties(Project)
     */
    public void copyInheritedProperties(Project other) {
        Hashtable inheritedProps = this.getInheritedProperties();
        
        Enumeration e = inheritedProps.keys();
        while (e.hasMoreElements()) {
            String name = e.nextElement().toString();
            if (other.getUserProperty(name) != null) {
                continue;
            }
            Object value = inheritedProps.get(name);
            other.setInheritedProperty(name, value.toString());
        }
    }
    
    /**
     * @see PropsHandler#setSystemProperties
     */
    public void setSystemProperties() {
        Properties systemProps = System.getProperties();
        Enumeration e = systemProps.keys();
        while (e.hasMoreElements()) {
            Object name = e.nextElement();
            String value = systemProps.get(name).toString();
            this.setPropertyIfUndefinedByUser(name.toString(), value);
        }
    }

    /**
     * @see PropsHandler#setJavaVersionProperty
     */
    public void setJavaVersionProperty() {
        String javaVersion = JavaEnvUtils.getJavaVersion();
        this.setPropertyIfUndefinedByUser("ant.java.version", javaVersion);
    }
}
