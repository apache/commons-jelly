/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/Tag.java,v 1.6 2002/05/17 15:18:12 jstrachan Exp $
 * $Revision: 1.6 $
 * $Date: 2002/05/17 15:18:12 $
 *
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
 * $Id: Tag.java,v 1.6 2002/05/17 15:18:12 jstrachan Exp $
 */

package org.apache.commons.jelly.util;

import java.beans.Introspector;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import org.cyberneko.html.parsers.SAXParser;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;

/**
 * Main Doclet class to generate Tag Library ML.  
 * 
 * @author <a href="mailto:gopi@aztecsoft.com">Gopinath M.R.</a>
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author Rodney Waldhoff
 */

// #### somehow we need to handle taglib inheritence...

public class TagXMLDoclet extends Doclet {

    private String xmlns = "jvx";
    private String encodingFormat="UTF-8";
    private String localName = "javadoc";
    private ContentHandler cm = null;
    private String targetFileName="target/taglib.xml";
    private Attributes emptyAtts = new AttributesImpl();

    public TagXMLDoclet (RootDoc root) throws Exception {
        FileOutputStream writer = new FileOutputStream(targetFileName);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter xmlWriter = new XMLWriter(writer, format);
        try {
            cm = xmlWriter;
            cm.startDocument();
            javadocXML(root);
            cm.endDocument();
            xmlWriter.close();
        } 
        catch (IOException e) {
            xmlWriter.close();
            throw e;
        }
    }

    /**
     * Generates the xml for the tag libraries
     */
    private void javadocXML(RootDoc root) throws SAXException {
        cm.startElement(xmlns, localName, "tags", emptyAtts);
        PackageDoc[] packageArray = root.specifiedPackages();

        // Generate for packages.
        for (int i = 0; i < packageArray.length; ++i) {
            packageXML(packageArray[i]);
        }

        cm.endElement(xmlns, localName, "tags");
    }

    /**
     * Generates doc for a tag library
     */
    private void packageXML(PackageDoc packageDoc) throws SAXException {
        ClassDoc[] classArray = packageDoc.ordinaryClasses();
        
        // lets see if we find a Tag
        boolean foundTag = false;
        for (int i = 0; i < classArray.length; ++i) {
            ClassDoc classDoc = classArray[i];
            if ( isTag( classArray[i] ) ) {
                foundTag = true;
                break;
            }
        }
        if ( ! foundTag ) {
            return;
        }
        
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute(xmlns, localName, "name", "String", packageDoc.name());
        
        String name = packageDoc.name();
        int idx = name.lastIndexOf('.');
        if ( idx > 0 ) {
            name = name.substring(idx+1);
        }
        atts.addAttribute(xmlns, localName, "prefix", "String", name);
        
        String uri = "jelly:" + name;
        
        atts.addAttribute(xmlns, localName, "uri", "String", uri );
        cm.startElement(xmlns, localName, "library", atts);

        // generate Doc element.
        docXML(packageDoc);

        // generate tags
        for (int i = 0; i < classArray.length; ++i) {
            if ( isTag( classArray[i] ) ) {
                tagXML(classArray[i]);
            }
        }
        cm.endElement(xmlns, localName, "library");
    }

    /**
     * @return true if this class is a Jelly Tag
     */
    private boolean isTag(ClassDoc classDoc) {
        ClassDoc[] interfaceArray = classDoc.interfaces();
        for (int i = 0; i < interfaceArray.length; ++i) {
            String name = interfaceArray[i].qualifiedName();
            if ("org.apache.commons.jelly.Tag".equals(name)) {
                return true;
            }
        }
        ClassDoc base = classDoc.superclass();
        if ( base != null ) {
            return isTag(base);
        }
        return false;
    }
    
    /**
     * Generates doc for a tag
     */
    private void tagXML(ClassDoc classDoc) throws SAXException {
        if (classDoc.isAbstract()) {
            return;
        }
        
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute(xmlns, localName, "className", "String", classDoc.name());
        String name = classDoc.name();
        if ( name.endsWith( "Tag" ) ) {
            name = name.substring(0, name.length() - 3 );
        }
        name = Introspector.decapitalize(name);
        
        atts.addAttribute(xmlns, localName, "name", "String", name);
        cm.startElement(xmlns, localName, "tag", atts);

        // generate "doc" sub-element
        docXML(classDoc);

        // generate the attributes
        propertiesXML(classDoc);
        
        // generate "method" sub-elements
        cm.endElement(xmlns, localName, "tag");
    }

