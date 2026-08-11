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
package org.apache.commons.jelly.expression.xpath;

import junit.framework.TestCase;
import org.apache.commons.jelly.expression.ConstantExpression;
import org.apache.commons.jelly.impl.DefaultTagFactory;
import org.apache.commons.jelly.impl.StaticTagScript;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for class {@link XPathExpression}.
 *
 * @see XPathExpression
 *
 */
public class TestXPathExpression extends TestCase {

  public void testCreateUriMapReturningMapWhereIsEmptyIsFalse() {
      XPathExpression xPathExpression = new XPathExpression();
      Map<Object, Object> hashMap = new HashMap<Object, Object>();
      hashMap.put("a", "a");
      Map map = xPathExpression.createUriMap(hashMap);

      assertNull(xPathExpression.toString());
      assertNull(xPathExpression.getExpressionText());

      assertFalse(hashMap.isEmpty());
      assertEquals(1, hashMap.size());

      assertEquals(1, map.size());
      assertFalse(map.isEmpty());
  }

  public void testCreatesXPathExpressionTakingNoArguments() {
      Map<Object, Object> hashMap = new HashMap<Object, Object>();
      hashMap.put( null, hashMap);
      XPathExpression xPathExpression = new XPathExpression();
      Map map = xPathExpression.createUriMap(hashMap);

      assertFalse(hashMap.isEmpty());
      assertEquals(1, hashMap.size());

      assertNull(xPathExpression.toString());
      assertNull(xPathExpression.getExpressionText());

      assertTrue(map.isEmpty());
      assertEquals(0, map.size());
  }

  public void testCreatesXPathExpressionTaking3Arguments() {
      Map<Object, Object> hashMap = new HashMap<Object, Object>();
      hashMap.put("a", "a");
      StaticTagScript staticTagScript = new StaticTagScript(new DefaultTagFactory());
      XPathExpression xPathExpression = new XPathExpression("", new ConstantExpression(""), staticTagScript);
      Map map = xPathExpression.createUriMap(hashMap);

      assertFalse(hashMap.isEmpty());
      assertEquals(1, hashMap.size());

      assertEquals((-1), staticTagScript.getLineNumber());
      assertNull(staticTagScript.getFileName());

      assertEquals((-1), staticTagScript.getColumnNumber());
      assertNull(staticTagScript.getElementName());

      assertNull(staticTagScript.getLocalName());
      assertEquals("", xPathExpression.getExpressionText());

      assertEquals("", xPathExpression.toString());
      assertFalse(map.isEmpty());

      assertEquals(1, map.size());
  }

}