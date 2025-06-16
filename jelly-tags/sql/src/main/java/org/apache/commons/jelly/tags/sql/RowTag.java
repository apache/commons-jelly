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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MapTagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * Adds a new row to a parent &lt;resultSet&gt; Tag.
 * This tag is useful for unit testing with Mock Tags to simulate the results returned by databases.
 */
public class RowTag extends MapTagSupport {

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        final ResultSetTag tag = (ResultSetTag) findAncestorWithClass( ResultSetTag.class );
        if ( tag == null ) {
            throw new JellyTagException( "This tag must be nested with in a <resultSet> tag" );
        }
        tag.addRow( getAttributes() );
    }
}
