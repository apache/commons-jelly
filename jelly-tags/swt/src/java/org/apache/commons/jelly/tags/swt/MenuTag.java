/*
 * /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/swt/MenuTag.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
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
 * MenuTag.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
 */
package org.apache.commons.jelly.tags.swt;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

/** 
 * This tag creates an SWT Menu
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version 1.1
 */
public class MenuTag extends WidgetTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(MenuTag.class);
    
    public MenuTag() {
        super(Menu.class);
    }

    /**
     * @return the parent Shell or returns null
     */
/*    
    public Widget getParentWidget() {
        Widget parent = super.getParentWidget();
        if (parent instanceof Menu) {
            Menu menu = (Menu) parent;
            return menu.getShell();
        }
        return null;
    }
*/
    
    // Implementation methods
    //-------------------------------------------------------------------------                    
    
    /**
     * Provides a strategy method to allow a new child widget to be attached to
     * its parent
     * 
     * @param parent is the parent widget which is never null
     * @param widget is the new child widget to be attached to the parent
     */
    protected void attachWidgets(Widget parent, Widget widget) {
        Menu menu = (Menu) widget;
        if (parent instanceof Decorations) {
            Decorations shell = (Decorations) parent;
            shell.setMenuBar(menu);
        }
        else if (parent instanceof Control) {
            Control control = (Control) parent;
            control.setMenu(menu);
        }
        else if (parent instanceof MenuItem) {
            MenuItem menuItem = (MenuItem) parent;
            menuItem.setMenu(menu);
        }
    }
    
    /**
     * @see org.apache.commons.jelly.tags.swt.WidgetTag#createWidget(java.lang.Class, org.eclipse.swt.widgets.Widget, int)
     */
    protected Object createWidget(Class theClass, Widget parent, int style)
        throws JellyException {

        if (parent instanceof Decorations) {            
            return super.createWidget(theClass, parent, style);
        }
        else {
            if (parent instanceof Menu) {
                return new Menu((Menu) parent);
            }
            else if (parent instanceof MenuItem) {
                return new Menu((MenuItem) parent);
            }
            else if (parent instanceof Control) {
                return new Menu((Control) parent);
            }
            else {
                throw new JellyException("This tag must be nested inside a <shell>, <menu>, <menuItem> or control tag");
            }
        }
    }

}
