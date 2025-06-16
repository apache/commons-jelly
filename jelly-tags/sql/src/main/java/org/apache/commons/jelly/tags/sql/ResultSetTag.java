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
package org.apache.commons.jelly.tags.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * This Tag creates a result set object based on its body content via child row tags.
 * This tag is useful for unit testing with Mock Tags to simulate the results returned by databases.
 */
public class ResultSetTag extends TagSupport {

    private List rows;
    private String var;

    /**
     * Adds the given row to the list of rows
     */
    public void addRow(final Map row) {
        rows.add(row);
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (var == null) {
            throw new MissingAttributeException( "var" );
        }
        rows = new ArrayList();
        invokeBody(output);

        // now lets create a new Result implementation
        final ResultImpl results = new ResultImpl( rows );
        context.setVariable(var, results);
        rows = null;
    }

    // Properties
    //-------------------------------------------------------------------------
    /**
     * Sets the variable to export the result set to.
     */
    public void setVar(final String var) {
        this.var = var;
    }

}
