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
package org.apache.commons.jelly.tags.swing.impl;

import java.awt.Component;
import java.awt.GridBagConstraints;

/**
 * A simple class to represent the information for a single cell in a table
 * when using the GridBagLayout
 */
public class Cell {
    private GridBagConstraints constraints;
    private Component component;

    public Cell() {
    }

    public Cell(final GridBagConstraints constraints, final Component component) {
        this.constraints = constraints;
        this.component = component;
    }

    /**
     * Returns the component.
     * @return Component
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Returns the constraints.
     * @return GridBagConstraints
     */
    public GridBagConstraints getConstraints() {
        return constraints;
    }

    /**
     * Sets the component.
     * @param component The component to set
     */
    public void setComponent(final Component component) {
        this.component = component;
    }

    /**
     * Sets the constraints.
     * @param constraints The constraints to set
     */
    public void setConstraints(final GridBagConstraints constraints) {
        this.constraints = constraints;
    }

}
