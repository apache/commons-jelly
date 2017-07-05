/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.xpath.XPathComparator;
import org.apache.commons.jelly.xpath.XPathTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Node;

import org.jaxen.XPath;
import org.jaxen.JaxenException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/** A tag which defines a variable from an XPath expression.
  * This function creates a variable of type {@link List} or {@link org.dom4j.Node}
  * (for example {@link org.dom4j.Element} or {@link org.dom4j.Attribute}).
  * Thus, the variable created from xml:set can be
  * used from the other xml library functions.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision$
  */
public class SetTag extends XPathTagSupport {

    private static final int RETURN_NODE_LIST = 0;
    private static final int RETURN_FIRST_NODE = 1;
    private static final int RETURN_STRING_LIST = 2;
    private static final int RETURN_DELIMITED_STRING_LIST = 3;
    private static final int RETURN_FIRST_AS_STRING = 4;
    
    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog(SetTag.class);

    /** The variable name to export. */
    private String var;

    /** The XPath expression to evaluate. */
    private XPath select;

    /** Xpath comparator for sorting */
    private XPathComparator xpCmp = null;

    private Boolean single = null;
    
    private Boolean asString = null;
    
    private String delimiter = null;

    private String delim = null;

    public SetTag() {

    }

    // Tag interface
    //-------------------------------------------------------------------------
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (var == null) {
            throw new MissingAttributeException( "var" );
        }
        if (select == null) {
            throw new MissingAttributeException( "select" );
        }

        Object xpathContext = getXPathContext();
        Object value = null;
        try {
            if( single != null && single.booleanValue() == true ) {
                value = select.selectSingleNode(xpathContext);
            } else {
                value = select.evaluate(xpathContext);
            }
        }
        catch (JaxenException e) {
            throw new JellyTagException(e);
        }
        
        if (value instanceof List) {
            List list = (List) value;
            // sort the list if xpCmp is set.
            if (xpCmp != null && (xpCmp.getXpath() != null)) {
                Collections.sort(list, xpCmp);
            }
            if(list.isEmpty()) {
                value = null;
            }
        }
        

        // handle single
        if (single!=null) {
            if (single.booleanValue() == true) {
                if(value instanceof List) {
                    List l = (List) value;
                    if (l.size() == 0)
                        value=null;
                    else
                        value=l.get(0);
                }
            } else { // single == false
                if(! (value instanceof List) ) {
                    List l = null;
                    if (value==null) {
                        l = new ArrayList(0);
                    } else {
                        l = new ArrayList(1);
                        l.add(value);
                    }
                    value = l;
                }
            }
        }
        
        // now convert the result(s) to string if need
        if(asString != null && asString.booleanValue()) {
            if(value instanceof Node) {
                value = ((Node) value).getStringValue();
            } else if(value instanceof List) {
                for(ListIterator it = ((List) value).listIterator(); it.hasNext(); ) {
                    Object v = it.next();
                    if(v instanceof Node) {
                        v = ((Node) v).getStringValue();
                        it.set(v);
                    }
                }
            }
        }
        
        // finally convert the result to a concatenated string if delimiter is defined
        if(delimiter != null && value instanceof List) {
            StringBuffer buff = new StringBuffer();
            for(Iterator it = ((List) value).iterator(); it.hasNext(); ) {
                Object v = it.next();
                if (v instanceof Node) {
                    buff.append( ((Node) v).getStringValue());
                } else {
                    buff.append(v.toString());
                }
                if(it.hasNext()) {
                    buff.append(delimiter);
                }
            }
            buff.setLength(buff.length());
            value = buff.toString();
        }
        

        //log.info( "Evaluated xpath: " + select + " as: " + value + " of type: " + value.getClass().getName() );

        context.setVariable(var, value);
    }

    private List valueAsList( final Object value ) {
        if (value instanceof List) {
            return (List)value;
        } else {
            if (value == null) {
                return Collections.EMPTY_LIST;
            } else {
                return Collections.singletonList(value);
            }
        }
    }

    private Object valueAsSingle( final Object value ) {
        if (value instanceof List) {
            List l = (List) value;
            if (l.isEmpty())
                return null;
            else
                return l.get(0);
        } else {
            return value;
        }
    }
    
    private String singleValueAsString( final Object value ) {
        if (value instanceof Node) {
            return ((Node) value).getStringValue();
        } else {
            return null;
        }
    }

    private List nodeListToStringList( final List values ) {
        List l = new ArrayList(values.size());
        for (Iterator it = values.iterator(); it.hasNext(); ) {
            Object v = it.next();
            String s = singleValueAsString(v);
            if (s != null) {
                l.add(s);
            }
        }
        return l;
    }

    private String joinDelimitedElements( final List values ) {
        StringBuffer sb = new StringBuffer();
        int sz = values.size();
        for (int i = 0; i < sz; i++) {
            String s = (String)values.get(i); 
            sb.append(s);
            if (i < sz - 1)
                sb.append(delim);
        }
        return sb.toString();
    }

    private int determineReturnType() {
        int resultType;
        if (single != null && single.booleanValue()) { // first node
            if (asString != null && asString.booleanValue()) {
                resultType = RETURN_FIRST_AS_STRING;
            } else {
                resultType = RETURN_FIRST_NODE;
            }
        } else { // all nodes
            if (asString != null && asString.booleanValue()) {
                if (delim != null) {
                    resultType = RETURN_DELIMITED_STRING_LIST;
                } else {
                    resultType = RETURN_STRING_LIST;
                }
            } else {
                resultType = RETURN_NODE_LIST;
            }
        }
        return resultType;
    }

    // Properties
    //-------------------------------------------------------------------------

    /** Sets the variable name to define for this expression
     */
    public void setVar(String var) {
        this.var = var;
    }

    /** Sets the XPath expression to evaluate. */
    public void setSelect(XPath select) {
        this.select = select;
    }

    /** If set to true will only take the first element matching.
        It then guarantees that the result is of type
        {@link org.dom4j.Node} thereby making sure that, for example,
        when an element is selected, one can directly call such methods
        as setAttribute.<br/>
        If set to false, guarantees that a list is returned.
        If set to false, guarantees that a list is returned.
        */
    public void setSingle(boolean single) {
        this.single = new Boolean(single);
    }
    
    /** If set to true, will ensure that the (XPath) text-value
      * of the selected node is taken instead of the node
      * itself.
      * This ensures that, thereafter, string manipulations
      * can be performed on the result.
      */
    public void setAsString(boolean asString) {
        this.asString = new Boolean(asString);
    }

    /** If set, returns a string delimited by this delimiter.
      * Implies <code>asString</code> to be true.
      */
    public void setDelim(String delim) {
        this.delimiter = delim;
        if( delim!=null ) {
            this.asString = Boolean.TRUE;
        }
    }

    /** Sets the xpath expression to use to sort selected nodes.
     *  Ignored if single is true.
     */
    public void setSort(XPath sortXPath) throws JaxenException {
        if (xpCmp == null) xpCmp = new XPathComparator();
        xpCmp.setXpath(sortXPath);
    }

    /**
     * Set whether to sort ascending or descending.
     */
    public void setDescending(boolean descending) {
        if (xpCmp == null) xpCmp = new XPathComparator();
        xpCmp.setDescending(descending);
    }
}
