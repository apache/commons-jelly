/*
 * $Header: /home/cvspublic/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/xml/ForEachTag.java,v 1.10 2002/10/30 19:16:23 jstrachan Exp $
 * $Revision: 1.10 $
 * $Date: 2002/10/30 19:16:23 $
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
 * $Id$
 */
package org.apache.commons.jelly.tags.xml;

import java.util.Comparator;
import java.util.List;

import org.dom4j.Node;

import org.jaxen.XPath;
import org.jaxen.JaxenException;

import org.apache.commons.beanutils.ConvertUtils;

import org.apache.commons.jelly.util.NestedRuntimeException;

/**
 * Compares xml nodes by extracting the value at xpath and
 * comparing it.
 *
 * @author <a href="mailto:jason@jhorman.org">Jason Horman</a>
 * @version $Id$
 */

public class XPathComparator implements Comparator {

    /** The xpath to use to extract value from nodes to compare */
    private XPath xpath = null;

    /** Sort descending or ascending */
    private boolean descending = false;

    public XPathComparator() {

    }

    public XPathComparator(XPath xpath, boolean descending) {
        this.xpath = xpath;
        this.descending = descending;
    }

    public void setXpath(XPath xpath) {
        this.xpath = xpath;
    }

    public XPath getXpath() {
        return xpath;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    public int compare(Object o1, Object o2) {
        return compare((Node)o1, (Node)o2);
    }

    public int compare(Node n1, Node n2) {
        try {

            // apply the xpaths. not using stringValueOf since I don't
            // want all of the child nodes appended to the strings
            Object val1 = xpath.evaluate(n1);
            Object val2 = xpath.evaluate(n2);

            // return if null
            if (val1 == null || val2 == null) {
                return val1 == null ? (val2 == null ? 1 : -1) : 1;
            }

            Comparable c1 = getComparableValue(val1);
            Comparable c2 = getComparableValue(val2);

            // compare descending or ascending
            if (!descending) {
                return c1.compareTo(c2);
            } else {
                return c2.compareTo(c1);
            }

        } catch (JaxenException e) {

            throw new XPathSortException("error sorting nodes", e);

        }
    }

    /**
     * Turns the XPath result value into a Comparable object.
     */
    protected Comparable getComparableValue(Object value) {
        if (value instanceof List) {
            List list = (List) value;
            if (list.isEmpty()) {
                value = "";
            }
            value = list.get(0);
            if (value == null) {
                value = "";
            }
        }
        if (value instanceof Comparable) {
            return (Comparable) value;
        }
        else if (value instanceof Node) {
            Node node = (Node) value;
            return node.getStringValue();
        }
        return value.toString();
    }

    /**
     * My own runtime exception in case something goes wrong with sort.
     */
    public static class XPathSortException extends NestedRuntimeException {
        public XPathSortException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
