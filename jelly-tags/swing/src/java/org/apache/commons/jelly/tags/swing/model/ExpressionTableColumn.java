/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/define/DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 * $Revision: 1.7 $
 * $Date: 2002/05/17 15:18:12 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 * 
 * $Id: DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.swing.model;

import javax.swing.table.TableColumn;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.expression.Expression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Represents a column in an ExpressionTable
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.7 $
 */
public class ExpressionTableColumn extends TableColumn {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog( ExpressionTableColumn.class );
    
    private Expression value;
    private Class type = Object.class;
    
    public ExpressionTableColumn() {
    }

    public String toString() {
        return super.toString() + "[value:" + value + "]";
    }
    
    /**
     * Evaluates the value of a cell
     */    
    public Object evaluateValue(ExpressionTableModel model, Object row, int rowIndex, int columnIndex) {
        if (value == null) {
            return null;
        }
        // lets put the values in the context
        JellyContext context = model.getContext();
        context.setVariable("rows", model.getRows());
        context.setVariable("columns", model.getColumnList());
        context.setVariable("row", row);
        context.setVariable("rowIndex", new Integer(rowIndex));
        context.setVariable("columnIndex", new Integer(columnIndex));
        
        // now lets invoke the expression
        try {
            return value.evaluateRecurse(context);
        }
        catch (RuntimeException e) {
            log.warn( "Caught exception: " + e + " evaluating: " + value, e );
            throw e;
        }
    }

    // Properties
    //-------------------------------------------------------------------------                    
    
    /**
     * Returns the column type.
     * @return Class
     */
    public Class getType() {
        return type;
    }

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
    public void setType(Class type) {
        this.type = type;
    }

    /**
     * Sets the value.
     * @param value The value to set
     */
    public void setValue(Expression value) {
        this.value = value;
    }

}
