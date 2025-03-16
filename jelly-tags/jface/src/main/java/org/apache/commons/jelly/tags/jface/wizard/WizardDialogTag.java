/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly.tags.jface.wizard;

import java.util.Map;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.jelly.tags.jface.window.ApplicationWindowTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 *  This Tag creates a JFace WizardDialog
 */
public class WizardDialogTag extends UseBeanTag {

    /**
     * Provide a public method getWizard
     */
    final class WizardDialogImpl extends WizardDialog {
        public WizardDialogImpl(Shell parentShell, IWizard newWizard) {
            super(parentShell, newWizard);
        }

        @Override
        public IWizard getWizard() {
            return super.getWizard();
        }
    }

    /**
      * Provide a Wizard implementation
      */
    final class WizardImpl extends Wizard {
        public WizardImpl() {
            super();
            setNeedsProgressMonitor(true);
        }

        @Override
        public boolean performCancel() {
            try {
                if (performCancel != null) {
                    performCancel.run(context, output);
                } else {
                    invokeBody(output);
                }
            } catch (JellyTagException e) {
                log.error(e);
                return false;
            }
            return true;
        }

        @Override
        public boolean performFinish() {
            try {
                if (performFinish != null) {
                    performFinish.run(context, output);
                } else {
                    invokeBody(output);
                }
            } catch (JellyTagException e) {
                log.error(e);
                return false;
            }
            return true;
        }
    }

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(WizardDialogTag.class);

    /** Jelly XMLOutput */
    private XMLOutput output;

    /** Script to be executed on performCancel */
    private Script performCancel;

    /** Script to be executed on performFinish */
    private Script performFinish;

    /**
     * @param theClass
     */
    public WizardDialogTag(Class theClass) {
        super(theClass);
    }

    /*
     * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
     */
    @Override
    public void doTag(XMLOutput output) throws JellyTagException {
        super.doTag(output);

        if (getAttributes().get("performCancel") != null) {
            Object script = getAttributes().get("performCancel");
            if (script instanceof Script) {
                performCancel = (Script) getAttributes().get("performCancel");
            } else {
                throw new JellyTagException("AttributeValue " + script + " must be a Script");
            }
        }

        if (getAttributes().get("performFinish") != null) {
            Object script = getAttributes().get("performFinish");
            if (script instanceof Script) {
                performFinish = (Script) getAttributes().get("performFinish");
            } else {
                throw new JellyTagException("AttributeValue " + script + " must be a Script");
            }
        }

        this.output = output;
    }

    /**
     * @return Shell
     * @throws JellyTagException
     */
    protected Shell getShell() throws JellyTagException {
        ApplicationWindowTag tag =
            (ApplicationWindowTag) findAncestorWithClass(ApplicationWindowTag.class);
        if (tag == null) {
            throw new JellyTagException("This tag must be nested inside a <applicationWindow>");
        } else {
            return tag.getWindow().getShell();
        }
    }

    /**
     * @return WizardDialog
     */
    public WizardDialogImpl getWizardDialogImpl() {
        Object bean = getBean();
        if (bean instanceof WizardDialog) {
            return (WizardDialogImpl) bean;
        }
        return null;
    }

    /*
     * @see org.apache.commons.jelly.tags.core.UseBeanTag#newInstance(java.lang.Class, java.util.Map, org.apache.commons.jelly.XMLOutput)
     */
    @Override
    protected Object newInstance(Class theClass, Map attributes, XMLOutput output)
        throws JellyTagException {
        Wizard wizard = new WizardImpl();
        return new WizardDialogImpl(getShell(), wizard);
    }

    /**
     * Sets the Script to be executed on performCancel.
     * @param performCancel The performCancel to set
     */
    public void setPerformCancel(Script performCancel) {
        this.performCancel = performCancel;
    }

    /**
     * Sets the Script to be executed on performFinish.
     * @param performFinish The performFinish to set
     */
    public void setPerformFinish(Script performFinish) {
        this.performFinish = performFinish;
    }

}
