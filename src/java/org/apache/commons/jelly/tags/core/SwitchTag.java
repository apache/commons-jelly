/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/core/SwitchTag.java,v 1.1 2002/10/22 15:13:43 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2002/10/22 15:13:43 $
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
 * $Id: SwitchTag.java,v 1.1 2002/10/22 15:13:43 rwaldhoff Exp $
 */
package org.apache.commons.jelly.tags.core;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;

import bsh.This;

/** 
 * Executes the child &lt;case&gt; tag whose value equals my on attribute.
 * Executes a child &lt;default&gt; tag when present and no &lt;case&gt; tag has
 * yet matched.
 * 
 * @see CaseTag
 * @see DefaultTag
 * 
 * @author Rodney Waldhoff
 * @version $Revision: 1.1 $ $Date: 2002/10/22 15:13:43 $
 */
public class SwitchTag extends TagSupport {

    public SwitchTag() {
    }
    
    // Tag interface
    //------------------------------------------------------------------------- 

    /**
     * Sets the value to switch on.
     * Note that the {@link Expression} is evaluated only once, when the 
     * &lt;switch&gt; tag is evaluated.
     * @param on the value to switch on
     */
    public void setOn(Expression on) {
        this.on = on;
    }
    
    public void doTag(XMLOutput output) throws Exception {
        if(null == on) {
            throw new MissingAttributeException("on");
        } else {
            value = on.evaluate(context);
            invokeBody(output);
        }
    }
    
    // Protected properties
    //------------------------------------------------------------------------- 
    protected boolean hasSomeCaseMatched() {
        return this.someCaseMatched;
    }
    
    protected void caseMatched() {
        this.someCaseMatched = true;
    }
    
    protected boolean isFallingThru() {
        return this.fallingThru;
    }
    
    protected void setFallingThru(boolean fallingThru) {
        this.fallingThru = fallingThru;
    }
    
    protected Object getValue() {
        return value;        
    }

    protected boolean hasDefaultBeenEncountered() {
        return defaultEncountered;        
    }

    protected void defaultEncountered() {
        this.defaultEncountered = true;
    }

    // Attributes
    //------------------------------------------------------------------------- 
    private boolean someCaseMatched = false;
    private boolean fallingThru = false;
    private boolean defaultEncountered = false;
    private Expression on = null;
    private Object value = null;

}
