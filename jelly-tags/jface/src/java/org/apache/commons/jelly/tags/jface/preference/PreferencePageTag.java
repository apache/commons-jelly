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

import java.io.IOException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.widgets.Composite;

/**
 * This Tag creates a JFace PreferencePage
 * 
 * Provides a concrete preference store implementation based on an internal java.util.Properties object
 * 
 * @author <a href="mailto:ckl@dacelo.nl">Christiaan ten Klooster</a> 
 */
public class PreferencePageTag extends TagSupport {

    /**
     * Implementation of a FieldEditorPreferencePage
     * method createFieldEditors is called on Dialog.open()
     */
    public class PreferencePageImpl extends FieldEditorPreferencePage {
        private PreferenceStore preferenceStore;

        public PreferencePageImpl(String title) {
            super(title, FieldEditorPreferencePage.GRID);
            try {
                preferenceStore = new PreferenceStore(filename);
                preferenceStore.load();
                setPreferenceStore(preferenceStore);
            } catch (IOException e) {
                log.error(e);
            }
        }

        public void addField(FieldEditor editor) {
            super.addField(editor);
        }

        protected void createFieldEditors() {
            try {
                invokeBody(output);
            } catch (JellyTagException e) {
                log.error(e);
            }
        }

        public Composite getFieldEditorParent() {
            return super.getFieldEditorParent();
        }

        public IPreferenceStore getPreferenceStore() {
            return preferenceStore;
        }
    }

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(PreferencePageTag.class);
    
    /** Filename of the store */
    private String filename;

    /** Jelly XMLOutput */
    private XMLOutput output;
    
    /** Current PreferencePageImpl */
    private PreferencePageImpl page;
    
    /** Title of both PreferenceNode and PreferencePage */ 
    private String title;

    /* 
     * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
     */
    public void doTag(XMLOutput output) throws JellyTagException {
        // check location
        PreferenceDialogTag dialogTag =
            (PreferenceDialogTag) findAncestorWithClass(PreferenceDialogTag.class);
        if (dialogTag == null) {
            throw new JellyTagException("This tag must be nested within a <preferenceDialog>");
        }

        // check for missing attributes
        if (filename == null) {
            throw new MissingAttributeException("filename");
        }
        if (title == null) {
            throw new MissingAttributeException("title");
        }

        // build new PreferenceNode with same title as the PreferencePage
        PreferenceDialog dialog = dialogTag.getPreferenceDialog();
        PreferenceNode node = new PreferenceNode(title);

        // build new PreferencePage
        page = new PreferencePageImpl(title);

        // add node to PreferenceManager
        node.setPage(page);
        dialog.getPreferenceManager().addToRoot(node);

        // used by PreferencePageImpl
        this.output = output;
    }

    /**
     * Get the PreferencePageImpl
     * @return PreferencePageImpl
     */
    public PreferencePageImpl getPreferencePageImpl() {
        return page;
    }

    /**
     * Sets the filename.
     * @param filename The filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Sets the title.
     * @param title The title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

}
