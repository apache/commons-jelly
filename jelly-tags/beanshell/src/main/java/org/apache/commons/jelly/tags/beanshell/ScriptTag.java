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
package org.apache.commons.jelly.tags.beanshell;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import bsh.EvalError;

/**
 * A tag which invokes a BeanShell script.
 */
public class ScriptTag extends TagSupport {

    public ScriptTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        try {
            final JellyInterpreter interpreter = BeanShellExpressionFactory.getInterpreter(context);

            // @todo it'd be really nice to create a JellyNameSpace to pass into
            // this method so that any variables declared by beanshell could be exported
            // into the JellyContext
            final String text = getBodyText(false);
            interpreter.eval(text);
        } catch (final EvalError e) {
            throw new JellyTagException(e);
        }
    }
}
