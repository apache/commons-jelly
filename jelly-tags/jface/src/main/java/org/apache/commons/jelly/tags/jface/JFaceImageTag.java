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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.jface.window.ApplicationWindowImpl;
import org.apache.commons.jelly.tags.jface.window.ApplicationWindowTag;
import org.apache.commons.jelly.tags.swt.ImageTag;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;

/**
 * Implementation of SWT ImageTag
 */
public class JFaceImageTag extends ImageTag {

    /*
     * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {

        // invoke by body just in case some nested tag configures me
        invokeBody(output);

        Widget parent = getParentWidget();
        Window window = null;
        if (parent == null) {
            window = getParentWindow();
            if (window != null && window instanceof ApplicationWindowImpl) {
                parent = ((ApplicationWindowImpl) window).getContents();
            }
        }

        if (parent == null && window == null) {
            throw new JellyTagException("This tag must be nested within a Widget or a Window");
        }

        final Image image = new Image(parent.getDisplay(), getSrc());
        if (window != null) {
            setWindowImage(window, image);
        } else {
            setWidgetImage(parent, image);
        }

    }

    /**
     * @return the parent window
     */
    public Window getParentWindow() {
        final ApplicationWindowTag tag =
            (ApplicationWindowTag) findAncestorWithClass(ApplicationWindowTag.class);
        if (tag != null) {
            return tag.getWindow();
        }
        return null;
    }

    /**
     * Sets default image Window
     * @param window
     * @param image
     */
    private void setWindowImage(final Window window, final Image image) {
        window.getShell().setImage(image);
    }

}
