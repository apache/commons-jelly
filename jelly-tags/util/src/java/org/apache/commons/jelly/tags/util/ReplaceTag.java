/*
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
 */

package org.apache.commons.jelly.tags.util;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.SAXException;

/**
 * A tag that replaces occurrences of a character in its body (or value)
 * and places the result into the context
 * 
 * @author dion
 */
public class ReplaceTag extends TagSupport {
    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ReplaceTag.class);
    
    /** The variable name to export. */
    private String var;

    /** The expression to evaluate. */
    private Expression value;

    /** the old character to be replaced */
    private String oldChar;
    
    /** the new character that will replace the old */
    private String newChar;
    
    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        // check required properties
        if (oldChar == null) {
            throw new MissingAttributeException("oldChar must be provided");
        }
        if (newChar == null) {
            throw new MissingAttributeException("newChar must be provided");
        }
        
        // get either the value or the body of the tag
        Object answer = null;
        if ( value != null ) {
            answer = value.evaluateAsString(context);
        } else {
            answer = getBodyText(false);
        }
        
        // set the result in the context, or output it
        if (answer != null) {
            String stringAnswer = answer.toString().replace(oldChar.charAt(0),
                newChar.charAt(0));
            if ( var != null ) {
                context.setVariable(var, stringAnswer);
            } else {
                try {
                    output.write(stringAnswer);
                } catch (SAXException e) {
                    throw new JellyTagException(e);
                }
            }
        }
    }
    
    /**
     * Returns the newChar used in replacing. Should only be a single
     * character.
     * @return String
     */
    public String getNewChar()
    {
        return newChar;
    }

    /**
     * Returns the oldChar that will be replaced. Should only be a single
     * character.
     * @return String
     */
    public String getOldChar()
    {
        return oldChar;
    }

    /**
     * Returns the value.
     * @return Expression
     */
    public Expression getValue()
    {
        return value;
    }

    /**
     * Returns the var.
     * @return String
     */
    public String getVar()
    {
        return var;
    }

    /**
     * Sets the newChar.
     * @param newChar The newChar to set
     */
    public void setNewChar(String newChar)
    {
        this.newChar = newChar;
    }

    /**
     * Sets the oldChar.
     * @param oldChar The oldChar to set
     */
    public void setOldChar(String oldChar)
    {
        this.oldChar = oldChar;
    }

    /**
     * Sets the value.
     * @param value The value to set
     */
    public void setValue(Expression value)
    {
        this.value = value;
    }

    /**
     * Sets the var.
     * @param var The var to set
     */
    public void setVar(String var)
    {
        this.var = var;
    }

}