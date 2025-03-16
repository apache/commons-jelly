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

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.expression.ExpressionFactory;
import org.apache.commons.jelly.expression.CompositeExpression;
import org.apache.commons.jelly.expression.jexl.JexlExpressionFactory;
import org.apache.commons.jelly.expression.xpath.XPathExpression;
import org.apache.commons.jelly.impl.TagScript;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Describes the Taglib. This class could be generated by XDoclet
  */
public class XMLTagLibrary extends TagLibrary {

    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog(XMLTagLibrary.class);

    private JexlExpressionFactory jexlFactory;

    public XMLTagLibrary() {
        registerTag("out", ExprTag.class);
        registerTag("if", IfTag.class);
        registerTag("forEach", ForEachTag.class);
        registerTag("parse", ParseTag.class);
        registerTag("set", SetTag.class);
        registerTag("transform", TransformTag.class);
        registerTag("param", ParamTag.class);

        // extensions to JSTL
        registerTag("expr", ExprTag.class);
        registerTag("element", ElementTag.class);
        registerTag("attribute", AttributeTag.class);
        registerTag("replaceNamespace", ReplaceNamespaceTag.class);
        registerTag("copy", CopyTag.class);
        registerTag("copyOf", CopyOfTag.class);
        registerTag("comment", CommentTag.class);
        registerTag("doctype", DoctypeTag.class);
        registerTag("sort", SortTag.class);

        this.jexlFactory = new JexlExpressionFactory();
    }

    @Override
    public Expression createExpression(
        ExpressionFactory factory,
        TagScript tagScript,
        String attributeName,
        String attributeValue) throws JellyException {

        // #### may need to include some namespace URI information in the XPath instance?

        if (attributeName.equals("select") || attributeName.equals("sort")) {
            if ( log.isDebugEnabled() ) {
                log.debug( "Parsing XPath expression: " + attributeValue );
            }

            Expression xpathExpr = createXPathTextExpression( attributeValue );

            return new XPathExpression(attributeValue,
                                       xpathExpr,
                                       tagScript);
        }

        // will use the default expression instead
        return super.createExpression(factory, tagScript, attributeName, attributeValue);
    }

    protected Expression createXPathTextExpression(String exprText) throws JellyException {
        return CompositeExpression.parse( exprText,
                                          this.jexlFactory );
    }
}
