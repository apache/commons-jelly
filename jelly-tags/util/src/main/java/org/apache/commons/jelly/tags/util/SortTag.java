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

package org.apache.commons.jelly.tags.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils2.BeanComparator;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

public class SortTag extends TagSupport {

    /** Things to sort */
    private List items;

    /** The variable to store the result in */
    private String var;

    /** Property of the beans to sort on, if any */
    private String property;

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        if (var == null)
        {
            throw new MissingAttributeException("var");
        }

        if (items == null) {
            throw new MissingAttributeException("items");
        }

        final List sorted = new ArrayList(items);
        if (property == null) {
            Collections.sort(sorted);
        } else {
            final BeanComparator comparator = new BeanComparator(property);
            Collections.sort(sorted, comparator);
        }
        context.setVariable(var, sorted);
    }

    /**
     * Sets the items to be sorted
     * @param newItems some collection
     */
    public void setItems(final List newItems) {
        items = newItems;
    }

    public void setProperty(final String newProperty)
    {
        property = newProperty;
    }

    /**
     * The variable to hold the sorted collection.
     * @param newVar the name of the variable.
     */
    public void setVar(final String newVar) {
        var = newVar;
    }
}