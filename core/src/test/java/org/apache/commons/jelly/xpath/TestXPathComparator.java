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
package org.apache.commons.jelly.xpath;

import junit.framework.TestCase;
import org.dom4j.QName;
import org.dom4j.bean.BeanElement;
import org.dom4j.dom.DOMDocument;
import org.dom4j.tree.DefaultDocument;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.javabean.DocumentNavigator;
import org.jaxen.javabean.JavaBeanXPath;
import org.jaxen.saxpath.SAXPathException;

/**
 * Unit tests for class {@link XPathComparator}.
 *
 * @see XPathComparator
 *
 */
public class TestXPathComparator extends TestCase {


    public void testCompareTaking2NodesWithNull() throws JaxenException {
        BaseXPath baseXPath = new BaseXPath(".", new DocumentNavigator());
        QName qName = QName.get("org.apache.xerces.impl.XMLNSDocumentScannerImpl$NSContentDispatcher", ".", ".");
        BeanElement beanElement = new BeanElement(qName,  null);

        assertEquals(0, new XPathComparator(baseXPath, false).compare(null, beanElement));

    }


    public void testCreatesXPathComparatorTaking2Arguments() throws SAXPathException {
        JavaBeanXPath javaBeanXPath = (JavaBeanXPath)new DocumentNavigator().parseXPath("3");
        DefaultDocument defaultDocument = new DefaultDocument();

        assertEquals(0, new XPathComparator(javaBeanXPath, true).compare(defaultDocument, defaultDocument));
    }


    public void testCreatesXPathComparatorTakingNoArgumentsAndCallsSetXpath() throws SAXPathException {
        JavaBeanXPath javaBeanXPath = (JavaBeanXPath) new DocumentNavigator().parseXPath("txext()");
        XPathComparator xPathComparator = new XPathComparator();
        xPathComparator.setXpath(javaBeanXPath);

        DOMDocument dOMDocument = new DOMDocument("txext()");

        try {
            xPathComparator.compare(dOMDocument, dOMDocument);
            fail("Expecting exception: XPathSortException");
        } catch(XPathComparator.XPathSortException e) {
            assertEquals(XPathComparator.class.getName(), e.getStackTrace()[0].getClassName());
        }
    }


}