    /**
     * Generates doc for a tag property
     */
    private void propertiesXML(ClassDoc classDoc) throws SAXException {
        MethodDoc[] methodArray = classDoc.methods();
        for (int i = 0; i < methodArray.length; ++i) {
            propertyXML(methodArray[i]);
        }
        ClassDoc base = classDoc.superclass();
        if ( base != null ) {
            propertiesXML( base );
        }
    }


    /**
     * Generates doc for a tag property
     */
    private void propertyXML(MethodDoc methodDoc) throws SAXException {
        if ( ! methodDoc.isPublic() || methodDoc.isStatic() ) {
            return;
        }
        String name = methodDoc.name();
        if ( ! name.startsWith( "set" ) ) {
            return;
        }
        Parameter[] parameterArray = methodDoc.parameters();
        if ( parameterArray == null || parameterArray.length != 1 ) {
            return;
        }
        Parameter parameter = parameterArray[0];
        
        name = name.substring(3);
        name = Introspector.decapitalize(name);
        
        if ( name.equals( "body") || name.equals( "context" ) || name.equals( "parent" ) ) {
            return;
        }
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute(xmlns, localName, "name", "String", name);
        atts.addAttribute(xmlns, localName, "type", "String", parameter.typeName());
        
        cm.startElement(xmlns, localName, "attribute", atts);

        // maybe do more semantics, like use custom tags to denote if its required, optional etc.
        
        // generate "doc" sub-element
        docXML(methodDoc);

        cm.endElement(xmlns, localName, "attribute");
    }

    /**
     * Generates doc for element "doc"
     */
    private void docXML(Doc doc) throws SAXException {
        cm.startElement(xmlns, localName, "doc", emptyAtts);
        // handle the "comment" part, including {@link} tags
        {
            Tag[] tags = doc.inlineTags();
            for(int i=0;i<tags.length;i++) {
                // if tags[i] is an @link tag
                if(tags[i] instanceof SeeTag) {                    
                    String label = ((SeeTag)tags[i]).label();
                    // if the label is null or empty, use the class#member part of the link
                    if(null == label || "".equals(label)) { 
                        StringBuffer buf = new StringBuffer();
                        String className = ((SeeTag)tags[i]).referencedClassName();
                        if("".equals(className)) { className = null; }
                        String memberName = ((SeeTag)tags[i]).referencedMemberName();
                        if("".equals(memberName)) { memberName = null; }
                        if(null != className) {
                            buf.append(className);
                            if(null != memberName) {
                                buf.append(".");
                            }
                        }
                        if(null != memberName) {
                            buf.append(memberName);
                        }
                        label = buf.toString();
                    }
                    parseHTML(label);
                } else {
                    parseHTML(tags[i].text());
                }
            }
        }
        // handle the "tags" part
        {
            Tag[] tags = doc.tags();
            for (int i = 0; i < tags.length; ++i) {
                javadocTagXML(tags[i]);
            }
        }
        cm.endElement(xmlns, localName, "doc");
    }

    protected void parseHTML(String text) throws SAXException {
        SAXParser parser = new SAXParser();
        parser.setProperty(
            "http://cyberneko.org/html/properties/names/elems",
            "lower"
        );
        parser.setProperty(
            "http://cyberneko.org/html/properties/names/attrs",
            "lower"
        );
        parser.setContentHandler(
            new DefaultHandler() {
                public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
                    if ( validDocElementName( localName ) ) {
                        cm.startElement(namespaceURI, localName, qName, atts);
                    }
                }
                public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
                    if ( validDocElementName( localName ) ) {
                        cm.endElement(namespaceURI, localName, qName);
                    }
                }
                public void characters(char[] ch, int start, int length) throws SAXException {
                    cm.characters(ch, start, length);
                }
            }
        );
        try {
            parser.parse( new InputSource(new StringReader( text )) );
        }
        catch (IOException e) {
            System.err.println( "This should never happen!" + e );
        }
    }

    /**
     * @return true if the given name is a valid HTML markup element.
     */
    protected boolean validDocElementName(String name) {
        return ! name.equalsIgnoreCase( "html" ) && ! name.equalsIgnoreCase( "body" );
    }
    
    /**
     * Generates doc for all tag elements.
     */
    private void javadocTagXML(Tag tag) throws SAXException {
        String name = tag.name().substring(1) + "tag";
        if (! tag.text().equals("")) {
            cm.startElement(xmlns, localName, name, emptyAtts);
            cm.characters(tag.text().toCharArray(), 0, tag.text().length());
            cm.endElement(xmlns, localName, name);
        }
    }

    public static boolean start(RootDoc root) {
        try {
            new TagXMLDoclet(root);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }
}
