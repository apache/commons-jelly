/*
 * Created on Feb 26, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.apache.commons.jelly.tags.jface;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.tags.jface.window.ApplicationWindowImpl;
import org.apache.commons.jelly.tags.jface.window.ApplicationWindowTag;
import org.apache.commons.jelly.tags.swt.LayoutDataTag;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * Implementation of SWT LayoutDataTag
 *  
 * @author <a href="mailto:ckl@dacelo.nl">Christiaan ten Klooster</a> 
 */
public class JFaceLayoutDataTag extends LayoutDataTag {

    /**
     * @param layoutDataClass
     */
    public JFaceLayoutDataTag(Class layoutDataClass) {
        super(layoutDataClass);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.jelly.tags.core.UseBeanTag#processBean(java.lang.String, java.lang.Object)
     */
    protected void processBean(String var, Object bean) throws JellyTagException {
        Widget parent = getParentWidget();
        Window window = null;
        if (parent == null) {
            window = getParentWindow();
            if (window != null && window instanceof ApplicationWindowImpl) {
                parent = ((ApplicationWindowImpl) window).getContents();
            }
        }

        if (parent instanceof Control) {
            Control control = (Control) parent;
            control.setLayoutData(getBean());
        } else {
            throw new JellyTagException("This tag must be nested within a control widget tag");
        }
    }

    /**
     * @return the parent window 
     */
    public Window getParentWindow() {
        ApplicationWindowTag tag =
            (ApplicationWindowTag) findAncestorWithClass(ApplicationWindowTag.class);
        if (tag != null) {
            return tag.getWindow();
        }
        return null;
    }

}
