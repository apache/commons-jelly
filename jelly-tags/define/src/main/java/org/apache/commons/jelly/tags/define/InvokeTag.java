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
package org.apache.commons.jelly.tags.define;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The &lt;invoke&gt; tag will invoke a given Script instance.
 * It can be used with the &lt;script&gt; tag which defines scripts
 * as variables that can later be invoked by this &lt;invoke&gt; tag.
 */
public class InvokeTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(InvokeTag.class);

    private Script script;

    public InvokeTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        if ( script == null ) {
            throw new MissingAttributeException("script");
        }
        script.run(context, output);
    }

    /**
     * Sets the Script to be invoked by this tag, which typically has been previously
     * defined by the use of the &lt;script&gt; tag.
     */
    public void setScript(final Script script) {
        this.script = script;
    }
}
