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

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Tag;
import org.xml.sax.Attributes;

/**
 * <p><code>DefaultTagFactory</code> a default implementation of TagFactory
 * which creates new instances of a given class.
 */
public class DefaultTagFactory implements TagFactory {

    private Class tagClass;

    public DefaultTagFactory() {
    }

    public DefaultTagFactory(final Class tagClass) {
        this.tagClass = tagClass;
    }

    // TagFactory interface
    //-------------------------------------------------------------------------
    @Override
    public Tag createTag(final String name, final Attributes attributes) throws JellyException {
        try {
          return (Tag) tagClass.getConstructor().newInstance();
        } catch (final ReflectiveOperationException e) {
            throw new JellyException(e.toString());
        }
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Returns the tagClass.
     * @return Class
     */
    public Class getTagClass() {
        return tagClass;
    }

    /**
     * Sets the tagClass.
     * @param tagClass The tagClass to set
     */
    public void setTagClass(final Class tagClass) {
        this.tagClass = tagClass;
    }

}
