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
package org.apache.commons.jelly.util;

import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.impl.CompositeTextScriptBlock;
import org.apache.commons.jelly.impl.ScriptBlock;
import org.apache.commons.jelly.impl.TextScript;

/** Contains static methods to help tag developers.
 */
public class TagUtils {
    /** Trims the whitespace from a script and its children.
     *
     */
    public static void trimScript(final Script body) {
        synchronized(body) {
            if ( body instanceof CompositeTextScriptBlock ) {
                final CompositeTextScriptBlock block = (CompositeTextScriptBlock) body;
                block.trimWhitespace();
            }
            else
            if ( body instanceof ScriptBlock ) {
                final ScriptBlock block = (ScriptBlock) body;
                block.trimWhitespace();
            }
            else if ( body instanceof TextScript ) {
                final TextScript textScript = (TextScript) body;
                textScript.trimWhitespace();
            }
        }
    }

    private TagUtils() {

    }

}