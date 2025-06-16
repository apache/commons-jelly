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

package org.apache.commons.jelly.tags.core;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.impl.BreakException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A tag which performs an iteration while the result of an expression is true.
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
     * @param output destination for XML output
     * @throws MissingAttributeException when the test attribute is missing
     * @throws RuntimeException for anything else
     */
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (test == null) {
            throw new MissingAttributeException("test");
        }
        try {
            while (test.evaluateAsBoolean(getContext())) {
                if (log.isDebugEnabled()) {
                    log.debug("evaluated to true! gonna keep on chuggin!");
                }
                invokeBody(output);
            }
        }
        catch (final BreakException e) {
            if (log.isDebugEnabled()) {
                log.debug("loop terminated by break: " + e, e);
            }
        }
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Setter for the expression
     * @param e the expression to test
     */
    public void setTest(final Expression e) {
        this.test = e;
    }
}

