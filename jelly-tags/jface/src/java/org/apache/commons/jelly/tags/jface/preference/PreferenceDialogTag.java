/*
* ====================================================================
*
* The Apache Software License, Version 1.1
*
* Copyright (c) 2002 The Apache Software Foundation.  All rights
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
*/
package org.apache.commons.jelly.tags.jface.preference;

import java.util.Map;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.jelly.tags.jface.window.ApplicationWindowTag;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.widgets.Shell;

/**
 * This Tag creates a JFace PreferenceDialog
 * 
 * @author <a href="mailto:ckl@dacelo.nl">Christiaan ten Klooster</a> 
 */
public class PreferenceDialogTag extends UseBeanTag {

    public PreferenceDialogTag(Class arg0) {
        super(arg0);
    }

    /**
     * @return PreferenceDialog
     */
    public PreferenceDialog getPreferenceDialog() {
        Object bean = getBean();
        if (bean instanceof PreferenceDialog) {
            return (PreferenceDialog) bean;
        }
        return null;
    }

    /**
     * @return Shell
     * @throws JellyTagException
     */
    protected Shell getShell() throws JellyTagException {
        ApplicationWindowTag tag =
            (ApplicationWindowTag) findAncestorWithClass(ApplicationWindowTag.class);

        if (tag != null) {
            return tag.getWindow().getShell();
            
        } else {
            Map attributes = getAttributes();
            Object parent = attributes.remove("parent");
            if (parent instanceof Shell) {
                return (Shell) parent;
            } else {
                throw new JellyTagException("This tag must be nested inside a <applicationWindow> or have a parent of type Shell");
            }
        }
    }

    /* 
     * @see org.apache.commons.jelly.tags.core.UseBeanTag#newInstance(java.lang.Class, java.util.Map, org.apache.commons.jelly.XMLOutput)
     */
    protected Object newInstance(Class arg0, Map arg1, XMLOutput arg2) throws JellyTagException {
        PreferenceManager pm = new PreferenceManager();
        return new PreferenceDialog(getShell(), pm);
    }

}
