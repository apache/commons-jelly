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

import java.io.InputStream;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.util.ClassLoaderUtils;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;

/**
 * This creates an image on the parent Widget.
 * @version CVS ImageTag.java,v 1.5 2004/09/07 02:41:40 dion Exp
 */
public class ImageTag extends TagSupport {

    /** Path to file */
    private String src;

    /** Variable name, if specified */
    private String var;

    /** Resource name, if specified */
    private String resource;

    /**
     * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        // invoke by body just in case some nested tag configures me
        invokeBody(output);

        final Widget parent = getParentWidget();

        if (parent == null) {
            throw new JellyTagException("This tag must be nested within a Widget or a Window");
        }

        Image image = null;

        if (getSrc() != null) {
           image = loadLocalImage(parent.getDisplay());
        } else if (getResource() != null) {
           image = loadResourceImage(parent.getDisplay());
        } else {
            throw new JellyTagException("Either an image location or a resource must be specified");
        }

        setWidgetImage(parent, image);

        // store the image as a context variable if specified
        if (var != null) {
            context.setVariable(var, image);
        }
    }

    /**
     * @return the parent widget which this widget will be added to.
     */
    public Widget getParentWidget() {
        final WidgetTag tag = (WidgetTag) findAncestorWithClass(WidgetTag.class);
        if (tag != null) {
            return tag.getWidget();
        }
        return null;
    }

    /**
     * Obtains the resource
     * @return the image resource
     */
    public String getResource() {
        return resource;
    }

    /**
     * Method getSrc.
     * @return String
     */
    public String getSrc() {
        return src;
    }

    /**
     * Obtain the variable name.
     * @return String the variable name
     */
    public String getVar() {
      return this.var;
    }

    /**
     * Creates an Image, loaded from the local disk
     */
    private Image loadLocalImage(final Display display) {
        return new Image(display, getSrc());
    }

    /**
     * Creates an Image, loaded from a specified resource.
     */
    private Image loadResourceImage(final Display display) {
        final ClassLoader loader = ClassLoaderUtils.getClassLoader(null, getContext().getUseContextClassLoader(), getClass());
        final InputStream stream = loader.getResourceAsStream(getResource());
        return new Image(display, stream);
    }

    // Tag interface
    //-------------------------------------------------------------------------

    /**
     * Sets the resource
     * @param resource image resource location
     */
    public void setResource(final String resource) {
       this.resource = resource;
    }

    /**
     * Sets the src.
     * @param src The src to set
     */
    public void setSrc(final String src) {
        this.src = src;
    }

    /**
     * Sets the variable name
     */
    public void setVar(final String var) {
        this.var = var;
    }

    /**
     * Add image to a widget
     * @param parent
     * @param image
     * @throws JellyTagException
     */
    protected void setWidgetImage(final Widget parent, final Image image) throws JellyTagException {
        if (parent instanceof Label) {
            final Label label = (Label) parent;
            label.setImage(image);

        } else if (parent instanceof Button) {
            final Button button = (Button) parent;
            button.setImage(image);

        } else if (parent instanceof Item) {
            final Item item = (Item) parent;
            item.setImage(image);

        } else if (parent instanceof Decorations) {
            final Decorations item = (Decorations) parent;
            item.setImage(image);

        } else {
            throw new JellyTagException("This tag must be nested inside a <label>, <button>, <shell> or <item> tag");
        }
    }
}
