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
package org.apache.commons.jelly.tags.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.Map;

import javax.swing.JDialog;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Creates a Swing Dialog.  A JDialog needs to have it's owner set in the constructor,
 * which is why this class is needed instead of just using a BeanFactory.
 */
public class DialogTag extends UseBeanTag implements ContainerTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(DialogTag.class);

    public DialogTag() {
        super(JDialog.class);
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Adds a component to the dialog.
     */
    @Override
    public void addChild(final Component component, final Object constraints) {
        final Container contentPane = ((JDialog) getBean()).getContentPane();
        if (constraints != null) {
            contentPane.add( component, constraints );
        } else {
            contentPane.add( component );
        }
    }

    // ContainerTag interface
    //-------------------------------------------------------------------------

    /**
     * Creates a JDialog.  The constructor used depends on the value of the owner attribute.
     */
    @Override
    protected Object newInstance(final Class theClass, final Map attributes, final XMLOutput output)
    throws JellyTagException {
        final Object owner = attributes.remove( "owner" );
        if (owner instanceof Frame) {
            return new JDialog((Frame) owner);
        }
        if (owner instanceof Dialog) {
            return new JDialog((Dialog) owner);
        }
        return new JDialog();
    }
}

