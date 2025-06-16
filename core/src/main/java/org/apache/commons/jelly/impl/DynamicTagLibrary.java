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
package org.apache.commons.jelly.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.xml.sax.Attributes;

/**
 * <p><code>DynamicTagLibrary</code> represents a TagLibrary which
 * gets created by running a Jelly script.</p>
 */
public class DynamicTagLibrary extends TagLibrary {

    private String uri;
    private final Map templates = new HashMap();
    private TagLibrary parent;

    public DynamicTagLibrary() {
    }

    public DynamicTagLibrary(final String uri) {
        this.uri = uri;
    }

    /** Creates a new Tag for the given tag name if it exists */
    @Override
    public Tag createTag(final String name, final Attributes attributes)
        throws JellyException {

        final Object value = templates.get(name);
        if ( value instanceof Script ) {
            final Script template = (Script) value;
            return new DynamicTag(template);
        }
        if ( value instanceof TagFactory ) {
            final TagFactory factory = (TagFactory) value;
            return factory.createTag(name, attributes);
        }
        if ( parent != null ) {
            // delegate to the parent
            return parent.createTag(name, attributes);
        }

        return null;
    }

    /** Creates a new script to execute the given tag name and attributes */
    @Override
    public TagScript createTagScript(final String name, final Attributes attributes)
        throws JellyException {

        return new TagScript(
            DynamicTagLibrary.this::createTag
        );
    }

    /**
     * Returns the tag library instance which contains the named tag.
     * <p>
     * If the tag is not registered within this library, the set of
     * parent libraries will be searched.
     * </p>
     * @param name The tag name
     * @return The tag library containing the named tag, or {@code null}
     *         if the tag is not registered.
     */
    public DynamicTagLibrary find(final String name) {
        DynamicTagLibrary result = null;
        if (templates.get(name) != null) {
            result = this;
        }
        else if (parent instanceof DynamicTagLibrary) {
            result = ((DynamicTagLibrary) parent).find(name);
        }
        return result;
    }

    /**
     * Returns the script associated with the given tag name
     *
     * @param name The tag name
     * @return The script associated with <code>name</code>, or
     *         {@code null} if the tag doesn't exist or isn't a script
     */
    public Script getDynamicTag(final String name) {
        final Object result = templates.get(name);
        return result instanceof Script ? (Script) result : null;
    }

    /**
     * Returns the parent library which will be used to resolve unknown tags.
     * @return TagLibrary
     */
    public TagLibrary getParent() {
        return parent;
    }

    // Properties
    //-------------------------------------------------------------------------
    public String getUri() {
        return uri;
    }

    /**
     * Creates a new Jelly Bean Tag with the given name
     */
    public void registerBeanTag(final String name, final TagFactory factory) {
        templates.put(name, factory);
    }

    /**
     * Creates a new tag with the given name and template
     */
    public void registerDynamicTag(final String name, final Script template) {
        templates.put(name, template);
    }

    /**
     * Sets the parent to inherit tags from that are not defined in this library.
     * @param parent The parent to set
     */
    public void setParent(final TagLibrary parent) {
        this.parent = parent;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

}
