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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

/** <p>{@code ScriptBlock} a block of scripts.</p>
  */
public class ScriptBlock implements Script {

    /** The list of scripts */
    private final List list = new ArrayList();

    /**
     * Create a new instance.
     */
    public ScriptBlock() {
    }

    /** Add a new script to the end of this block */
    public void addScript(final Script script) {
        list.add(script);
    }

    // Script interface
    //-------------------------------------------------------------------------
    @Override
    public Script compile() throws JellyException {
        final int size = list.size();
        if (size == 1) {
            final Script script = (Script) list.get(0);
            return script.compile();
        }
        // now compile children
        for (int i = 0; i < size; i++) {
            final Script script = (Script) list.get(i);
            list.set(i, script.compile());
        }
        return this;
    }

    /**
     * Gets the child scripts that make up this block. This list is live
     * so that it can be modified if required
     */
    public List getScriptList() {
        return list;
    }

    /** Removes a script from this block */
    public void removeScript(final Script script) {
        list.remove(script);
    }

    /** Evaluates the body of a tag */
    @Override
    public void run(final JellyContext context, final XMLOutput output) throws JellyTagException {
/*
        for (int i = 0, size = scripts.length; i < size; i++) {
            Script script = scripts[i];
            script.run(context, output);
        }
*/
        for (final Iterator iter = list.iterator(); iter.hasNext(); ) {
            final Script script = (Script) iter.next();
            script.run(context, output);
        }
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return super.toString() + "[scripts=" + list + "]";
    }

    /**
     * Trim the body of the script.
     * In this case, trim all elements, removing any that are empty text.
     */
    public void trimWhitespace() {
        final List list = getScriptList();
        for ( int i = list.size() - 1; i >= 0; i-- ) {
            final Script script = (Script) list.get(i);
            if ( script instanceof TextScript ) {
                final TextScript textScript = (TextScript) script;
                String text = textScript.getText();
                text = text.trim();
                if ( text.length() == 0 ) {
                    list.remove(i);
                }
                else {
                    textScript.setText(text);
                }
            }
        }
    }
}
