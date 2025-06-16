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
package org.apache.commons.jelly.tags.jface.wizard;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.jelly.tags.jface.preference.PreferencePageTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 *  This Tag creates a JFace WizardPage
 */
public class WizardPageTag extends UseBeanTag {

    /**
     * Implementation of a WizardPage
     * method createControl is called on Dialog.open()
     */
    public class WizardPageImpl extends WizardPage {
        private Composite parentComposite;

        public WizardPageImpl(final String title) {
            super(title);
        }

        @Override
        public void createControl(final Composite parent) {
            // set initial parent Control to avoid a NPE during invokeBody
            setControl(parent);

            // create page contents
            try {
                invokeBody(output);
            } catch (final JellyTagException e) {
                log.error(e);
            }

            // parentComposite should be first Composite child
            if (parentComposite != null) {
                setControl(parentComposite);
            }
        }

        public Control getParentControl() {
            return parentComposite;
        }
        public void setParentComposite(final Composite parentComposite) {
            this.parentComposite = parentComposite;
        }

    }

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(PreferencePageTag.class);

    /** Jelly XMLOutput */
    private XMLOutput output;

    /**
     * @param theClass
     */
    public WizardPageTag(final Class theClass) {
        super(theClass);
    }

    /*
     * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        // check location
        final WizardDialogTag wizardTag = (WizardDialogTag) findAncestorWithClass(WizardDialogTag.class);
        if (wizardTag == null) {
            throw new JellyTagException("This tag must be nested within a <wizardDialog>");
        }

        // check for missing attributes
        final String title = (String) getAttributes().get("title");
        if (title == null) {
            throw new MissingAttributeException("title");
        }

        // get WizardPageImpl
        final WizardPageImpl page = new WizardPageImpl(title);
        setBean(page);
        setBeanProperties(page, getAttributes());

        final String var = (String) getAttributes().get("var");
        processBean(var, page);

        // get Wizard
        final WizardDialogTag.WizardDialogImpl dialog = wizardTag.getWizardDialogImpl();
        final Wizard wizard = (Wizard) dialog.getWizard();

        // add WizardPage to the Wizard
        wizard.addPage(page);

        // used by implementing page
        this.output = output;
    }

    /**
     * Gets the WizardPageImpl
     * @return WizardPageImpl
     */
    public WizardPageImpl getWizardPageImpl() {
        final Object bean = getBean();
        if (bean instanceof WizardPageImpl) {
            return (WizardPageImpl) bean;
        }
        return null;
    }

}
