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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.tags.jface.window.ApplicationWindowImpl;
import org.apache.commons.jelly.tags.jface.window.ApplicationWindowTag;
import org.apache.commons.jelly.tags.jface.wizard.WizardPageTag;
import org.apache.commons.jelly.tags.swt.WidgetTag;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

/**
 * Implementation of SWT WidgetTag  
 * 
 * @author <a href="mailto:ckl@dacelo.nl">Christiaan ten Klooster</a> 
 */
public class JFaceWidgetTag extends WidgetTag implements Tag {

    /**
     * @param widgetClass
     */
    public JFaceWidgetTag(Class widgetClass) {
        super(widgetClass);
    }

    /**
     * @param widgetClass
     * @param style
     */
    public JFaceWidgetTag(Class widgetClass, int style) {
        super(widgetClass, style);
    }

    /* 
     * @see org.apache.commons.jelly.tags.swt.WidgetTag#attachWidgets(java.lang.Object, org.eclipse.swt.widgets.Widget)
     */
    protected void attachWidgets(Object parent, Widget widget) throws JellyTagException {
        super.attachWidgets(parent, widget);

        // set Parent composite of wizard page
        if (getParent() instanceof WizardPageTag) {
            WizardPageTag tag = (WizardPageTag) getParent();
            if (tag.getWizardPageImpl().getParentControl() == null) {
                if (widget instanceof Composite) {
                    tag.getWizardPageImpl().setParentComposite((Composite) widget);
                } else {
                    throw new JellyTagException("First child of a <wizardPage> must be of type Composite");
                }
            }
        }
    }

    /* 
     * @see org.apache.commons.jelly.tags.swt.WidgetTag#getParentWidget()
     */
    public Widget getParentWidget() {
        parent = super.getParentWidget();

        if (parent == null && getParent() instanceof WizardPageTag) {
            WizardPageTag tag = (WizardPageTag) getParent();
            if (tag != null) {
                WizardPageTag.WizardPageImpl page = tag.getWizardPageImpl();
                return page.getControl();
            }
        }

        if (parent == null) {
            ApplicationWindowTag tag =
                (ApplicationWindowTag) findAncestorWithClass(ApplicationWindowTag.class);
            if (tag != null) {
                Window window = tag.getWindow();
                if (window != null && window instanceof ApplicationWindowImpl) {
                    return ((ApplicationWindowImpl) window).getContents();
                }
            }
        }

        return parent;
    }

}
