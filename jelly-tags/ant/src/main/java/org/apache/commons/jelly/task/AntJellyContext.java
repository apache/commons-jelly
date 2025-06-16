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

package org.apache.commons.jelly.task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Project;

/** <p><code>AntJellyContext</code> represents the Jelly context from inside Ant.</p>
  */

public class AntJellyContext extends JellyContext {

    /** The Ant project which contains the variables */
    private final Project project;

    /** The Log to which logging calls will be made. */
    private final Log log = LogFactory.getLog(AntJellyContext.class);

    public AntJellyContext(final Project project, final JellyContext parentJellyContext) {
        super( parentJellyContext );
        this.project = project;
    }

    /**
     * Factory method to create a new child of this context
     */
    @Override
    protected JellyContext createChildContext() {
        return new AntJellyContext(project, this);
    }

    /** @return the value of the given variable name */
    @Override
    public Object getVariable(final String name) {
        // look in parent first
        Object answer = super.getVariable(name);
        if (answer == null) {
            answer = project.getProperty(name);
        }

        if (log.isDebugEnabled()) {
            String answerString = null;
            try {
                answerString = answer.toString();
            } catch (final Exception ex) {
            }
            if (answerString == null && answer != null) {
                answerString = " of class " + answer.getClass();
            }
            log.debug("Looking up variable: " + name + " answer: " + answerString);
        }

        return answer;
    }

    /**
     * @return an Iterator over the current variable names in this
     * context
     */
    @Override
    public Iterator getVariableNames() {
        return getVariables().keySet().iterator();
    }

    /**
     * @return the Map of variables in this scope
     */
    @Override
    public Map getVariables() {
        // we should add all the Project's properties
        final Map map = new HashMap( project.getProperties() );

        // override any local properties
        map.putAll( super.getVariables() );
        return map;
    }

    /** Removes the given variable */
    @Override
    public void removeVariable(final String name) {
        super.removeVariable( name );
        project.setProperty(name, null);
    }

    /** Sets the value of the given variable name */
    @Override
    public void setVariable(final String name, final Object value) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Setting variable: " + name + " to: " + value );
        }

        super.setVariable( name, value );

        // only export string values back to Ant?
        if ( value instanceof String ) {
            project.setProperty(name, (String) value);
        }
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the Map of variables to use
     */

    @Override
    public void setVariables(final Map variables) {
        super.setVariables(variables);

        // export any Ant properties
        for ( final Iterator iter = variables.entrySet().iterator(); iter.hasNext(); ) {
            final Map.Entry entry = (Map.Entry) iter.next();
            final String key = (String) entry.getKey();
            final Object value = entry.getValue();
            if ( value instanceof String ) {
                project.setProperty(key, (String)value);
            }
        }
    }

}
