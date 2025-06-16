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
package org.apache.commons.jelly.impl;

import org.apache.commons.jelly.expression.Expression;

/**
 * Represents the attribute definition used by dynamic tags, such as whether the attribute is required
 * or any default values etc.
 */
public class Attribute {

    /** The name of the attribute */
    private String name;

    /** The default value expression */
    private Expression defaultValue;

    /** Whether this attribute is required */
    private boolean required;

    public Attribute() {
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Returns the defaultValue.
     * @return Expression
     */
    public Expression getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns the name.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether this attribute is required.
     * @return boolean
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets the defaultValue.
     * @param defaultValue The defaultValue to set
     */
    public void setDefaultValue(final Expression defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets whether this attribute is required.
     * @param required is true if this attribute is a mandatory attribute
     */
    public void setRequired(final boolean required) {
        this.required = required;
    }

}
