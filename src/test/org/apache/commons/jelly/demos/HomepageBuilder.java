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
package org.apache.commons.jelly.demos;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;

//Jelly imports

import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.Embedded;

/**
 * A sample Swing program that demonstrates the use of Jelly as a templating mechanism
 * 
 * @author Otto von Wachter
 */
public class HomepageBuilder extends JPanel {

	JTextField nameField;
	JTextField urlField;
	JTextField addField;
	JTextField colorField;
	JComboBox templateList;
	JList interestList;
	DefaultListModel listModel;
	

    public HomepageBuilder() {
    	
    	System.out.println("Starting Homepage Builder");
    	
    	JPanel leftPanel = new JPanel();
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
    	
//    	JPanel rightPanel = new JPanel();
//    	rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
    	
    	leftPanel.add(new JLabel("Add a Hobby:"));
		
		addField = new JTextField();
		leftPanel.add(addField);
		
		JButton addButton = new JButton("Add >>>");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                listModel.addElement(addField.getText());
            }
		});
		leftPanel.add(addButton);
    	
    	listModel = new DefaultListModel();
    	listModel.addElement("Killing bad guys");
    	listModel.addElement("Wrecking cars");
    	listModel.addElement("Eating jelly");
    	interestList = new JList(listModel);
		    	

		JButton submit = new JButton("Build and preview your page!");
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                buildPage(templateList.getSelectedItem().toString(),new JellyContext());
                showPage();
            }
		});

        // Layout the demo
        setLayout(new BorderLayout());
        add(submit, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);
        add(new JScrollPane(interestList), BorderLayout.EAST);
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    }
    
    public void buildPage(String template, JellyContext ctx) {
    	
//		try {
//		
//		Embedded embedded = new Embedded();
//		embedded.setOutputStream(new FileOutputStream("out.html"));
//		//embedded.setVariable("some-var","some-object");
//		
//		embedded.setScript("file:///anoncvs/jakarta-commons-sandbox/jelly/sample.jelly");
//		//or one can do.
//		//embedded.setScript(scriptAsInputStream);
//		
//		boolean bStatus=embedded.execute();
//		if(!bStatus) //if error
//		{
//		System.out.println(embedded.getErrorMsg());
//		}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
			

    	try {
    		
    		OutputStream output = new FileOutputStream("demopage.html");
    						
            JellyContext context = new JellyContext();
            context.setVariable("name",nameField.getText());
            context.setVariable("background",colorField.getText());
            context.setVariable("url",urlField.getText());
            
            Vector v = new Vector();
            Enumeration enum= listModel.elements();
            while (enum.hasMoreElements()) {
            	v.add(enum.nextElement());
            }
            context.setVariable("hobbies", v);
    
    		XMLOutput xmlOutput = XMLOutput.createXMLOutput(output);
    		context.runScript( resolveURL("src/test/org/apache/commons/jelly/demos/"+template), xmlOutput );
            xmlOutput.flush();
            System.out.println("Finished merging template");
            
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	
    }

	void showPage() {
				
		//open new window
        JFrame frame = new JFrame("Your Homepage");
		
		//add html pane
		try {
			
		  URL url = resolveURL("demopage.html");
		  JEditorPane htmlPane = new JEditorPane(url);
		  htmlPane.setEditable(false);
		  frame.setContentPane(new JScrollPane(htmlPane));
		  
		} catch(Exception ioe) {
		  System.err.println("Error displaying page");
		}
		
        frame.pack();
		frame.setSize(500,500);		
        frame.setVisible(true);
		
	} 
	
	 /***
      * @return the URL for the relative file name or absolute URL 
      */
    protected URL resolveURL(String name) throws MalformedURLException {
         File file = new File(name);
         if (file.exists()) {
             return file.toURL();
         }
         return new URL(name);
     }


    public static void main(String s[]) {
        JFrame frame = new JFrame("Homepage Builder");

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
 
        frame.setContentPane(new HomepageBuilder());
        frame.pack();
        frame.setVisible(true);
    }
}
