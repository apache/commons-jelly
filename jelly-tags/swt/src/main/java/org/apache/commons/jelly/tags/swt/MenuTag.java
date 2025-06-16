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
package org.apache.commons.jelly.tags.swt;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

/**
 * This tag creates an SWT Menu
 * @version 1.1
 */
public class MenuTag extends WidgetTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(MenuTag.class);

    public MenuTag() {
        super(Menu.class);
    }

    public MenuTag(final int style) {
        super(Menu.class, style);
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Provides a strategy method to allow a new child widget to be attached to
     * its parent
     *
     * @param parent is the parent widget which is never null
     * @param widget is the new child widget to be attached to the parent
     */
    @Override
    protected void attachWidgets(final Object parent, final Widget widget) {
        final Menu menu = (Menu) widget;
        if (parent instanceof Decorations) {
            final Decorations shell = (Decorations) parent;
            shell.setMenuBar(menu);
        }
        else if (parent instanceof Control) {
            final Control control = (Control) parent;
            control.setMenu(menu);
        }
        else if (parent instanceof MenuItem) {
            final MenuItem menuItem = (MenuItem) parent;
            menuItem.setMenu(menu);
        }
    }

    /**
     * @see org.apache.commons.jelly.tags.swt.WidgetTag#createWidget(java.lang.Class, org.eclipse.swt.widgets.Widget, int)
     */
    @Override
    protected Object createWidget(final Class theClass, final Widget parent, final int style)
        throws JellyTagException {

        if (parent instanceof Decorations) {
            return super.createWidget(theClass, parent, style);
        }
        if (parent instanceof Menu) {
            return new Menu((Menu) parent);
        }
        if (parent instanceof MenuItem) {
            return new Menu((MenuItem) parent);
        }
        if (parent instanceof Control) {
            return new Menu((Control) parent);
        }
        throw new JellyTagException("This tag must be nested inside a <shell>, <menu>, <menuItem> or control tag");
    }

}
