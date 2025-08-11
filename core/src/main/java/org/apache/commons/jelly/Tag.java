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

package org.apache.commons.jelly;

/**
 * {@code Tag} represents a Jelly custom tag. A Tag is only ever used by a single thread so that Tag developers do not need to concern themselves with
 * mutli-threading issues when writing a Tag. A Tag is created per custom tag in a script, per invocation. So there is no need to worry about pooling errors
 * like those caused in JSP 1.x.(
 */
public interface Tag {

    /**
     * Evaluates this tag after all the tags properties have been initialized.
     *
     * @param output output stream.
     * @throws MissingAttributeException Thrown on error.
     * @throws JellyTagException Thrown on error.
     */
    void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException;

    /**
     * @return the body of the tag.
     */
    Script getBody();

    /**
     * Gets the context in which the tag will be run.
     *
     * @return the context in which the tag will be run
     */
    JellyContext getContext();

    /**
     * Gets the parent of this tag.
     *
     * @return the parent of this tag.
     */
    Tag getParent();

    /**
     * Gets the TagLibrary, null if this is an unrecognized tag (for example, a StaticTag)
     *
     * @return the TagLibrary.
     * @since 1.1.0
     */
    default TagLibrary getTagLib() {
        return null;
    }

    /**
     * A helper method to invoke this tags body
     *
     * @param output XML output stream.
     * @throws JellyTagException Thrown on error.
     */
    void invokeBody(XMLOutput output) throws JellyTagException;

    /**
     * Sets the body of the tag.
     *
     * @param body the body of the tag.
     */
    void setBody(Script body);

    /**
     * Sets the context in which the tag will be run.
     *
     * @param context the context in which the tag will be run.
     * @throws JellyTagException Thrown on error.
     */
    void setContext(JellyContext context) throws JellyTagException;

    /**
     * Sets the parent of this tag.
     *
     * @param parent the parent of this tag.
     */
    void setParent(Tag parent);

    /**
     * Sets the tag library. Defaults to do nothing.
     *
     * @param tagLibrary the tag library.
     * @since 1.1.0
     */
    default void setTagLib(TagLibrary tagLibrary) {
        // noop
    }
}
