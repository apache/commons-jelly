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

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;

import org.apache.commons.beanutils2.ConvertUtils;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.impl.TagFactory;
import org.apache.commons.jelly.impl.TagScript;
import org.apache.commons.jelly.tags.swing.converters.ColorConverter;
import org.apache.commons.jelly.tags.swing.converters.DimensionConverter;
import org.apache.commons.jelly.tags.swing.converters.PointConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

/** The Swing tag library.
 * <p>
 * In addition to the tag descriptions in the tag doc, implements
 * the following basic components:
 * </p>
 * <ul>
 * <li>button - JButton
        <li>checkBox - JCheckBox
<li>checkBoxMenuItem - JCheckBoxMenuItem
<li>comboBox - JComboBox
<li>desktopPane - JDesktopPane
<li>editorPane - JEditorPane
<li>fileChooser - JFileChooser
<li>frame - JFrame
<li>internalFrame - JInternalFrame
<li>label - JLabel
<li>list - JList
<li>menu - JMenu
<li>menuBar - JMenuBar
<li>menuItem - JMenuItem
<li>panel - JPanel
<li>passwordField - JPasswordField
<li>popupMenu - JPopupMenu
<li>progressBar - JProgressBar
<li>radioButton - JRadioButton
<li>radioButtonMenuItem - JRadioButtonMenuItem
<li>optionPane - JOptionPane
<li>scrollPane - JScrollPane
<li>separator - JSeparator

<li>splitPane - JSplitPane
<li>hbox" - Box.createHorizontalBox()
<li>vbox - Box.createVerticalBox()

<li>tabbedPane - JTabbedPane
<li>table - JTable
<li>textArea - JTextArea
<li>textField - JTextField
<li>toggleButton - JToggleButton
<li>tree - JTree
<li>toolBar - JToolBar
 * </ul>
 */
public class SwingTagLibrary extends TagLibrary {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(SwingTagLibrary.class);

    /** A map of element name to bean class objects */
    private Map factoryMap;

    static {

        // ### we should create Converters from Strings to various Swing types such as
        // ### Icon, KeyStroke etc.
        ConvertUtils.register( new DimensionConverter(), Dimension.class );
        ConvertUtils.register( new PointConverter(), Point.class );
        ConvertUtils.register( new ColorConverter(), java.awt.Color.class );
    }

    public SwingTagLibrary() {
        registerTag( "action", ActionTag.class );
        registerTag( "buttonGroup", ButtonGroupTag.class );
        registerTag( "component", ComponentTag.class );
        registerTag( "font", FontTag.class );
        registerTag( "windowListener", WindowListenerTag.class );
        registerTag( "focusListener", FocusListenerTag.class );
        registerTag( "keyListener", KeyListenerTag.class );

        // the model tags
        registerTag( "tableModel", TableModelTag.class );
        registerTag( "tableModelColumn", TableModelColumnTag.class );

        // the border tags...
        registerTag( "etchedBorder", EtchedBorderTag.class );
        registerTag( "emptyBorder", EmptyBorderTag.class );
        registerTag( "titledBorder", TitledBorderTag.class );
        // @todo the other kinds of borders, empty, bevelled, compound etc

        // the layout tags...

        // HTML style table, tr, td layouts
        registerTag( "tableLayout", TableLayoutTag.class );
        registerTag( "tr", TrTag.class );
        registerTag( "td", TdTag.class );

        // GridBagLayout
        registerTag( "gridBagLayout", GridBagLayoutTag.class );
        registerTag( "gbc", GbcTag.class );

        // BorderLayout
        registerTag( "borderLayout", BorderLayoutTag.class );
        registerTag( "borderAlign", BorderAlignTag.class );

        //CardLayout
        registerTag( "cardLayout", CardLayoutTag.class);

        // Dialog
        registerTag( "dialog", DialogTag.class );
    }

    /** Creates a new script to execute the given tag name and attributes */
    @Override
    public TagScript createTagScript(final String name, final Attributes attributes) throws JellyException {
        final TagScript answer = super.createTagScript(name, attributes);
        if ( answer == null ) {
            final Factory factory = getFactory( name );
            if ( factory != null ) {
                return new TagScript(
                    (name1, attributes1) -> {
                        if ( factory instanceof TagFactory ) {
                            return ((TagFactory) factory).createTag(name1, attributes1);
                        }
                        return new ComponentTag(factory);
                    }
                );
            }
        }
        return answer;
    }

    /**
     * @return the Factory of the Swing component for the given element name
     */
    public Factory getFactory(final String elementName) {
        return (Factory) getFactoryMap().get(elementName);
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Strategy method allowing derived classes to change the registration behavior
     */
    protected void registerFactories() {
        registerBeanFactory( "button", JButton.class );
        registerBeanFactory( "checkBox", JCheckBox.class );
        registerBeanFactory( "checkBoxMenuItem", JCheckBoxMenuItem.class );
        registerBeanFactory( "comboBox", JComboBox.class );
        // how to add content there ?
        // Have a ComboBoxModel (just one should have a Table or Tree Model objects) ?
        // can the element control it's children ?
        // but children should also be able to be any component (as Swing comps. are all container)
        registerBeanFactory( "desktopPane", JDesktopPane.class );
        registerBeanFactory( "editorPane", JEditorPane.class );
        registerBeanFactory( "fileChooser", JFileChooser.class );
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
        registerBeanFactory( "progressBar", JProgressBar.class );
        registerBeanFactory( "radioButton", JRadioButton.class );
        registerBeanFactory( "radioButtonMenuItem", JRadioButtonMenuItem.class );
        registerBeanFactory( "optionPane", JOptionPane.class );
        registerBeanFactory( "scrollPane", JScrollPane.class );
        registerBeanFactory( "separator", JSeparator.class );

        registerFactory(
            "splitPane",
            () -> {
                final JSplitPane answer = new JSplitPane();
                answer.setLeftComponent(null);
                answer.setRightComponent(null);
                answer.setTopComponent(null);
                answer.setBottomComponent(null);
                return answer;
            }
        );

        // Box related layout components
        registerFactory(
            "hbox",
            Box::createHorizontalBox
        );
        registerFactory(
            "vbox",
            Box::createVerticalBox
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
    protected void registerFactory(final String name, final Factory factory) {
        getFactoryMap().put(name, factory);
    }

    /**
     * Register a bean factory for the given element name and class
     */
    protected void registerBeanFactory(final String name, final Class beanClass) {
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
