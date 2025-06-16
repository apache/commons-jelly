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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * A tag which conditionally evaluates its body if
 * none of its preceeding sibling {@link CaseTag &lt;case&gt;}
 * tags have been evaluated.
 *
 * This tag must be contained within the body of some
 * {@link SwitchTag &lt;switch&gt;} tag.
 *
 * @see SwitchTag
 */
public class DefaultTag extends TagSupport {

    // Attributes
    //-------------------------------------------------------------------------
    private boolean fallThru = false;

    // Tag interface
    //-------------------------------------------------------------------------

    public DefaultTag() {
    }

    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        final SwitchTag tag = (SwitchTag)findAncestorWithClass(SwitchTag.class);
        if (null == tag) {
            throw new JellyTagException("This tag must be enclosed inside a <switch> tag" );
        }
        if (tag.hasDefaultBeenEncountered()) {
            throw new JellyTagException("Only one <default> tag is allowed per <switch>.");
        }
        tag.defaultEncountered();
        if (tag.isFallingThru() || !tag.hasSomeCaseMatched()) {
            tag.caseMatched();
            tag.setFallingThru(fallThru);
            invokeBody(output);
        }
    }

    public void setFallThru(final boolean fallThru) {
        this.fallThru = fallThru;
    }

}
