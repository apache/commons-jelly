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
package org.apache.commons.jelly.tags.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.CollectionTag;

/**
 * A simple tag which demonstrates how to process beans generically.
 * @version  $Revision$
 */
public class MyContainerTag extends TagSupport implements CollectionTag {

    private List list = new ArrayList();
    private String var;

    public MyContainerTag() {
    }

    // CollectionTag interface
    //-------------------------------------------------------------------------
    @Override
    public void addItem(final Object value) {
        list.add(value);
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        invokeBody(output);
        context.setVariable(var, list);
        list = new ArrayList();
    }

    // Properties
    //-------------------------------------------------------------------------
    /**
     * @return String
     */
    public String getVar() {
        return var;
    }

    /**
     * Sets the var.
     * @param var The var to set
     */
    public void setVar(final String var) {
        this.var = var;
    }

}
