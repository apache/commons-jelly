/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/junit/src/java/org/apache/commons/jelly/tags/junit/AssertTag.java,v 1.2 2003/01/24 23:23:47 morgand Exp $
 * $Revision: 1.2 $
 * $Date: 2003/01/24 23:23:47 $
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
 * $Id: AssertTag.java,v 1.2 2003/01/24 23:23:47 morgand Exp $
 */
package org.apache.commons.jelly.tags.junit;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jaxen.JaxenException;
import org.jaxen.XPath;

/** 
 * Performs an assertion that a given boolean expression, or XPath expression is
 * true. If the expression returns false then this test fails.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.2 $
 */
public class AssertTag extends AssertTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(AssertTag.class);
    
    /** The expression to evaluate. */
    private Expression test;
    
    /** The XPath expression to evaluate */
    private XPath xpath;

    public AssertTag() {
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (test == null && xpath == null) {
            throw new MissingAttributeException( "test" );
        }
        if (test != null) {
            if (! test.evaluateAsBoolean(context)) {
                fail( getBodyText(), "evaluating test: "+ test.getExpressionText() );
            }
        }
        else {
            Object xpathContext = getXPathContext();
            try {
                if (! xpath.booleanValueOf(xpathContext)) {
                    fail( getBodyText(), "evaluating xpath: "+ xpath );
                }
            }
            catch (JaxenException e) {
                throw new JellyTagException(e);
            }
        }

    }
    
    // Properties
    //-------------------------------------------------------------------------                

    /** 
     * Sets the boolean expression to evaluate. If this expression returns true
     * then the test succeeds otherwise if it returns false then the text will
     * fail with the content of the tag being the error message.
     */
    public void setTest(Expression test) {
        this.test = test;
    }

    /** 
     * Sets the boolean XPath expression to evaluate. If this expression returns true
     * then the test succeeds otherwise if it returns false then the text will
     * fail with the content of the tag being the error message.
     */
    public void setXpath(XPath xpath) {
        this.xpath = xpath;
    }
}
