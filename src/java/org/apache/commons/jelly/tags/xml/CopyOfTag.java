/*
 * Copyright 2002,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly.tags.xml;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.xpath.XPathTagSupport;
import org.dom4j.Node;
import org.dom4j.io.SAXWriter;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.xml.sax.SAXException;

/** A tag which performs a copy-of operation like the XSLT tag
  *
  * @author James Strachan
  */
public class CopyOfTag extends XPathTagSupport {

    /** The XPath expression to evaluate. */
    private XPath select;

    public CopyOfTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        Object xpathContext = getXPathContext();
        
        if (select == null) {
            throw new MissingAttributeException( "select" );
        }

        SAXWriter saxWriter = new SAXWriter(output, output);
        try {
            List nodes = select.selectNodes(xpathContext);
            for (Iterator iter = nodes.iterator(); iter.hasNext(); ) {
                Object object = iter.next();
                if ( object instanceof Node ) {
                    saxWriter.write( (Node) object );
                }
                else if (object != null ) {
                    output.write( object.toString() );
                }
            } 
        }
        catch (SAXException e) {
            throw new JellyTagException(e);
        } catch (JaxenException e) {
            throw new JellyTagException(e);
        }
    }

    // Properties
    //-------------------------------------------------------------------------                
    /** Sets the XPath expression to evaluate. */
    public void setSelect(XPath select) {
        this.select = select;
    }
}
