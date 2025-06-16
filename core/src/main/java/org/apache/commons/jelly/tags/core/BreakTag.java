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
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.impl.BreakException;

/**
 * A tag which terminates the execution of the current &lt;forEach&gt; or &lt;while&gt;
 * loop. This tag can take an optional boolean test attribute which if its true
 * then the break occurs otherwise the loop continues processing.
 */
public class BreakTag extends TagSupport {

    /** The expression to evaluate. */
    private Expression test;

    /**
     * If specified, the given variable will hold a true/false value
     * indicating if the loop was broken.
     */
    private String var;

    public BreakTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws BreakException, JellyTagException {
        boolean broken = false;
        if (test == null || test.evaluateAsBoolean(context)) {
            broken = true;
        }
        if ( var != null ) {
            context.setVariable( this.var, String.valueOf(broken));
        }
        if ( broken ) {
            throw new BreakException();
        }
    }

    /**
     * Sets the Jelly expression to evaluate (optional).
     * If this is {@code null} or evaluates to
     * {@code true} then the loop is terminated
     *
     * @param test the Jelly expression to evaluate
     */
    public void setTest(final Expression test) {
        this.test = test;
    }

    /**
     * Sets the variable name to export indicating if the item was broken
     * @param var name of the variable to be exported
     */
    public void setVar(final String var) {
        this.var = var;
    }

}
