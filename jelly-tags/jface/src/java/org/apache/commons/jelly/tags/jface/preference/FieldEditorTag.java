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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * This Tag creates a JFace FieldEditor
 * 
 * @author <a href="mailto:ckl@dacelo.nl">Christiaan ten Klooster</a> 
 */
public class FieldEditorTag extends UseBeanTag {

    public FieldEditorTag(Class arg0) {
        super(arg0);
    }

    /* 
     * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
     */
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        PreferencePageTag tag = (PreferencePageTag) findAncestorWithClass(PreferencePageTag.class);
        if (tag == null) {
            throw new JellyTagException("This tag must be nested inside a <preferencePage>");
        }

        // get new instance of FieldEditor
        PreferencePageTag.PreferencePageImpl page = tag.getPreferencePageImpl();
        getAttributes().put("parentComposite", page.getFieldEditorParent());

        // add fieldEditor to PreferencePage
        Object fieldEditor = newInstance(getDefaultClass(), getAttributes(), output);
        if (fieldEditor instanceof FieldEditor) {
            page.addField((FieldEditor) fieldEditor);
        }

    }

    /* 
     * @see org.apache.commons.jelly.tags.core.UseBeanTag#newInstance(java.lang.Class, java.util.Map, org.apache.commons.jelly.XMLOutput)
     */
    protected Object newInstance(Class theClass, Map attributes, XMLOutput output)
        throws JellyTagException {

        if (theClass == null) {
            throw new JellyTagException("No Class available to create the FieldEditor");
        }

        String name = (String) attributes.get("name");
        if (name == null) {
            throw new MissingAttributeException("name");
        }

        String labelText = (String) attributes.get("labelText");
        if (labelText == null) {
            throw new MissingAttributeException("labelText");
        }

        Composite parentComposite = (Composite) attributes.get("parentComposite");
        if (parentComposite == null) {
            throw new MissingAttributeException("parentComposite");
        }

        // let's try to call a constructor 
        try {
            Class[] types = { String.class, String.class, Composite.class };
            Constructor constructor = theClass.getConstructor(types);
            if (constructor != null) {
                Object[] arguments = { name, labelText, parentComposite };
                return constructor.newInstance(arguments);
            }
            return theClass.newInstance();

        } catch (NoSuchMethodException e) {
            throw new JellyTagException(e);
        } catch (InstantiationException e) {
            throw new JellyTagException(e);
        } catch (IllegalAccessException e) {
            throw new JellyTagException(e);
        } catch (InvocationTargetException e) {
            throw new JellyTagException(e);
        }
    }

}
