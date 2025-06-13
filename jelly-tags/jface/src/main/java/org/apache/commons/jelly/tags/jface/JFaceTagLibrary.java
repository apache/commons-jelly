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
package org.apache.commons.jelly.tags.jface;

import org.apache.commons.jelly.tags.jface.preference.FieldEditorTag;
import org.apache.commons.jelly.tags.jface.preference.PreferenceDialogTag;
import org.apache.commons.jelly.tags.jface.preference.PreferencePageTag;
import org.apache.commons.jelly.tags.jface.window.ApplicationWindowTag;
import org.apache.commons.jelly.tags.jface.wizard.WizardDialogTag;
import org.apache.commons.jelly.tags.jface.wizard.WizardPageTag;
import org.apache.commons.jelly.tags.swt.SwtTagLibrary;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;

/**
 * A Jelly custom tag library that creates JFace user interfaces
 * This taglib extends the SWT tag lib
 */
public class JFaceTagLibrary extends SwtTagLibrary {

    public JFaceTagLibrary() {

        // Viewer tags
        registerViewerTag("tableViewer", TableViewer.class);
        registerViewerTag("tableTreeViewer", TableTreeViewer.class);
        registerViewerTag("treeViewer", TreeViewer.class);
        registerViewerTag("checkboxTreeViewer", CheckboxTreeViewer.class);

        // Event tags
        registerTag("doubleClickListener", DoubleClickListenerTag.class);
        registerTag("selectionChangedListener", SelectionChangedListenerTag.class);

        // Window tags
        registerWindowTag("applicationWindow", ApplicationWindow.class);

        // ContributionManager tags
        registerMenuManager("menuManager", MenuManagerTag.class);

        // Action tags
        registerActionTag("action", ActionTag.class);

        // ContributionItem tags
        registerContributionItemTag("separator", Separator.class);

        // Wizard tags
        registerWizardDialogTag("wizardDialog", WizardDialogTag.class);
        registerWizardPageTag("wizardPage", WizardPageTag.class);

        // Preference tags
        registerPreferenceDialogTag("preferenceDialog", PreferenceDialogTag.class);
        registerTag("preferencePage", PreferencePageTag.class);
        registerFieldEditorTag("booleanFieldEditor", BooleanFieldEditor.class);
        registerFieldEditorTag("colorFieldEditor", ColorFieldEditor.class);
        registerFieldEditorTag("directoryFieldEditor", DirectoryFieldEditor.class);
        registerFieldEditorTag("fileFieldEditor", FileFieldEditor.class);
        registerFieldEditorTag("fontFieldEditor", FontFieldEditor.class);
        registerFieldEditorTag("integerFieldEditor", IntegerFieldEditor.class);
        //registerFieldEditorTag("radioGroupFieldEditor", RadioGroupFieldEditor.class);
        //registerFieldEditorTag("stringButtonFieldEditor", StringButtonFieldEditor.class);
        registerFieldEditorTag("stringFieldEditor", StringFieldEditor.class);

    }

    /**
     * @param string
     * @param class1
     */
    private void registerMenuManager(final String name, final Class theClass) {
        registerTagFactory(name, (name1, attributes) -> new MenuManagerTag());

    }

    /**
     * Register a widget tag for the given name
     *
     * @param name
     * @param widgetClass
     */
    protected void registerViewerTag(final String name, final Class widgetClass) {
        registerViewerTag(name, widgetClass, SWT.NULL);
    }

    /**
     * Register a widget tag for the given name
     *
     * @param name
     * @param theClass
     * @param style
     */
    protected void registerViewerTag(final String name, final Class theClass, final int style) {
        registerTagFactory(name, (name1, attributes) -> new ViewerTag(theClass, style));
    }

    /**
     * Register a widget tag for the given name
     *
     * @param name
     * @param theClass
     */
    protected void registerWindowTag(final String name, final Class theClass) {
        registerTagFactory(name, (name1, attributes) -> new ApplicationWindowTag(theClass));
    }

    /**
     * Register an action tag for the given name
     */
    protected void registerActionTag(final String name, final Class theClass) {
        registerTagFactory(name, (name1, attributes) -> new ActionTag(theClass));
    }

    /**
       * Register a contribution item tag for the given name
       */
    protected void registerContributionItemTag(final String name, final Class theClass) {
        registerTagFactory(name, (name1, attributes) -> new ContributionItemTag(theClass));
    }

    /**
     * @param name
     * @param theClass
     */
    protected void registerPreferenceDialogTag(final String name, final Class theClass) {
        registerTagFactory(name, (name1, attributes) -> new PreferenceDialogTag(theClass));
    }

    /**
     * @param name
     * @param theClass
     */
    protected void registerFieldEditorTag(final String name, final Class theClass) {
        registerTagFactory(name, (name1, attributes) -> new FieldEditorTag(theClass));
    }

    /**
     * @param name
     * @param theClass
     */
    protected void registerWizardDialogTag(final String name, final Class theClass) {
        registerTagFactory(name, (name1, attributes) -> new WizardDialogTag(theClass));
    }

    protected void registerWizardPageTag(final String name, final Class theClass) {
        registerTagFactory(name, (name1, attributes) -> new WizardPageTag(theClass));
    }

    /**
     * Register a widget tag for the given name
     */
    @Override
    protected void registerWidgetTag(final String name, final Class widgetClass) {
        registerWidgetTag(name, widgetClass, SWT.NULL);
    }

    /**
     * Register a widget tag for the given name
     */
    @Override
    protected void registerWidgetTag(final String name, final Class widgetClass, final int style) {
        registerTagFactory(name, (name1, attributes) -> new JFaceWidgetTag(widgetClass, style));
    }

    /**
     * Register a layout tag for the given name
     */
    @Override
    protected void registerLayoutTag(final String name, final Class layoutClass) {
        registerTagFactory(name, (name1, attributes) -> new JFaceLayoutTag(layoutClass));
    }

}
