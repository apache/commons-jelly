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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MapTagSupport;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.BeanSource;

/**
 * A tag which sets the bean properties on the given bean.
 * So if you used it as follows, for example using the &lt;j:new&gt; tag.
 * <pre>
 * &lt;j:new className="com.acme.Person" var="person"/&gt;
 * &lt;j:setProperties object="${person}" name="James" location="${loc}"/&gt;
 * </pre>
 * Then it would set the name and location properties on the bean denoted by
 * the expression ${person}.
 * <p>
 * This tag can also be nested inside a bean tag such as the &lt;useBean&gt; tag
 * or a JellySwing tag to set one or more properties, maybe inside some conditional
 * logic.
 */
public class SetPropertiesTag extends MapTagSupport  {

    public SetPropertiesTag(){
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        final Map attributes = getAttributes();
        Object bean = attributes.remove( "object" );
        if ( bean == null ) {
            // lets try find a parent bean
            final BeanSource tag = (BeanSource) findAncestorWithClass(BeanSource.class);
            if (tag != null) {
                try {
                    bean = tag.getBean();
                } catch (final JellyException e) {
                    throw new JellyTagException(e);
                }
            }
            if (bean == null) {
                throw new MissingAttributeException("bean");
            }
        }
        setBeanProperties(bean, attributes);
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the properties on the bean. Derived tags could implement some custom
     * type conversion etc.
     */
    protected void setBeanProperties(final Object bean, final Map attributes) throws JellyTagException {
        try {
            BeanUtils.populate(bean, attributes);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new JellyTagException("could not set the properties on a bean", e);
        }
    }
}
