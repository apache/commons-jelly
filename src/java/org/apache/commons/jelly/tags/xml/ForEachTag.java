/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/xml/Attic/ForEachTag.java,v 1.12 2002/11/27 19:22:41 jstrachan Exp $
 * $Revision: 1.12 $
 * $Date: 2002/11/27 19:22:41 $
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
 * $Id: ForEachTag.java,v 1.12 2002/11/27 19:22:41 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.xml;

import java.util.Iterator;
import java.util.List;
import java.util.Collections;

import org.apache.commons.jelly.XMLOutput;

import org.jaxen.XPath;
import org.jaxen.JaxenException;

/** A tag which performs an iteration over the results of an XPath expression
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.12 $
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
    public void doTag(XMLOutput output) throws Exception {
        if (select != null) {
            List nodes = select.selectNodes( getXPathContext() );

            // sort the list if xpCmp is set.
            if (xpCmp != null && (xpCmp.getXpath() != null)) {
                Collections.sort(nodes, xpCmp);
            }

            Iterator iter = nodes.iterator();
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
    public Object getXPathSource() {
        return iterationValue;
    }
    
    // Properties
    //-------------------------------------------------------------------------                    
    /** Sets the XPath selection expression
      */
    public void setSelect(XPath select) {
        this.select = select;
    }

    /** Sets the variable name to export for the item being iterated over
     */
    public void setVar(String var) {
        this.var = var;
    }

    /** Sets the xpath expression to use to sort selected nodes.
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
