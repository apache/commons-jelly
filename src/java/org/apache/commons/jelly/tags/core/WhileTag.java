/*
 * $Header: /home/cvspublic/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/core/ForEach.java,v 1.15 2002/06/26 14:50:43 jstrachan Exp $
 * $Revision: 1.15 $
 * $Date: 2002/06/26 14:50:43 $
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
 * $Id: ForEachTag.java,v 1.15 2002/06/26 14:50:43 jstrachan Exp $
 */

package org.apache.commons.jelly.tags.core;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.impl.BreakException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A tag which performs an iteration while the result of an expression is true.
 *
 * @author <a href="mailto:eric@ericalexander.net">Eric Alexander</a>
 * @author dIon Gillard
 * @version $Revision: 1.15 $
 */
public class WhileTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(WhileTag.class);

    /** The expression to use to determine if the while should continue */
    private Expression test;

    /**
     * Create a new while tag
     */
    public WhileTag() {
    }

    /**
     * Tag interface
     * @param output destination for xml output
     * @throws MissingAttributeException when the test attribute is missing
     * @throws Exception for anything else
     */
    public void doTag(XMLOutput output) throws MissingAttributeException, 
    Exception {
        if (test != null) {
            try {
                while (test.evaluateAsBoolean(getContext())) {
                    if (log.isDebugEnabled()) {
                        log.debug("evaluated to true! gonna keep on chuggin!");
                    }
                    invokeBody(output);
                }
            }
            catch (BreakException e) {
                if (log.isDebugEnabled()) {
                    log.debug("loop terminated by break: " + e, e);
                }
            }
        } 
        else {
            throw new MissingAttributeException("test");
        }
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Setter for the expression
     * @param e the expression to test
     */
    public void setTest(Expression e) {
        this.test = e;
    }
}

