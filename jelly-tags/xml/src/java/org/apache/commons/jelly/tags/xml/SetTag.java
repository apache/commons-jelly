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
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

/** A tag which defines a variable from an XPath expression.
  * This function creates a variable of type {@link List} or {@link org.dom4j.Node}
  * (for example {@link org.dom4j.Element} or {@link org.dom4j.Attribute}).
  * Thus, the variable created from xml:set can be
  * used from the other xml library functions.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.12 $
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
            if(single!=null && single.booleanValue()==true) {
                value = select.selectSingleNode(xpathContext);
            } else {
                value = select.evaluate(xpathContext);
            }
        }
        catch (JaxenException e) {
            throw new JellyTagException(e);
        }
        
        if (value instanceof List) {
            // sort the list if xpCmp is set.
            if (xpCmp != null && (xpCmp.getXpath() != null)) {
                Collections.sort((List)value, xpCmp);
            }
        }

        switch ( determineReturnType() ) {
        case RETURN_NODE_LIST:
            value = valueAsList(value);
            break;
        case RETURN_FIRST_NODE:
            value = valueAsSingle(value);
            break;
        case RETURN_STRING_LIST:
            value = nodeListToStringList(valueAsList(value));
            break;
        case RETURN_DELIMITED_STRING_LIST:
            value = joinDelimitedElements(nodeListToStringList(valueAsList(value)));
            break;
        case RETURN_FIRST_AS_STRING:
            value = singleValueAsString(valueAsSingle(value));
            break;
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
        as setAttribute.
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
      * Setting this attribute to true will also set the single
      * attribute to true.
      */
    public void setAsString(boolean asString) {
        this.asString = new Boolean(asString);
    }

    /** If set, returns a string delimited by this delimiter.
     */
    public void setDelim(String delim) {
        this.delim  = delim;
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
