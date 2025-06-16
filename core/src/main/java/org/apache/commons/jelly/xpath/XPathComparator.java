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
package org.apache.commons.jelly.xpath;

import java.util.Comparator;
import java.util.List;

import org.dom4j.Node;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

/**
 * Compares XML nodes by extracting the value at xpath and
 * comparing it.
 */

public class XPathComparator implements Comparator {

    /**
     * My own runtime exception in case something goes wrong with sort.
     */
    public static class XPathSortException extends RuntimeException {
        public XPathSortException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    /** The xpath to use to extract value from nodes to compare */
    private XPath xpath = null;

    /** Sort descending or ascending */
    private boolean descending = false;

    public XPathComparator() {

    }

    public XPathComparator(final XPath xpath, final boolean descending) {
        this.xpath = xpath;
        this.descending = descending;
    }

    public int compare(final Node n1, final Node n2) {
        try {

            // apply the xpaths. not using stringValueOf since I don't
            // want all of the child nodes appended to the strings
            final Object val1 = xpath.evaluate(n1);
            final Object val2 = xpath.evaluate(n2);

            // return if null
            if (val1 == null || val2 == null) {
                return val1 == null ? val2 == null ? 1 : -1 : 1;
            }

            final Comparable c1 = getComparableValue(val1);
            final Comparable c2 = getComparableValue(val2);

            // compare descending or ascending
            if (!descending) {
                return c1.compareTo(c2);
            }
            return c2.compareTo(c1);

        } catch (final JaxenException e) {

            throw new XPathSortException("error sorting nodes", e);

        }
    }

    @Override
    public int compare(final Object o1, final Object o2) {
        return compare((Node)o1, (Node)o2);
    }

    /**
     * Turns the XPath result value into a Comparable object.
     */
    protected Comparable getComparableValue(Object value) {
        if (value instanceof List) {
            final List list = (List) value;
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
        if (value instanceof Node) {
            final Node node = (Node) value;
            return node.getStringValue();
        }
        return value.toString();
    }

    public XPath getXpath() {
        return xpath;
    }

    public void setDescending(final boolean descending) {
        this.descending = descending;
    }

    public void setXpath(final XPath xpath) {
        this.xpath = xpath;
    }
}
