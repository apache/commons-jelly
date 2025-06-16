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
package org.apache.commons.jelly.tags.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.impl.CollectionTag;

/**
 * A tag which creates a List implementation and optionally
 * adds all of the elements identified by the items attribute.
 * <p>
 * The exact implementation of List can be specified via the
 * class attribute
 * </p>
 */
public class UseListTag extends UseBeanTag implements CollectionTag {

    private Expression items;

    public UseListTag(){
    }

    // CollectionTag interface
    //-------------------------------------------------------------------------
    @Override
    public void addItem(final Object value) {
        getList().add(value);
    }

    // DynaTag interface
    //-------------------------------------------------------------------------
    @Override
    public Class getAttributeType(final String name) throws JellyTagException {
        if (name.equals("items")) {
            return Expression.class;
        }
        return super.getAttributeType(name);
    }

    @Override
    protected Class getDefaultClass() {
        return ArrayList.class;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    public List getList() {
        return (List) getBean();
    }

    @Override
    protected void processBean(final String var, final Object bean) throws JellyTagException {
        super.processBean(var, bean);

        final List list = getList();

        // if the items variable is specified lets append all the items
        if (items != null) {
            final Iterator iter = items.evaluateAsIterator(context);
            while (iter.hasNext()) {
                list.add( iter.next() );
            }
        }
    }

    @Override
    protected void setBeanProperties(final Object bean, final Map attributes) throws JellyTagException {
        items = (Expression) attributes.remove("items");
        super.setBeanProperties(bean, attributes);
    }
}
