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

import org.apache.commons.jelly.JellyTagException;

/**
 * A tag which is capable of consuming objects, such as a &lt;useList&gt; tag
 * such that nested objects will be added to the parent tag.
 */
public interface CollectionTag {

    /**
     * Adds an item to the tags collection.
     *
     * @param value The item to add
     * @throws JellyTagException Thrown when the recevier cannot add the item.
     */
    public void addItem(Object value) throws JellyTagException;
}
