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
package org.apache.commons.jelly.tags.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Map;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.jelly.tags.swing.impl.GridBagConstraintBean;
import org.apache.commons.lang3.StringUtils;

/**
 * This class represents a {@link GridBagConstraints} constraints as passed in
 * the second argument of {@link Container#add(Component,Object)}.
 * It supports inheritance between such tags in the following fashion:
 * <ul>
 *     <li>either using a <code>basedOn</code> attribute which is
 *         supposed to provide a reference to another {@link GbcTag}.</li>
 *     <li>either using a parent {@link GbcTag}.</li>
 * </ul>
 * The first version takes precedence.
 * A Grid-bag-constraint inherits from another simply by setting other attributes
 * as is done in {@link GridBagConstraintBean#setBasedOn}.
 * <p>
 * In essence, it looks really like nothing else than a bean-class...
 * with {@link #getConstraints}.
 * Probably a shorter java-source is do-able.
 * <p>
 * TODO: this class should probably be extended with special treatment for dimensions
 * using the converter package.
 */
public class GbcTag extends UseBeanTag implements ContainerTag {

    public static Tag findAncestorWithClass(
        Tag from,
        final Class tagClass,
        final Tag parent) {
        while (from != null && from != parent) {
            if (tagClass.isInstance(from)) {
                return from;
            }
            from = from.getParent();
        }
        return null;
    }

    // ContainerTag interface
    //-------------------------------------------------------------------------

    /**
     * Adds a child component to this parent
     * <p>
     * TODO constraints looks like it's ignored
     * </p>
     * @param component the child to add
     * @param constraints the constraints to use
     */
    @Override
    public void addChild(final Component component, final Object constraints) throws JellyTagException {
        final GridBagLayoutTag tag = (GridBagLayoutTag) findAncestorWithClass( GridBagLayoutTag.class );
        if (tag == null) {
            throw new JellyTagException( "this tag must be nested within a <gridBagLayout> tag" );
        }
        tag.addLayoutComponent(component, getConstraints());
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * A class may be specified otherwise the Factory will be used.
     * @param classObject the object to be converted
     */
    @Override
    protected Class convertToClass(final Object classObject)
    throws MissingAttributeException, ClassNotFoundException {
        if (classObject == null) {
            return null;
        }
        return super.convertToClass(classObject);
    }

    public GridBagConstraints getConstraints() {
        return (GridBagConstraints) getBean();
    }

    /**
     * A class may be specified otherwise the Factory will be used.
     */
    @Override
    protected Object newInstance(final Class theClass, final Map attributes, final XMLOutput output) throws JellyTagException {
        if (theClass == null ) {
            return new GridBagConstraintBean();
        }
        try {
            return theClass.getConstructor().newInstance();
        } catch (final ReflectiveOperationException e) {
            throw new JellyTagException(e);
        }
    }

    @Override
    protected void setBeanProperties(final Object bean, final Map attributes)
        throws JellyTagException {

        Insets ins = null;
        final Object insetString = attributes.get("insets");
        if (insetString instanceof String) {
            attributes.remove("insets");

            final String[] parts = StringUtils.split((String) insetString, ",");

            if (parts.length != 4) {
                throw new JellyTagException(
                    "insets must be specified"
                        + "as four comma - separated integers.");
            }

            ins =
                new Insets(
                    Integer.parseInt(parts[0].trim()),
                    Integer.parseInt(parts[1].trim()),
                    Integer.parseInt(parts[2].trim()),
                    Integer.parseInt(parts[3].trim()));
        }

        super.setBeanProperties(bean, attributes);

        // set basedOn info of the bean if we have a parent gbc tag
        // in the context of the closest gridbaglayout tag

        if (bean instanceof GridBagConstraintBean) {
            final GridBagConstraintBean gbc = (GridBagConstraintBean) bean;

            if (ins != null) {
                gbc.setInsets(ins);
            }

            final GridBagLayoutTag parentLayoutTag =
                (GridBagLayoutTag) findAncestorWithClass(GridBagLayoutTag
                    .class);
            if (parentLayoutTag != null) {
                final GbcTag parentGbcTag =
                    (GbcTag) findAncestorWithClass(getParent(),
                        GbcTag.class,
                        parentLayoutTag);
                if (parentGbcTag != null) {
                    final GridBagConstraints parentGbc =
                        parentGbcTag.getConstraints();

                    if (parentGbc != null
                        && parentGbc instanceof GridBagConstraintBean) {
                        gbc.setBasedOn((GridBagConstraintBean) parentGbc);
                        if (insetString == null) {
                            gbc.setInsets(parentGbc.insets);
                        }
                    }
                }
            }
        }
    }
}

