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

import java.io.File;
import java.util.Hashtable;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/** A subclass of an ant <code>Project</code> which allows
 *  installation of delegators for particular functions.
 *
 *  <p>
 *  Current delegation points include:
 *
 *  <ul>
 *    <li>Properties</li>
 *  </ul>
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class GrantProject extends Project {

    /** Properties delegate. */
    private PropsHandler propsHandler;

    /** Constructs  a new, empty <code>GrantProject</code>.
     *
     *  <p>
     *  Immediately after initialization, a <code>GrantProject</code>
     *  delegates <b>all</b> calls to the normal ant <code>Project</code>
     *  super class.   Only after installing delegators will custom
     *  behavior be achieved.
     *  </p>
     */
    public GrantProject() {
        this.propsHandler = null;
    }

    /** Install a <code>PropsHandler</code> delegate.
     *
     *  @param propsHandler The <code>PropsHandler</code> to install,
     *         or {@code null} to remove any currently installed
     *         <code>PropsHandler</code>.
     */
    public void setPropsHandler(PropsHandler propsHandler) {
        this.propsHandler = propsHandler;
    }

    /** Retrieve the currently installed <code>PropsHandler</code>.
     *
     *  @return The currently installed <code>PropsHandler</code>,
     *          or {@code null} if no <code>PropsHandler</code>
     *          had yet to be installed.
     */
    public PropsHandler getPropsHandler() {
        return this.propsHandler;
    }

    // ------------------------------------------------------------
    //     org.apache.tools.ant.Project implementation
    // ------------------------------------------------------------

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //         properties delegators:
    //
    //         If a PropsHandler is not installed, delegate
    //         up the hierarchy to the ant.Project parent
    //         class.  Otherwise, delegate outwards using
    //         the PropsHandler.
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    @Override
    public String replaceProperties(String value) throws BuildException {
        return ProjectHelper.replaceProperties(this, value, getProperties());
    }

    @Override
    public synchronized void setProperty(String key, String value) {
        if (this.propsHandler == null) {
            super.setProperty(key, value);
        }
        else {
            this.propsHandler.setProperty(key, value);
        }
    }

    @Override
    public synchronized void setUserProperty(String key, String value) {
        if (this.propsHandler == null) {
            super.setUserProperty(key, value);
        }
        else {
            this.propsHandler.setUserProperty(key, value);
        }
    }

    @Override
    public synchronized void setNewProperty(String key, String value) {
        if (this.propsHandler == null) {
            super.setNewProperty(key, value);
        }
        else {
            this.propsHandler.setNewProperty(key, value);
        }
    }

    @Override
    public void setInheritedProperty(String key, String value) {
        if (this.propsHandler == null) {
            super.setInheritedProperty(key, value);
        }
        else {
            this.propsHandler.setInheritedProperty(key, value);
        }
    }

    @Override
    public String getProperty(String key) {
        if (this.propsHandler == null) {
            return super.getProperty(key);
        }

        return this.propsHandler.getProperty(key);
    }

    @Override
    public String getUserProperty(String key) {
        if (this.propsHandler == null) {
            return super.getUserProperty(key);
        }

        return this.propsHandler.getUserProperty(key);
    }

    @Override
    public Hashtable getUserProperties() {
        if (this.propsHandler == null) {
            return super.getUserProperties();
        }

        return this.propsHandler.getUserProperties();
    }

    @Override
    public Hashtable getProperties() {
        if (this.propsHandler == null) {
            return super.getProperties();
        }

        return this.propsHandler.getProperties();
    }

    @Override
    public void copyUserProperties(Project other) {
        if (this.propsHandler == null) {
            super.copyUserProperties(other);
        }
        else {
            this.propsHandler.copyUserProperties(other);
        }
    }

    @Override
    public void copyInheritedProperties(Project other) {
        if (this.propsHandler == null) {
            super.copyInheritedProperties(other);
        }
        else {
            this.propsHandler.copyInheritedProperties(other);
        }
    }

    @Override
    public void setSystemProperties() {
        if (this.propsHandler == null) {
            super.setSystemProperties();
        }
        else {
            this.propsHandler.setSystemProperties();
        }
    }

    @Override
    public void setJavaVersionProperty() throws BuildException {
        // Always call the super, as they do some sanity checks
        super.setJavaVersionProperty();

        if (this.propsHandler != null) {
            this.propsHandler.setJavaVersionProperty();
        }
    }

    @Override
    public void setBaseDir(File baseDir) throws BuildException {
        super.setBaseDir(baseDir);

        if (this.propsHandler != null) {
            this.propsHandler.setPropertyIfUndefinedByUser(
                "basedir",
                baseDir.getPath());
        }
    }
}
