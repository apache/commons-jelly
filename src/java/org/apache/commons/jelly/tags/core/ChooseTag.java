/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/core/ChooseTag.java,v 1.5 2002/05/17 15:18:08 jstrachan Exp $
 * $Revision: 1.5 $
 * $Date: 2002/05/17 15:18:08 $
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
 * $Id: ChooseTag.java,v 1.5 2002/05/17 15:18:08 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.core;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jelly.CompilableTag;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.ScriptBlock;
import org.apache.commons.jelly.impl.TagScript;
import org.apache.commons.jelly.expression.Expression;

/** A tag which conditionally evaluates its body based on some condition
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.5 $
  */
public class ChooseTag extends TagSupport implements CompilableTag {

    private TagScript[] whenTags;
    private TagScript otherwiseTag;

    public ChooseTag() {
    }

    // CompilableTag interface
    //------------------------------------------------------------------------- 
    public void compile() throws Exception {
        // extract the When tags and the Other tag script
        // XXX: iterate through the body....
        List whenTagList = new ArrayList();
        otherwiseTag = null;
        Script body = getBody();
        if (body instanceof ScriptBlock) {
            ScriptBlock block = (ScriptBlock) body;
            for (Iterator iter = block.getScriptList().iterator(); iter.hasNext();) {
                Script script = (Script) iter.next();
                if (script instanceof TagScript) {
                    TagScript tagScript = (TagScript) script;
                    Tag tag = tagScript.getTag();
                    if (tag instanceof WhenTag) {
                        whenTagList.add(tagScript);
                    }
                    else if (tag instanceof OtherwiseTag) {
                        otherwiseTag = tagScript;
                        break;
                    }
                }
            }
        }
        else if (body instanceof TagScript) {
            // if only one child tag
            TagScript tagScript = (TagScript) body;
            Tag tag = tagScript.getTag();
            if (tag instanceof WhenTag) {
                whenTagList.add(tagScript);
            }
            else if (tag instanceof OtherwiseTag) {
                otherwiseTag = tagScript;
            }
        }
        whenTags = new TagScript[whenTagList.size()];
        whenTagList.toArray(whenTags);
    }
    
    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws Exception {
        for (int i = 0, size = whenTags.length; i < size; i++) {
            TagScript script = whenTags[i];
            script.run(context, output);
            WhenTag tag = (WhenTag) script.getTag();
            if (tag.getValue()) {
                return;
            }
        }
        if (otherwiseTag != null) {
            otherwiseTag.run(context, output);
        }
    }
}
