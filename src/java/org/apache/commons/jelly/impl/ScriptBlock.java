/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/impl/ScriptBlock.java,v 1.6 2002/05/17 15:18:11 jstrachan Exp $
 * $Revision: 1.6 $
 * $Date: 2002/05/17 15:18:11 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 * 
 * $Id: ScriptBlock.java,v 1.6 2002/05/17 15:18:11 jstrachan Exp $
 */
package org.apache.commons.jelly.impl;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

/** <p><code>ScriptBlock</code> a block of scripts.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.6 $
  */
public class ScriptBlock implements Script {

    private static final Script[] EMPTY_ARRAY = {
    };

    /** The list of scripts */
    private List list = new ArrayList();

    /** Compiled form uses arrays for speed 
     * - no Iterator, object allocation or typecast */
    private Script[] scripts = EMPTY_ARRAY;

    public ScriptBlock() {
    }

    public String toString() {
        return super.toString() + "[scripts=" + list + "]";
    }

    /** Add a new script to the end of this block */
    public void addScript(Script script) {
        list.add(script);
    }
    
    /** Removes a script from this block */
    public void removeScript(Script script) {
        list.remove(script);
    }

    /** Gets the child scripts that make up this block */
    public Script[] getScripts() {
        return scripts;
    }

    /** Allows direct modification of the List of scripts */
    public List getScriptList() {
        return list;
    }

    // Script interface
    //-------------------------------------------------------------------------                    
    public Script compile() throws Exception {
        int size = list.size();
        if (size == 1) {
            Script script = (Script) list.get(0);
            return script.compile();
        }
        scripts = new Script[size];
        list.toArray(scripts);
        // now compile children
        for (int i = 0; i < size; i++) {
            Script script = scripts[i];
            script.compile();
        }
        return this;
    }

    /** Evaluates the body of a tag */
    public void run(JellyContext context, XMLOutput output) throws Exception {
        for (int i = 0, size = scripts.length; i < size; i++) {
            Script script = scripts[i];
            script.run(context, output);
        }
    }
}
