/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/xml/XMLTagLibrary.java,v 1.6 2002/05/17 18:04:00 jstrachan Exp $
 * $Revision: 1.6 $
 * $Date: 2002/05/17 18:04:00 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * $Id: XMLTagLibrary.java,v 1.6 2002/05/17 18:04:00 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.swing;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.ConvertUtils;

import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.impl.TagScript;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.Attributes;

/** 
 * A Jelly custom tag library that allows Ant tasks to be called from inside Jelly.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @version $Revision: 1.6 $
 */
public class SwingTagLibrary extends TagLibrary {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(SwingTagLibrary.class);

    /** A map of element name to bean class objects */
    private Map factoryMap;
        
    static {

        // ### we should create Converters from Strings to various Swing types such as
        // ### Dimension, Icon, KeyStroke etc.

/*               
        ConvertUtils.register(
            new Converter() {
                public Object convert(Class type, Object value) {
                    if ( value != null ) {
                        String text = value.toString();
                        // now lets parse the dimension...
                        return new Dimension( x, y );
                    }
                    return null;
                }
            },
            Dimension.class
       );
*/       
    }
        
    public SwingTagLibrary() {
    }

    /** Creates a new script to execute the given tag name and attributes */
    public TagScript createTagScript(String name, Attributes attributes) throws Exception {
        Factory factory = getFactory( name );
        if ( factory != null ) {
            ComponentTag tag = new ComponentTag(factory);
            return TagScript.newInstance(tag);
        }
        return null;
    }
    
    /**
     * @return the Factory of the Swing component for the given element name
     */
    public Factory getFactory(String elementName) {
        return (Factory) getFactoryMap().get(elementName);
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                    
    
    /**
     * Strategy method allowing derived classes to change the registration behaviour
     */
    protected void registerFactories() {
        registerBeanFactory( "button", JButton.class );
        registerBeanFactory( "checkBox", JCheckBox.class );
        registerBeanFactory( "checkBoxMenuItem", JCheckBoxMenuItem.class );
        registerBeanFactory( "comboBox", JComboBox.class );
        registerBeanFactory( "desktopPane", JDesktopPane.class );
        registerBeanFactory( "dialog", JDesktopPane.class );
        registerBeanFactory( "frame", JFrame.class );
        registerBeanFactory( "internalFrame", JInternalFrame.class );
        registerBeanFactory( "label", JLabel.class );
        registerBeanFactory( "list", JList.class );
        registerBeanFactory( "menu", JMenu.class );
        registerBeanFactory( "menuBar", JMenuBar.class );
        registerBeanFactory( "menuItem", JMenuItem.class );        
        registerBeanFactory( "panel", JPanel.class );        
        registerBeanFactory( "passwordField", JPasswordField.class );        
        registerBeanFactory( "popupMenu", JPopupMenu.class );        
        registerBeanFactory( "radioButton", JRadioButton.class );        
        registerBeanFactory( "radioButtonMenuItem", JRadioButtonMenuItem.class );        
        registerBeanFactory( "optionPane", JOptionPane.class );
        registerBeanFactory( "scrollPane", JScrollPane.class );
        registerBeanFactory( "separator", JSeparator.class );

        registerFactory(
            "splitPane", 
            new Factory() {
                public Object newInstance() {
                    JSplitPane answer = new JSplitPane();
                    answer.setLeftComponent(null);
                    answer.setRightComponent(null);
                    answer.setTopComponent(null);
                    answer.setBottomComponent(null);
                    return answer;
                }
            }
        );
        registerBeanFactory( "tabbedPane", JTabbedPane.class );
        registerBeanFactory( "table", JTable.class );
        registerBeanFactory( "textArea", JTextArea.class );
        registerBeanFactory( "textField", JTextField.class );
        registerBeanFactory( "toggleButton", JToggleButton.class );
        registerBeanFactory( "tree", JTree.class );
        registerBeanFactory( "toolBar", JToolBar.class );
    }

    /**
     * Register a widget factory for the given element name
     */
    protected void registerFactory(String name, Factory factory) {
        getFactoryMap().put(name, factory);
    }

    /**
     * Register a bean factory for the given element name and class
     */    
    protected void registerBeanFactory(String name, Class beanClass) {
        registerFactory(name, new BeanFactory(beanClass));
    }
    
    protected Map getFactoryMap() {
        if ( factoryMap == null ) {
            factoryMap = new HashMap();
            registerFactories();
        }
        return factoryMap;
    }
}
