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

package org.apache.commons.jelly.tags.core;

import java.util.Iterator;

import javax.servlet.jsp.jstl.core.LoopTagStatus;

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
  * Uses the same syntax as the <a href="https://java.sun.com/products/jsp/jstl/">JSTL</a>
  * {@code forEach} tag does.
  */
public class ForEachTag extends TagSupport {

    /**
     * Holds the status of the loop.
     */
    public static final class LoopStatus implements LoopTagStatus
    {
        private final Integer begin;
        private int count;
        private Object current;
        private final Integer end;
        private int index;
        private final Integer step;
        private boolean first;
        private boolean last;

        public LoopStatus(final Integer begin, final Integer end, final Integer step) {
            this.begin = begin;
            this.end = end;
            this.step = step;
        }
        /**
         * @return the begin.
         */
        @Override
        public Integer getBegin() {
            return begin;
        }
        /**
         * @return the count.
         */
        @Override
        public int getCount() {
            return count;
        }
        /**
         * @return the current.
         */
        @Override
        public Object getCurrent() {
            return current;
        }
        /**
         * @return the end.
         */
        @Override
        public Integer getEnd() {
            return end;
        }
        /**
         * @return the index.
         */
        @Override
        public int getIndex() {
            return index;
        }
        /**
         * @return the step.
         */
        @Override
        public Integer getStep() {
            return step;
        }
        /**
         * @return the first.
         */
        @Override
        public boolean isFirst() {
            return first;
        }
        /**
         * @return the last.
         */
        @Override
        public boolean isLast() {
            return last;
        }
        /**
         * @param count The count to set.
         */
        public void setCount(final int count) {
            this.count = count;
        }
        /**
         * @param current The current to set.
         */
        public void setCurrent(final Object current) {
            this.current = current;
        }
        /**
         * @param first The first to set.
         */
        public void setFirst(final boolean first) {
            this.first = first;
        }
        /**
         * @param index The index to set.
         */
        public void setIndex(final int index) {
            this.index = index;
        }
        /**
         * @param last The last to set.
         */
        public void setLast(final boolean last) {
            this.last = last;
        }
    }

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

    /** Variable to hold loop status */
    private String statusVar;

    /** The starting index value */
    private int begin;

    /** The ending index value */
    private int end = Integer.MAX_VALUE;

    /** The index increment step */
    private int step = 1;

    /** The iteration index */
    private int index;

    // Tag interface

    public ForEachTag() {
    }

    // Properties
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {

        if (log.isDebugEnabled()) {
            log.debug("running with items: " + items);
        }

        try {
            if (items != null) {
                final Iterator iter = items.evaluateAsIterator(context);

                if (log.isDebugEnabled()) {
                    log.debug("Iterating through: " + iter);
                }

                // ignore the first items of the iterator
                for (index = 0; index < begin && iter.hasNext(); index++ ) {
                    iter.next();
                }

                // set up the status
                LoopStatus status = null;
                if (statusVar != null)
                {
                    // set up statii as required by JSTL
                    final Integer statusBegin = begin == 0 ? null : Integer.valueOf(begin);
                    final Integer statusEnd = end == Integer.MAX_VALUE ? null : Integer.valueOf(end);
                    final Integer statusStep = step == 1 ? null : Integer.valueOf(step);
                    status = new LoopStatus(statusBegin, statusEnd, statusStep);
                    context.setVariable(statusVar, status);
                }

                boolean firstTime = true;
                int count = 0;
                while (iter.hasNext() && index <= end) {
                    final Object value = iter.next();
                    if (var != null) {
                        context.setVariable(var, value);
                    }
                    if (indexVar != null) {
                        context.setVariable(indexVar, Integer.valueOf(index));
                    }
                    // set the status var up
                    if (statusVar != null) {
                        count++;
                        status.setCount(count);
                        status.setCurrent(value);
                        status.setFirst(firstTime);
                        status.setIndex(index);
                        // set first time up for the next iteration.
                        if (firstTime) {
                            firstTime = !firstTime;
                        }
                    }
                    // now we need to work out the next index for status isLast
                    // and also advance the iterator and index for the loop.
                    boolean finished = false;
                    index++;
                    for ( int i = 1; i < step && !finished; i++, index++ ) {
                        if ( ! iter.hasNext() ) {
                           finished = true;
                        }
                        else {
                            iter.next();
                        }
                    }

                    if (statusVar != null) {
                        status.setLast(finished || !iter.hasNext() || index > end);
                    }
                    invokeBody(output);

                }
            } else if ( end == Integer.MAX_VALUE && begin == 0 ) {
                throw new MissingAttributeException( "items" );
            }
            else {
                String varName = var;
                if ( varName == null ) {
                    varName = indexVar;
                }
                // set up the status
                LoopStatus status = null;
                if (statusVar != null)
                {
                    // set up statii as required by JSTL
                    final Integer statusBegin = Integer.valueOf(begin);
                    final Integer statusEnd = Integer.valueOf(end);
                    final Integer statusStep = Integer.valueOf(step);
                    status = new LoopStatus(statusBegin, statusEnd, statusStep);
                    context.setVariable(statusVar, status);
                }

                int count = 0;
                for (index = begin; index <= end; index += step ) {

                    final Object value = Integer.valueOf(index);
                    if (varName != null) {
                        context.setVariable(varName, value);
                    }
                    // set the status var up
                    if (status != null) {
                        count++;
                        status.setIndex(index);
                        status.setCount(count);
                        status.setCurrent(value);
                        status.setFirst(index == begin);
                        status.setLast(index > end - step);
                    }
                    invokeBody(output);
                }
            }
        }
        catch (final BreakException e) {
            if (log.isDebugEnabled()) {
                log.debug("loop terminated by break: " + e, e);
            }
        }
    }

    /** Sets the starting index value
     */
    public void setBegin(final int begin) {
        this.begin = begin;
    }

    /** Sets the ending index value
     */
    public void setEnd(final int end) {
        this.end = end;
    }

    /** Sets the variable name to export the current index counter to
     */
    public void setIndexVar(final String indexVar) {
        this.indexVar = indexVar;
    }

    /**
     * Sets the expression used to iterate over.
     * This expression could resolve to an Iterator, Collection, Map, Array,
     * Enumeration or comma separated String.
     */
    public void setItems(final Expression items) {
        this.items = items;
    }

    /** Sets the index increment step
     */
    public void setStep(final int step) {
        this.step = step;
    }

    /** Sets the variable name to export for the item being iterated over
     */
    public void setVar(final String var) {
        this.var = var;
    }

    /**
     * Sets the variable name to export the current status to.
     * The status is an implementation of the JSTL LoopTagStatus interface that provides
     * the following bean properties:
     * <ul>
     *   <li>current - the current value of the loop items being iterated</li>
     *   <li>index   - the current index of the items being iterated</li>
     *   <li>first   - true if this is the first iteration, false otherwise</li>
     *   <li>last    - true if this is the last iteration, false otherwise</li>
     *   <li>begin   - the starting index of the loop</li>
     *   <li>step    - the stepping value of the loop</li>
     *   <li>end     - the end index of the loop</li>
     * </ul>
     */
    public void setVarStatus(final String var) {
        this.statusVar = var;
    }
}
