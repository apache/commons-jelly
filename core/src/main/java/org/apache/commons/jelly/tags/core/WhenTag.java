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

/** A tag which conditionally evaluates its body based on some condition
  */
public class WhenTag extends TagSupport {

    /** The expression to evaluate. */
    private Expression test;

    public WhenTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        final ChooseTag tag = (ChooseTag) findAncestorWithClass( ChooseTag.class );
        if ( tag == null ) {
            throw new JellyTagException( "This tag must be enclosed inside a <choose> tag" );
        }
        if ( ! tag.isBlockEvaluated() && test != null && test.evaluateAsBoolean( context ) ) {
            tag.setBlockEvaluated(true);
            invokeBody(output);
        }
    }

    // Properties
    //-------------------------------------------------------------------------

    /** Sets the expression to evaluate. */
    public void setTest(final Expression test) {
        this.test = test;
    }

}
