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
package org.apache.commons.jelly.tags.dynabean;

import org.apache.commons.beanutils2.DynaBean;
import org.apache.commons.beanutils2.DynaClass;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/** A tag which conditionally evaluates its body based on some condition
  */
public class DynabeanTag extends TagSupport {

    private DynaClass dynaClass;
    private String var;

    public DynabeanTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {

        if (dynaClass == null) {
            throw new MissingAttributeException( "dynaclass" );
        }

        if (var == null) {
            throw new MissingAttributeException( "var" );
        }

        try {
            // Create dynabean instance for this dynaclass
            final DynaBean dynaBean = dynaClass.newInstance();

            // Place new dynabean in context as a variable
            context.setVariable(getVar(), dynaBean);
        } catch (final IllegalAccessException | InstantiationException e) {
            throw new JellyTagException(e);
        }
    }

    // Properties
    //-------------------------------------------------------------------------

    public String getVar() {
        return var;
    }

    /**
     * Sets the DynaClass of the new instance to create
     */
    public void setDynaclass(final DynaClass dynaClass) {
        this.dynaClass = dynaClass;
    }

    /**
     * Sets the name of the variable to export the new DynaBean instance to
     */
    public void setVar(final String var) {
        this.var = var;
    }

}
