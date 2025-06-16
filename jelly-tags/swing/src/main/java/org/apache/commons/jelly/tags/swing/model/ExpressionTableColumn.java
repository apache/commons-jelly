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
package org.apache.commons.jelly.tags.swing.model;

import javax.swing.table.TableColumn;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents a column in an ExpressionTable
 */
public class ExpressionTableColumn extends TableColumn {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog( ExpressionTableColumn.class );

    private Expression value;
    private Class type = Object.class;

    public ExpressionTableColumn() {
    }

    /**
     * Evaluates the value of a cell
     */
    public Object evaluateValue(final ExpressionTableModel model, final Object row, final int rowIndex, final int columnIndex) {
        if (value == null) {
            return null;
        }
        // lets put the values in the context
        final JellyContext context = model.getContext();
        context.setVariable("rows", model.getRows());
        context.setVariable("columns", model.getColumnList());
        context.setVariable("row", row);
        context.setVariable("rowIndex", new Integer(rowIndex));
        context.setVariable("columnIndex", new Integer(columnIndex));

        // now lets invoke the expression
        try {
            return value.evaluateRecurse(context);
        }
        catch (final RuntimeException e) {
            log.warn( "Caught exception: " + e + " evaluating: " + value, e );
            throw e;
        }
    }

    /**
     * Returns the column type.
     * @return Class
     */
    public Class getType() {
        return type;
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Returns the expression used to extract the value.
     * @return Expression
     */
    public Expression getValue() {
        return value;
    }

    /**
     * Sets the expression used to extract the value.
     * @param type The type to set
     */
    public void setType(final Class type) {
        this.type = type;
    }

    /**
     * Sets the value.
     * @param value The value to set
     */
    public void setValue(final Expression value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + "[value:" + value + "]";
    }

}
