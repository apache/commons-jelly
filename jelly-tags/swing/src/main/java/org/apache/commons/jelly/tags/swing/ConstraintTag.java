/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.commons.jelly.tags.swing;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.jelly.DynaBeanTagSupport;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.TagFactory;
import org.xml.sax.Attributes;

/** This class represents a layout-manager constraints as passed in
    * the second argument of {@link Container#add(Component,Object)}.
    *    <p>
    *    In essence, it looks really like nothing else than a bean-class...
    *    with {@link #getConstraintObject}.
    *    Probably a shorter java-source is do-able.
    *    </p>
    *    <p>
    *    TODO: this class should probably be extended with special treatment for dimensions
    *    using the converter package.
    *    </p>
    */
public class ConstraintTag extends DynaBeanTagSupport {

/*    TODO: make a gridbagconstraintTag class which supports an attribute "parent"
                        (or... startWith) which is another gridbagconstraintTag whose gridbagconstraint
                        is cloned then attributes are set
                        This tag should also support the attributes such as fill=BOTH
                        and anchor=NORTHEAST...
                        Whoops... need to define setters ?? let's see if BeanUtils does it on public vars
                        And... have an insets?? A child ?
    */

    public static class ConstantFactory implements TagFactory, Factory {
        private final Object constant;
        public ConstantFactory(final Object c) { this.constant = c;}
        @Override
        public Tag createTag(final String name, final Attributes attributes) throws JellyException {
            return new ConstraintTag ( this );
        }
        @Override
        public Object newInstance() { return constant; }
    } // class ConstantStringFactory
    public static class HereFactory extends BeanFactory implements TagFactory {
        public HereFactory(final Class c) { super(c); }
        @Override
        public Tag createTag(final String name, final Attributes attributes) {
            return new ConstraintTag ( this );
            // still scratching my head about "this" usage...
        }
    } // class HereFactory
    protected Factory factory;

        protected String var = null;
        protected Object bean = null;

        // we could be able to make factories that create their tags in parametrized
        // subclasses of the tag depending on the name and attributes
        // it would useful, for example, to make a cardLayout's <card name="">

    public ConstraintTag (final Factory factory) {
        this.factory = factory;
    }

    @Override
    public void beforeSetAttributes (  ) throws JellyTagException {
        try {
            createBean(factory);
        } catch (final InstantiationException e) {
            throw new JellyTagException(e.toString());
        }
    }

    protected void createBean ( final Factory factory ) throws InstantiationException {
        bean = factory.newInstance();
    }

    /** Children invocation... just nothing...
        */
    @Override
    public void doTag ( final XMLOutput output ) throws JellyTagException {
        if ( var != null ) {
            context.setVariable ( var, getBean() );
        }
        invokeBody ( output );
        // nothing else to do... the getConstraintObject method should have been called.
    }
    public Object getBean() {
        return bean;
    }

    /** Returns the attached constraint object.
        */
    public Object getConstraintObject() {
        return getBean();
    }

    @Override
    public void setAttribute ( final String name, final Object value ) throws JellyTagException {
        // no real need for DynaBeans or ?
        if ( "var".equals(name) ) {
            var = value.toString();
        } else {

            try {
              BeanUtils.setProperty( bean, name, value );
            } catch (final IllegalAccessException | InvocationTargetException e) {
                throw new JellyTagException(e.toString());
            }

        }
    }
}
