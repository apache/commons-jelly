/*
 * Copyright 2002,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jelly.tags.core;

import java.util.Iterator;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.impl.BreakException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
  * Iterates over a collection, iterator or an array of objects.
  * Uses the same syntax as the <a href="http://java.sun.com/products/jsp/jstl/">JSTL</a>
  * <code>forEach</code> tag does.
  * 
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.26 $
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
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {

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

    /**
     * Sets the expression used to iterate over.
     * This expression could resolve to an Iterator, Collection, Map, Array,
     * Enumeration or comma separated String. 
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
    
    /**
     * Sets the variable name to export the current index to. 
     * This does the same thing as #setIndexVar(), but is consistent
     * with the <a href="http://java.sun.com/products/jsp/jstl/">JSTL</a>
     * syntax.
     */
    public void setVarStatus(String var) {
            setIndexVar( var );
    }
}
