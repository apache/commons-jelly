package org.apache.commons.jelly.tags.ant;

/*
 * Copyright 1999-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Hashtable;

import org.apache.tools.ant.Project;

/** Interface for delegates supporting property management
 *  for a<code>GrantProject</code>.
 *
 *  @see org.apache.commons.jelly.tags.ant.GrantProject#setProperty
 *  @see org.apache.commons.jelly.tags.ant.GrantProject#setNewProperty
 *  @see org.apache.commons.jelly.tags.ant.GrantProject#setUserProperty
 *  @see org.apache.commons.jelly.tags.ant.GrantProject#setInheritedProperty
 *  @see org.apache.commons.jelly.tags.ant.GrantProject#getProperty
 *  @see org.apache.commons.jelly.tags.ant.GrantProject#getUserProperty
 *  @see org.apache.commons.jelly.tags.ant.GrantProject#getProperties
 *  @see org.apache.commons.jelly.tags.ant.GrantProject#getUserProperties
 *  @see org.apache.commons.jelly.tags.ant.GrantProject#copyUserProperties
 *  @see org.apache.commons.jelly.tags.ant.GrantProject#copyInheritedProperties
 *  @see org.apache.commons.jelly.tags.ant.GrantProject#setSystemProperties
 *  @see org.apache.commons.jelly.tags.ant.GrantProject#setJavaVersionProperty
 *
 *  @author <a href="mailto:bob@eng.werken.com">Bob McWhirter</a>
 *  @author <a href="mailto:stephenh@chase3000.com">Stephen Haberman</a>
 */
public interface PropsHandler {

    /** Set a property.
     *
     *  @param key The property key.
     *  @param value The value.
     */
    void setProperty(String key, String value);

    /** Set a user property.
     *
     *  @param key The user property key.
     *  @param value The value.
     */
    void setUserProperty(String key, String value);

    /** Set a new property.
     *
     *  @param key The property key.
     *  @param value The value.
     */
    void setNewProperty(String key, String value);

    /** Sets an inherited property.
     *
     *  @param key The user property key.
     *  @param value The value.
     */
    void setInheritedProperty(String key, String value);
    
    /** Sets a property that is not a user property.
     * 
     * Acts as the replacement for ant's private 
     * <code>setPropertyInternal</code> method.
     * 
     * @param key The property key.
     * @param value The value.
     */
    void setPropertyIfUndefinedByUser(String key, String value);

    /** Retrieve a property.
     *
     *  @param key The property key.
     *
     *  @return The value.
     */
    String getProperty(String key);

    /** Retrieve a user property.
     *
     *  @param key The user property key.
     *
     *  @return The value.
     */
    String getUserProperty(String key);

    /** Retrieve a <code>Hashtable</code> of all properties.
     *
     *  @return A <code>Hashtable</code> of all properties.
     */
    Hashtable getProperties();

    /** Retrieve a <code>Hashtable</code> of all user properties.
     *
     *  @return A <code>Hashtable</code> of all user properties.
     */
    Hashtable getUserProperties();
    
    /** Copy all of the user properties to the other <code>Project</code>.
     * 
     * @param other The <code>Project</code> to copy the properties to.
     */
    void copyUserProperties(Project other);
    
    /** Copy all of the inherited properties to the other <code>Project</code>.
     * 
     * @param other The <code>Project</code> to copy the properties to.
     */
    void copyInheritedProperties(Project other);
    
    /** Set the system variables for a <code>Project</code> that have
     * not already been assigned as user properties.
     */
    void setSystemProperties();
    
    /** Set the <code>ant.java.version</code> property.
     */
    void setJavaVersionProperty();    
    
    
}
