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
 *  
 * @author <a href="mailto:ckl@dacelo.nl">Christiaan ten Klooster</a> 
 */
public class WizardDialogTag extends UseBeanTag {

    /**
     * Provide a public method getWizard
     */
    class WizardDialogImpl extends WizardDialog {
        public WizardDialogImpl(Shell parentShell, IWizard newWizard) {
            super(parentShell, newWizard);
        }

        public IWizard getWizard() {
            return super.getWizard();
        }
    }

    /**
      * Provide a Wizard implementation
      */
    class WizardImpl extends Wizard {
        public WizardImpl() {
            super();
            setNeedsProgressMonitor(true);
        }

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
    public void doTag(XMLOutput output) throws JellyTagException {
        super.doTag(output);

        if (getAttributes().get("performCancel") != null) {
            Object script = getAttributes().get("performCancel");
            if (script instanceof Script) {
                performCancel = (Script) getAttributes().get("performCancel");
            } else {
                throw new JellyTagException("Attributevalue " + script + " must be a Script");
            }
        }

        if (getAttributes().get("performFinish") != null) {
            Object script = getAttributes().get("performFinish");
            if (script instanceof Script) {
                performFinish = (Script) getAttributes().get("performFinish");
            } else {
                throw new JellyTagException("Attributevalue " + script + " must be a Script");
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
