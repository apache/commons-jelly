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
package org.apache.commons.jelly.tags.swt;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;

/**
 * This creates an image on the parent Widget.
 *
 * @author <a href="mailto:ckl@dacelo.nl">Christiaan ten Klooster</a>
 * @version 
 */
public class ImageTag extends TagSupport {

	/** path to file */
	private String src;

	public ImageTag() {
	}

	/**
	 * Sets the src.
	 * @param src The src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}

	/**
	 * Method getSrc.
	 * @return String
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * @return the parent widget which this widget will be added to.
	 */
	public Widget getParentWidget() {
		WidgetTag tag = (WidgetTag) findAncestorWithClass(WidgetTag.class);
		if (tag != null) {
			return tag.getWidget();
		}
		return null;
	}

    // Tag interface
    //-------------------------------------------------------------------------

    /**
     * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
     */
    public void doTag(XMLOutput output) throws JellyTagException {
        // invoke by body just in case some nested tag configures me
        invokeBody(output);
        
		Widget parent = getParentWidget();
        if (parent == null) {
            throw new JellyTagException("This tag must be nested within a widget");
        }
        
		Image image = new Image(parent.getDisplay(), getSrc());

		if (parent instanceof Label) {
			Label label = (Label) parent;
			label.setImage(image);

		} else if (parent instanceof Button) {
			Button button = (Button) parent;
			button.setImage(image);
			
		} else if (parent instanceof Item) {
			Item item = (Item) parent;
			item.setImage(image);
			
		} else if (parent instanceof Decorations) {
			Decorations item = (Decorations) parent;
			item.setImage(image);
			
		} else {
			throw new JellyTagException("This tag must be nested inside a <label>, <button> or <item> tag");
		}
	}

}
