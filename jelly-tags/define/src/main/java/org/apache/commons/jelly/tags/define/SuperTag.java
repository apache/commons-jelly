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
package org.apache.commons.jelly.tags.define;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * &lt;super&gt; tag is used to invoke a parent tag implementation, when
 * a tag extends an existing tag
 * @see ExtendTag
 */
public class SuperTag extends TagSupport {

    public SuperTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(XMLOutput output) throws JellyTagException {
        ExtendTag tag = (ExtendTag) findAncestorWithClass(ExtendTag.class);
        if (tag == null) {
            throw new JellyTagException(
                "<define:super> must be inside a <define:extend>");
        }

        tag.getSuperScript().run(getContext(), output);
    }
}

