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
package org.apache.commons.jelly.tags.bsf;

import org.apache.bsf.util.ObjectRegistry;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** A BSF ObjectRegistry which uses the Context to find and
  * register objects
  */
public class JellyContextRegistry extends ObjectRegistry {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(JellyContextRegistry.class);

    /** The context */
    private JellyContext context;

    public JellyContextRegistry() {
    }

    public JellyContext getJellyContext() {
        return context;
    }

    // ObjectRegistry interface
    //-------------------------------------------------------------------------
    @Override
    public Object lookup(final String name) {
        return context.getVariable(name);
    }

    @Override
    public void register(final String name, final Object value) {
        context.setVariable(name, value);
    }

    public void setJellyContext(final JellyContext context) {
        this.context = context;
    }

    @Override
    public void unregister(final String name) {
        context.removeVariable(name);
    }
}
