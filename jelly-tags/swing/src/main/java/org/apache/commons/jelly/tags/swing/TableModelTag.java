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

import javax.swing.JTable;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.jelly.tags.swing.model.ExpressionTableModel;

/**
 * Creates a default TableModel using nested tableColumn tags.
 */
public class TableModelTag extends UseBeanTag {

    @Override
    protected Class getDefaultClass() {
        return ExpressionTableModel.class;
    }

    public ExpressionTableModel getTableModel() {
        return (ExpressionTableModel) getBean();
    }

    // Implementation methods
    //-------------------------------------------------------------------------
    @Override
    protected void processBean(final String var, final Object bean) throws JellyTagException {
        super.processBean(var, bean);

        final ComponentTag tag = (ComponentTag) findAncestorWithClass( ComponentTag.class );
        if ( tag == null ) {
            throw new JellyTagException( "This tag must be nested within a JellySwing <table> tag" );
        }
        final ExpressionTableModel model = getTableModel();
        model.setContext(context);

        final Object component = tag.getComponent();
        if (!(component instanceof JTable)) {
            throw new JellyTagException( "This tag must be nested within a JellySwing <table> tag" );
        }
        final JTable table = (JTable) component;
        table.setModel(model);
    }
}

