/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/core/ForEachTag.java,v 1.17 2002/10/24 06:59:53 jstrachan Exp $
 * $Revision: 1.17 $
 * $Date: 2002/10/24 06:59:53 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * $Id: ForEachTag.java,v 1.17 2002/10/24 06:59:53 jstrachan Exp $
 */

package org.apache.commons.jelly.tags.core;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.impl.BreakException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** A tag which performs an iteration over the results of an XPath expression
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.17 $
  */
public class ForEachTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ForEachTag.class);

    /** Holds the variable name to export for the item being iterated over. */
    private Expression items;

    /** 
     * If specified then the current item iterated through will be defined
     * as the given variable name. 
     */
    private String var;

    /** 
     * If specified then the current index counter will be defined
     * as the given variable name. 
     */
    private String indexVar;

    /** The starting index value */
    private int begin;

    /** The ending index value */
    private int end = Integer.MAX_VALUE;

    /** The index increment step */
    private int step = 1;

    /** The iteration index */
    private int index;

    public ForEachTag() {
    }

    // Tag interface

    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("running with items: " + items);
        }

        try {            
            if (items != null) {
                Iterator iter = items.evaluateAsIterator(context);
                if (log.isDebugEnabled()) {
                    log.debug("Iterating through: " + iter);
                }
    
                // ignore the first items of the iterator
                for (index = 0; index < begin && iter.hasNext(); index++ ) {
                    iter.next();
                }
                
                while (iter.hasNext() && index < end) {
                    Object value = iter.next();
                    if (var != null) {
                        context.setVariable(var, value);
                    }
                    if (indexVar != null) {
                        context.setVariable(indexVar, new Integer(index));
                    }
                    invokeBody(output);
                    
                    // now we need to move to next index
                    index++;
                    for ( int i = 1; i < step; i++, index++ ) {
                        if ( ! iter.hasNext() ) {
                           return;
                        }
                        iter.next();
                    }
                }
            }
            else {
                if ( end == Integer.MAX_VALUE && begin == 0 ) {
                    throw new MissingAttributeException( "items" );
                }
                else {
                    String varName = var;
                    if ( varName == null ) {
                        varName = indexVar;
                    }
                    
                    for (index = begin; index <= end; index += step ) {
                    
                        if (varName != null) {
                            Object value = new Integer(index);
                            context.setVariable(varName, value);
                        }
                        invokeBody(output);
                    }
                }
            }
        }
        catch (BreakException e) {
            if (log.isDebugEnabled()) {
                log.debug("loop terminated by break: " + e, e);
            }
        }
    }

    // Properties
    //-------------------------------------------------------------------------                    

    /** Sets the expression used to iterate over
      */
    public void setItems(Expression items) {
        this.items = items;
    }

    /** Sets the variable name to export for the item being iterated over
     */
    public void setVar(String var) {
        this.var = var;
    }

    /** Sets the variable name to export the current index counter to 
     */
    public void setIndexVar(String indexVar) {
        this.indexVar = indexVar;
    }

    /** Sets the starting index value 
     */
    public void setBegin(int begin) {
        this.begin = begin;
    }

    /** Sets the ending index value 
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /** Sets the index increment step 
     */
    public void setStep(int step) {
        this.step = step;
    }
}
