/*
 * Copyright 2002,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly.impl;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.util.TagUtils;

/** Wraps another Script instance in a WeakReference. Used to prevent
 * a Tag class from holding a hard reference to its body script.
 * <p/>
 * If the underlying Script has been GC'd and is no longer available,
 * an exception is thrown on any attempt to use this Script.
 * <p/>
 * WARNING: This class is not a permanent part of the API and will be removed or replaced in a future release.
 * Don't extend it or use it unless you absolutely must.
 * 
 * @author Hans Gilde
 *
 */
public class WeakReferenceWrapperScript implements Script {
    private WeakReference reference;
    
    public WeakReferenceWrapperScript(Script script) {
        reference = new WeakReference(script);
    }
    
    /* (non-Javadoc)
     * @see org.apache.commons.jelly.Script#compile()
     */
    public Script compile() throws JellyException {
        return script().compile();
    }

    /** Use this method to access the script.
     * @throws JellyException If the script has been GC'd.
     */
    public Script script() throws JellyTagException {
        Script script = (Script) reference.get();
        
        if (script == null) {
            throw new JellyTagException("Attempt to use a script that has been garbage collected.");
        }
        
        return script;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.jelly.Script#run(org.apache.commons.jelly.JellyContext, org.apache.commons.jelly.XMLOutput)
     */
    public void run(JellyContext context, XMLOutput output)
            throws JellyTagException {
        
        script().run(context, output);
    }
    
    /**Trims the white space from the script and its children.
     * TODO this code should be refactored into a formal part of the Script interface.
     * It has currently been cut and pasted from TagSupport.
     */
    public void trimWhitespace() throws JellyTagException {
        TagUtils.trimScript(script());
    }
    
    /** Determines if this script (the one in the WeakReference) 
     * or a child reference contains a Script
     * that's of a particular class. 
     * <p/>
     * <strong>This method is in place
     * to support specific features in the XML tag library and
     * shouldn't be used by anyone at all.
     * This method will be removed in a near-future verison of jelly.</strong>
     * <p/>
     * This method will be removed in a near-future verison of jelly.
     * XXX this is totally bogus and temporary, we should not need to check the type of scripts.
     * @param clazz Find a script that's an instance of this classs.
     * @throws JellyTagException
     */
    public boolean containsScriptType(Class clazz) throws JellyTagException {
        Object bodyScript = script();
        
        if (clazz.isInstance(bodyScript)) {
            return true;
        }
        
        if (bodyScript instanceof ScriptBlock) {
            ScriptBlock scriptBlock = (ScriptBlock) bodyScript;
            List scriptList = scriptBlock.getScriptList();
            for (Iterator iter = scriptList.iterator(); iter.hasNext(); ) {
                Script script = (Script) iter.next();
                if (script instanceof StaticTagScript) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
}