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
package org.apache.commons.jelly.demos;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;

/**
 * A sample Swing program that demonstrates the use of Jelly as a templating mechanism
 */
public class HomepageBuilder extends JPanel {

    public static void main(final String s[]) {
        final JFrame frame = new JFrame("Homepage Builder");

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {System.exit(0);}
        });

        frame.setContentPane(new HomepageBuilder());
        frame.pack();
        frame.setVisible(true);
    }
    JTextField nameField;
    JTextField urlField;
    JTextField addField;
    JTextField colorField;
    JComboBox templateList;
    JList interestList;

    DefaultListModel listModel;

    public HomepageBuilder() {

        System.out.println("Starting Homepage Builder");

        final JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        leftPanel.add(new JLabel("Name:"));

        nameField= new JTextField("James Bond");
        leftPanel.add(nameField);

        leftPanel.add(new JLabel("Favorite Color:"));

        colorField = new JTextField("#007007");
        leftPanel.add(colorField);

        leftPanel.add(new JLabel("Picture URL:"));

        urlField = new JTextField("http://www.ianfleming.org/007news/images3/c2002_pierce1.jpg");
        leftPanel.add(urlField);

        leftPanel.add(new JLabel("Choose template:"));

        templateList = new JComboBox(new String[] {"template1.jelly","template2.jelly"});
        leftPanel.add(templateList);

//        JPanel rightPanel = new JPanel();
//        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        leftPanel.add(new JLabel("Add a Hobby:"));

        addField = new JTextField();
        leftPanel.add(addField);

        final JButton addButton = new JButton("Add >>>");
        addButton.addActionListener(e -> listModel.addElement(addField.getText()));
        leftPanel.add(addButton);

        listModel = new DefaultListModel();
        listModel.addElement("Killing bad guys");
        listModel.addElement("Wrecking cars");
        listModel.addElement("Eating jelly");
        interestList = new JList(listModel);

        final JButton submit = new JButton("Build and preview your page!");
        submit.addActionListener(e -> {
            buildPage(templateList.getSelectedItem().toString(),new JellyContext());
            showPage();
        });

        // Layout the demo
        setLayout(new BorderLayout());
        add(submit, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);
        add(new JScrollPane(interestList), BorderLayout.EAST);
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    }

    public void buildPage(final String template, final JellyContext ctx) {

//        try {
//
//        Embedded embedded = new Embedded();
//        embedded.setOutputStream(new FileOutputStream("out.html"));
//        //embedded.setVariable("some-var","some-object");
//
//        embedded.setScript("file:///anoncvs/jakarta-commons-sandbox/jelly/sample.jelly");
//        //or one can do.
//        //embedded.setScript(scriptAsInputStream);
//
//        boolean bStatus=embedded.execute();
//        if (!bStatus) //if error
//        {
//        System.out.println(embedded.getErrorMsg());
//        }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {

            final OutputStream output = new FileOutputStream("demopage.html");

            final JellyContext context = new JellyContext();
            context.setVariable("name",nameField.getText());
            context.setVariable("background",colorField.getText());
            context.setVariable("url",urlField.getText());

            final Vector v = new Vector();
            final Enumeration items = listModel.elements();
            while (items.hasMoreElements()) {
                v.add(items.nextElement());
            }
            context.setVariable("hobbies", v);

            final XMLOutput xmlOutput = XMLOutput.createXMLOutput(output);
            context.runScript( resolveURL("src/test/org/apache/commons/jelly/demos/"+template), xmlOutput );
            xmlOutput.flush();
            System.out.println("Finished merging template");

        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

     /***
      * @return the URL for the relative file name or absolute URL
      */
    protected URL resolveURL(final String name) throws MalformedURLException {
         final File file = new File(name);
         if (file.exists()) {
             return file.toURL();
         }
         return new URL(name);
     }

    void showPage() {

        //open new window
        final JFrame frame = new JFrame("Your Homepage");

        //add html pane
        try {

          final URL url = resolveURL("demopage.html");
          final JEditorPane htmlPane = new JEditorPane(url);
          htmlPane.setEditable(false);
          frame.setContentPane(new JScrollPane(htmlPane));

        } catch (final Exception ioe) {
          System.err.println("Error displaying page");
        }

        frame.pack();
        frame.setSize(500,500);
        frame.setVisible(true);

    }
}
