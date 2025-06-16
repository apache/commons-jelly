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

import java.util.List;

import org.apache.commons.jelly.Script;

/**
 * <p><code>CompositeTextScriptBlock</code> represents a text body of a
 * a tag which contains expressions, so that whitespace trimming
 * can be handled differently.</p>
 */
public class CompositeTextScriptBlock extends ScriptBlock {

    /**
     * Create an instance.
     */
    public CompositeTextScriptBlock() {
    }


    /**
     * Trim the body of the script.
     * In this case, trim the whitespace from the start of the first element
     * and from the end of the last element.
     */
    @Override
    public void trimWhitespace() {
        final List list = getScriptList();
        final int size = list.size();
        if ( size > 0 ) {
            Script script = (Script) list.get(0);
            if ( script instanceof TextScript ) {
                final TextScript textScript = (TextScript) script;
                textScript.trimStartWhitespace();
            }
            if ( size > 1 ) {
                script = (Script) list.get(size - 1);
                if ( script instanceof TextScript ) {
                    final TextScript textScript = (TextScript) script;
                    textScript.trimEndWhitespace();
                }
            }
        }
    }

}
