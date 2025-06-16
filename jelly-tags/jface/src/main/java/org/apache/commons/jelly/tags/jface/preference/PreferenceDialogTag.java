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

import java.util.Map;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.jelly.tags.jface.window.ApplicationWindowTag;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.widgets.Shell;

/**
 * This Tag creates a JFace PreferenceDialog
 */
public class PreferenceDialogTag extends UseBeanTag {

    public PreferenceDialogTag(final Class arg0) {
        super(arg0);
    }

    /**
     * @return PreferenceDialog
     */
    public PreferenceDialog getPreferenceDialog() {
        final Object bean = getBean();
        if (bean instanceof PreferenceDialog) {
            return (PreferenceDialog) bean;
        }
        return null;
    }

    /**
     * @return Shell
     * @throws JellyTagException
     */
    protected Shell getShell() throws JellyTagException {
        final ApplicationWindowTag tag =
            (ApplicationWindowTag) findAncestorWithClass(ApplicationWindowTag.class);

        if (tag != null) {
            return tag.getWindow().getShell();

        }
        final Map attributes = getAttributes();
        final Object parent = attributes.remove("parent");
        if (parent instanceof Shell) {
            return (Shell) parent;
        }
        throw new JellyTagException("This tag must be nested inside a <applicationWindow> or have a parent of type Shell");
    }

    /*
     * @see org.apache.commons.jelly.tags.core.UseBeanTag#newInstance(java.lang.Class, java.util.Map, org.apache.commons.jelly.XMLOutput)
     */
    @Override
    protected Object newInstance(final Class arg0, final Map arg1, final XMLOutput arg2) throws JellyTagException {
        final PreferenceManager pm = new PreferenceManager();
        return new PreferenceDialog(getShell(), pm);
    }

}
