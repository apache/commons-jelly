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
 */
public class PreferencePageTag extends TagSupport {

    /**
     * Implementation of a FieldEditorPreferencePage
     * method createFieldEditors is called on Dialog.open()
     */
    public class PreferencePageImpl extends FieldEditorPreferencePage {
        private PreferenceStore preferenceStore;

        public PreferencePageImpl(final String title) {
            super(title, FieldEditorPreferencePage.GRID);
            try {
                preferenceStore = new PreferenceStore(fileName);
                preferenceStore.load();
                setPreferenceStore(preferenceStore);
            } catch (final IOException e) {
                log.error(e);
            }
        }

        @Override
        public void addField(final FieldEditor editor) {
            super.addField(editor);
        }

        @Override
        protected void createFieldEditors() {
            try {
                invokeBody(output);
            } catch (final JellyTagException e) {
                log.error(e);
            }
        }

        @Override
        public Composite getFieldEditorParent() {
            return super.getFieldEditorParent();
        }

        @Override
        public IPreferenceStore getPreferenceStore() {
            return preferenceStore;
        }
    }

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(PreferencePageTag.class);

    /** Filename of the store */
    private String fileName;

    /** Jelly XMLOutput */
    private XMLOutput output;

    /** Current PreferencePageImpl */
    private PreferencePageImpl page;

    /** Title of both PreferenceNode and PreferencePage */
    private String title;

    /*
     * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        // check location
        final PreferenceDialogTag dialogTag =
            (PreferenceDialogTag) findAncestorWithClass(PreferenceDialogTag.class);
        if (dialogTag == null) {
            throw new JellyTagException("This tag must be nested within a <preferenceDialog>");
        }

        // check for missing attributes
        if (fileName == null) {
            throw new MissingAttributeException("filename");
        }
        if (title == null) {
            throw new MissingAttributeException("title");
        }

        // build new PreferenceNode with same title as the PreferencePage
        final PreferenceDialog dialog = dialogTag.getPreferenceDialog();
        final PreferenceNode node = new PreferenceNode(title);

        // build new PreferencePage
        page = new PreferencePageImpl(title);

        // add node to PreferenceManager
        node.setPage(page);
        dialog.getPreferenceManager().addToRoot(node);

        // used by PreferencePageImpl
        this.output = output;
    }

    /**
     * Gets the PreferencePageImpl
     * @return PreferencePageImpl
     */
    public PreferencePageImpl getPreferencePageImpl() {
        return page;
    }

    /**
     * Sets the file name.
     * @param fileName The file name to set
     */
    public void setFilename(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * Sets the title.
     * @param title The title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

}
