/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/core/CaseTag.java,v 1.3 2002/10/30 19:16:21 jstrachan Exp $
 * $Revision: 1.3 $
 * $Date: 2002/10/30 19:16:21 $
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
 * $Id: CaseTag.java,v 1.3 2002/10/30 19:16:21 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.core;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;

/** 
 * A tag which conditionally evaluates its body if
 * my {@link #setValue value} attribute equals my ancestor
 * {@link SwitchTag &lt;switch&gt;} tag's 
 * {@link SwitchTag#setOn "on"} attribute.
 * 
 * This tag must be contained within the body of some 
 * {@link SwitchTag &lt;switch&gt;} tag.
 * 
 * @see SwitchTag
 * 
 * @author Rodney Waldhoff
 * @version $Revision: 1.3 $ $Date: 2002/10/30 19:16:21 $
 */
public class CaseTag extends TagSupport {

    public CaseTag() {
    }
    
    // Tag interface
    //------------------------------------------------------------------------- 
    public void setValue(Expression value) {
        this.valueExpression = value;
    }
    
    public void setFallThru(boolean fallThru) {
        this.fallThru = fallThru;
    }
    
    public void doTag(XMLOutput output) throws Exception {
        if(null == this.valueExpression) {
            throw new MissingAttributeException("value");
        }        
        SwitchTag tag = (SwitchTag)findAncestorWithClass(SwitchTag.class);
        if(null == tag) {
            throw new JellyException("This tag must be enclosed inside a <switch> tag" );
        }
        if(tag.hasDefaultBeenEncountered()) {
            throw new JellyException("<default> should be the last tag within a <switch>" );
        }
        Object value = valueExpression.evaluate(context);        
        if(tag.isFallingThru() || 
           (null == tag.getValue() && null == value) || 
           (null != tag.getValue() && tag.getValue().equals(value))) {
            tag.caseMatched();
            tag.setFallingThru(fallThru);
            invokeBody(output);
        }
    }
    
    // Attributes
    //------------------------------------------------------------------------- 
    private Expression valueExpression = null;
    private boolean fallThru = false;

}
