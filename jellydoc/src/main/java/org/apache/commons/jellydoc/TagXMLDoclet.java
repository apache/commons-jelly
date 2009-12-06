package org.apache.commons.jellydoc;

/* ====================================================================
 *   Copyright 2001-2004 The Apache Software Foundation.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * ====================================================================
 */

import java.beans.Introspector;
import java.io.File;
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
import com.sun.javadoc.DocErrorReporter;
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
    private String encodingFormat = "UTF-8";
    private String localName = "javadoc";
    private ContentHandler cm = null;
    private String targetFileName = null;
    private Attributes emptyAtts = new AttributesImpl();

    public TagXMLDoclet (RootDoc root) throws Exception
    {
        readOptions(root);
        File targetFile = new File(targetFileName);
        targetFile.getParentFile().mkdirs();
        FileOutputStream writer = new FileOutputStream(targetFileName);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(encodingFormat);
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
        
        System.out.println( "processing package: " + packageDoc.name());
        
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
            System.out.println( "no tags found");
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
        
        
        System.out.println( "processing tag: " + name);

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
System.out.println("TagXMLDoclet.start()");
        try {
            new TagXMLDoclet(root);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }
    
    private void readOptions(RootDoc root)
    {
        String[][] options = root.options();
        for (int i = 0; i < options.length; i++)
        {
            String[] opt = options[i];
            if (opt[0].equals("-d"))
            {
                targetFileName = opt[1] + "/taglib.xml";
            }
            if (opt[0].equals("-encoding"))
            {
                encodingFormat = opt[1];
            }
        }
    }
    
    public static int optionLength(String option)
    {
        if(option.equals("-d"))
        {
            return 2;
        }
        if(option.equals("-encoding"))
        {
            return 2;
        }
        return 0;
    }
    
    public static boolean validOptions(String options[][], 
        DocErrorReporter reporter)
    {
        boolean foundEncodingOption = false;
        boolean foundDirOption = false;
        for (int i = 0; i < options.length; i++)
        {
            String[] opt = options[i];
            if (opt[0].equals("-d"))
            {
                if (foundDirOption)
                {
                    reporter.printError("Only one -d option allowed.");
                    return false;
                }
                else
                { 
                    foundDirOption = true;
                }
            } 
            if (opt[0].equals("-encoding"))
            {
                if (foundEncodingOption)
                {
                    reporter.printError("Only one -encoding option allowed.");
                    return false;
                }
                else
                { 
                    foundEncodingOption = true;
                }
            } 
        }
        if (!foundDirOption)
        {
            reporter.printError("Usage: javadoc -d <directory> -doclet TagXMLDoclet ...");
            return false;
        }
        return true;
    }
}
