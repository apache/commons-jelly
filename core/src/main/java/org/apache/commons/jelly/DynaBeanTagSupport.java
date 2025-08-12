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

package org.apache.commons.jelly;

import org.apache.commons.beanutils2.DynaBean;
import org.apache.commons.beanutils2.DynaProperty;

/**
 * <p>{@code DynaBeanTag} is a DynaTag implementation which uses a DynaBean
 * to store its attribute values in. Derived tags can then process this
 * DynaBean in any way it wishes.
 * </p>
 */

public abstract class DynaBeanTagSupport extends DynaTagSupport {

    /** The DynaBean which is used to store the attributes of this tag. */
    private DynaBean dynaBean;

    public DynaBeanTagSupport() {
    }

    public DynaBeanTagSupport(final DynaBean dynaBean) {
        this.dynaBean = dynaBean;
    }

    /**
     * Callback to allow processing to occur before the attributes are about to be set
     */
    public void beforeSetAttributes() throws JellyTagException {
    }

    /**
     * @return the type of the given attribute. By default just return
     * Object.class if this is not known.
     */
    @Override
    public Class getAttributeType(final String name) throws JellyTagException {
        final DynaProperty property = getDynaBean().getDynaClass().getDynaProperty(name);
        if (property != null) {
            return property.getType();
        }
        return Object.class;
    }

    /**
     * @return the DynaBean which is used to store the
     *  attributes of this tag
     */
    public DynaBean getDynaBean() {
        return dynaBean;
    }

    /** Sets an attribute value of this tag before the tag is invoked
     */
    @Override
    public void setAttribute(final String name, final Object value) throws JellyTagException {
        getDynaBean().set(name, value);
    }

    /** Sets the context in which the tag will be run. */
    @Override
    public void setContext(final JellyContext context) throws JellyTagException {
        this.context = context;
        beforeSetAttributes();
    }

    /**
     * Sets the DynaBean which is used to store the
     *  attributes of this tag
     */
    public void setDynaBean(final DynaBean dynaBean) {
        this.dynaBean = dynaBean;
    }

}
