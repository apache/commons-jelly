/*
 * /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/swt/SwtTagLibrary.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
 * 1.1
 * 2002/12/18 15:27:49
 *
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
 * SwtTagLibrary.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
 */
package org.apache.commons.jelly.tags.jface;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.impl.TagFactory;
import org.apache.commons.jelly.tags.jface.preference.FieldEditorTag;
import org.apache.commons.jelly.tags.jface.preference.PreferenceDialogTag;
import org.apache.commons.jelly.tags.jface.preference.PreferencePageTag;
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
import org.xml.sax.Attributes;

/**
 * A Jelly custom tag library that creates JFace user interfaces
 * This taglib extends the SWT tag lib
 *  
 * @author <a href="mailto:ckl@dacelo.nl">Christiaan ten Klooster</a>
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
        //registerWizardDialogTag("wizardDialog", WizardDialog.class);
        //registerWizardTag("wizard", Wizard.class);
        //registerWizardPageTag("wizardPage", WizardPage.class);

        // preference tags
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
    private void registerMenuManager(String name, final Class theClass) {
        registerTagFactory(name, new TagFactory() {
            /**
             * @see org.apache.commons.jelly.impl.TagFactory#createTag(java.lang.String, org.xml.sax.Attributes)
             */
            public Tag createTag(String name, Attributes attributes) throws JellyException {
                return new MenuManagerTag();
            }
        });

    }

    /**
     * Register a widget tag for the given name
     *
     * @param name
     * @param widgetClass
     */
    protected void registerViewerTag(String name, Class widgetClass) {
        registerViewerTag(name, widgetClass, SWT.NULL);
    }

    /**
     * Register a widget tag for the given name
     *      
     * @param name
     * @param widgetClass
     * @param style
     */
    protected void registerViewerTag(String name, final Class theClass, final int style) {
        registerTagFactory(name, new TagFactory() {
            /**
             * @see org.apache.commons.jelly.impl.TagFactory#createTag(java.lang.String, org.xml.sax.Attributes)
             */
            public Tag createTag(String name, Attributes attributes) throws JellyException {
                return new ViewerTag(theClass, style);
            }
        });
    }

    /**
     * Register a widget tag for the given name
     *      
     * @param name
     * @param widgetClass
     * @param style
     */
    protected void registerWindowTag(String name, final Class theClass) {
        registerTagFactory(name, new TagFactory() {
            /**
             * @see org.apache.commons.jelly.impl.TagFactory#createTag(java.lang.String, org.xml.sax.Attributes)
             */
            public Tag createTag(String name, Attributes attributes) throws JellyException {
                return new ApplicationWindowTag(theClass);
            }
        });
    }

    /**
     * Register an action tag for the given name
     */
    protected void registerActionTag(String name, final Class theClass) {
        registerTagFactory(name, new TagFactory() {
            /**
             * @see org.apache.commons.jelly.impl.TagFactory#createTag(java.lang.String, org.xml.sax.Attributes)
             */
            public Tag createTag(String name, Attributes attributes) throws JellyException {
                return new ActionTag(theClass);
            }
        });
    }

    /**
       * Register a contribution item tag for the given name
       */
    protected void registerContributionItemTag(String name, final Class theClass) {
        registerTagFactory(name, new TagFactory() {
            /**
             * @see org.apache.commons.jelly.impl.TagFactory#createTag(java.lang.String, org.xml.sax.Attributes)
             */
            public Tag createTag(String name, Attributes attributes) throws JellyException {
                return new ContributionItemTag(theClass);
            }
        });
    }

    /**
     * @param name
     * @param theClass
     */
    protected void registerPreferenceDialogTag(String name, final Class theClass) {
        registerTagFactory(name, new TagFactory() {
            /**
             * @see org.apache.commons.jelly.impl.TagFactory#createTag(java.lang.String, org.xml.sax.Attributes)
             */
            public Tag createTag(String name, Attributes attributes) throws JellyException {
                return new PreferenceDialogTag(theClass);
            }
        });
    }


    /**
     * @param name
     * @param theClass
     */
    protected void registerFieldEditorTag(String name, final Class theClass) {
        registerTagFactory(name, new TagFactory() {
            /**
             * @see org.apache.commons.jelly.impl.TagFactory#createTag(java.lang.String, org.xml.sax.Attributes)
             */
            public Tag createTag(String name, Attributes attributes) throws JellyException {
                return new FieldEditorTag(theClass);
            }
        });
    }

}

