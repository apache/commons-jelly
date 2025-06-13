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
package org.apache.commons.jelly.tags.xml;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.xpath.XPathComparator;
import org.apache.commons.jelly.xpath.XPathSource;
import org.apache.commons.jelly.xpath.XPathTagSupport;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

/** A tag which performs an iteration over the results of an XPath expression
  */
public class ForEachTag extends XPathTagSupport implements XPathSource {

    /** Holds the XPath selector. */
    private XPath select;

    /** Xpath comparator for sorting */
    private XPathComparator xpCmp = null;

    /** If specified then the current item iterated through will be defined
      * as the given variable name. */
    private String var;

    /** The current iteration value */
    private Object iterationValue;

    public ForEachTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        if (select != null) {
            List nodes = null;
            try {
                nodes = select.selectNodes( getXPathContext() );
            }
            catch (final JaxenException e) {
                throw new JellyTagException(e);
            }

            // sort the list if xpCmp is set.
            if (xpCmp != null && xpCmp.getXpath() != null) {
                Collections.sort(nodes, xpCmp);
            }

            final Iterator iter = nodes.iterator();
            while (iter.hasNext()) {
                iterationValue = iter.next();
                if (var != null) {
                    context.setVariable(var, iterationValue);
                }
                invokeBody(output);
            }
        }
    }

    // XPathSource interface
    //-------------------------------------------------------------------------

    /**
     * @return the current XPath iteration value
     *  so that any other XPath aware child tags to use
     */
    @Override
    public Object getXPathSource() {
        return iterationValue;
    }

    // Properties
    //-------------------------------------------------------------------------
    /** Sets the XPath selection expression
      */
    public void setSelect(final XPath select) {
        this.select = select;
    }

    /** Sets the variable name to export for the item being iterated over
     */
    public void setVar(final String var) {
        this.var = var;
    }

    /** Sets the xpath expression to use to sort selected nodes.
     */
    public void setSort(final XPath sortXPath) throws JaxenException {
        if (xpCmp == null) {
            xpCmp = new XPathComparator();
        }
        xpCmp.setXpath(sortXPath);
    }

    /**
     * Sets whether to sort ascending or descending.
     */
    public void setDescending(final boolean descending) {
        if (xpCmp == null) {
            xpCmp = new XPathComparator();
        }
        xpCmp.setDescending(descending);
    }

    /*
     * Override superclass so method can be access by IfTag
     */
    @Override
    protected Object getXPathContext() {
        return super.getXPathContext();
    }

}
