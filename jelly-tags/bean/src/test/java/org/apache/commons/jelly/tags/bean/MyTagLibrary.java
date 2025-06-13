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
package org.apache.commons.jelly.tags.bean;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.impl.TagFactory;
import org.apache.commons.jelly.impl.TagScript;
import org.xml.sax.Attributes;

/**
 * A normal tag library which will use a BeanTag to create beans but this tag
 * library does not derive from BeanTagLibrary and so does not have a &lt;
 * beandef&gt; tag
 */
public class MyTagLibrary extends TagLibrary {

    public MyTagLibrary() {
    }

    /**
     * Factory method to create a Tag for the given tag and attributes. If this
     * tag matches a root bean, then a BeanTag will be created, otherwise a
     * BeanPropertyTag is created to make a nested property.
     */
    protected Tag createBeanTag(final String name, final Attributes attributes) throws JellyException {
        // is the name bound to a specific class
        final Class beanType = getBeanType(name, attributes);
        if (beanType != null) {
            return new BeanTag(beanType, name);
        }

        // its a property tag
        return new BeanPropertyTag(name);
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    // TagLibrary interface
    //-------------------------------------------------------------------------
    @Override
    public TagScript createTagScript(final String name, final Attributes attributes) throws JellyException {

        final TagFactory factory = this::createBeanTag;
        return new TagScript( factory );
    }

    /**
     * Gets the bean class that we should use for the given element name
     *
     * @param name is the XML element name
     * @param attributes the XML attributes
     * @return Class the bean class to use for this element or null if the tag
     * is a nested property
     */
    protected Class getBeanType(final String name, final Attributes attributes) {
        if (name.equals( "customer")) {
            return Customer.class;
        }
        return null;
    }
}
