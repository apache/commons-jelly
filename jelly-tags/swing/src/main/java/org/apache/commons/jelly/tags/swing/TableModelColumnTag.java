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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.jelly.tags.swing.model.ExpressionTableColumn;

/**
 * Creates a default TableColumnModel.
 */
public class TableModelColumnTag extends UseBeanTag {

    public ExpressionTableColumn getColumn() {
        return (ExpressionTableColumn) getBean();
    }

    @Override
    public Class getAttributeType(final String name) throws JellyTagException {
        if (name.equals("value")) {
            return Expression.class;
        }
        return super.getAttributeType(name);
    }

    // Implementation methods
    //-------------------------------------------------------------------------
    @Override
    protected void processBean(final String var, final Object bean) throws JellyTagException {
        super.processBean(var, bean);

        final TableModelTag tag = (TableModelTag) findAncestorWithClass( TableModelTag.class );
        if ( tag == null ) {
            throw new JellyTagException( "This tag must be nested within a <tableModel> tag" );
        }
        tag.getTableModel().addColumn( getColumn() );
    }

    @Override
    protected Class getDefaultClass() {
        return ExpressionTableColumn.class;
    }
}

