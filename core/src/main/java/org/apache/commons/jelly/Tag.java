/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jelly;

/**
 * <p><code>Tag</code> represents a Jelly custom tag.
 * A Tag is only ever used by a single thread so that Tag developers do not
 * need to concern themselves with mutli-threading issues when writing a Tag.
 * A Tag is created per custom tag in a script, per invocation.
 * So there is no need to worry about pooling errors like those caused
 * in JSP 1.x.(</p>
 */
public interface Tag {

    /**
     * @return the parent of this tag
     */
    public Tag getParent();

    /**
     * Sets the parent of this tag
     */
    public void setParent(Tag parent);
    
    /**
     * Returns the TagLibrary - will be null if this is an unrecognized tag 
     * (ie a StaticTag) 
     * @return
     */
    public TagLibrary getTagLib();
    
    /**
     * Sets the tag library
     * @param tagLibrary
     */
    public void setTagLib(TagLibrary tagLibrary);

    /**
     * @return the body of the tag
     */
    public Script getBody();

    /**
     * Sets the body of the tag
     */
    public void setBody(Script body);

    /**
     * Gets the context in which the tag will be run
     */
    public JellyContext getContext();

    /**
     * Sets the context in which the tag will be run
     */
    public void setContext(JellyContext context) throws JellyTagException;

    /**
     * Evaluates this tag after all the tags properties have been initialized.
     */
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException;

    /**
     * A helper method to invoke this tags body
     */
    public void invokeBody(XMLOutput output) throws JellyTagException;

}
