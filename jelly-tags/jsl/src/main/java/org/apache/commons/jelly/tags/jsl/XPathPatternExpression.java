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
package org.apache.commons.jelly.tags.jsl;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.expression.ExpressionSupport;
import org.dom4j.rule.Pattern;
import org.jaxen.VariableContext;

/** An expression which returns an XPath based Pattern (like an XSLT pattern).
  */
public class XPathPatternExpression extends ExpressionSupport implements VariableContext {

    private String text;
    private Pattern pattern;
    private JellyContext context;

    public XPathPatternExpression(String text, Pattern pattern) {
        this.text = text;
        this.pattern = pattern;
    }

    // Expression interface
    //-------------------------------------------------------------------------
    @Override
    public String getExpressionText() {
        return text;
    }

    @Override
    public Object evaluate(JellyContext context) {
        this.context = context;
        //pattern.setVariableContext(this);
        return pattern;
    }

    // VariableContext interface
    //-------------------------------------------------------------------------
    @Override
    public Object getVariableValue(
        String namespaceURI,
        String prefix,
        String localName) {

        Object value = context.getVariable(localName);

        //log.info( "Looking up XPath variable of name: " + localName + " value is: " + value );

        return value;
    }
}
