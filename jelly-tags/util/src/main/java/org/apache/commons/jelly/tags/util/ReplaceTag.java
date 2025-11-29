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

package org.apache.commons.jelly.tags.util;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.lang3.Strings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**
 * A tag that replaces occurrences of a character or string in its body or
 * (or value) and places the result into the context
 */
public class ReplaceTag extends TagSupport {
    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ReplaceTag.class);

    /** The variable name to export. */
    private String var;

    /** The expression to evaluate. */
    private Expression value;

    /** The old character to be replaced */
    private String oldChar;

    /** The new character that will replace the old */
    private String newChar;

    /** The old string to be replace */
    private String oldString;

    /** The new string that will replace the old */
    private String newString;

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        // check required properties
        if (oldChar != null) {
            oldString = oldChar.substring(0,1);
        }

        if (newChar != null) {
            newString = newChar.substring(0,1);
        }

        if (oldString == null) {
            throw new MissingAttributeException("oldChar or oldString must be provided");
        }

        if (newString == null) {
            throw new MissingAttributeException("newChar or newString must be provided");
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
            final String stringAnswer = Strings.CS.replace(answer.toString(), oldString, newString);
            if ( var != null ) {
                context.setVariable(var, stringAnswer);
            } else {
                try {
                    output.write(stringAnswer);
                } catch (final SAXException e) {
                    throw new JellyTagException(e);
                }
            }
        }
    }

    /**
     * Returns the newString that will be replaced.
     * @return String
     */
    public String getNew()
    {
        return newString;
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
     * Returns the oldString that will be replaced.
     * @return String
     */
    public String getOld()
    {
        return oldString;
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
     * Sets the newString.
     * @param newString The newString to set
     */
    public void setNew(final String newString)
    {
        this.newString = newString;
    }

    /**
     * Sets the newChar.
     * @param newChar The newChar to set
     */
    public void setNewChar(final String newChar)
    {
        this.newChar = newChar;
    }

    /**
     * Sets the oldString.
     * @param oldString The oldString to set
     */
    public void setOld(final String oldString)
    {
        this.oldString = oldString;
    }

    /**
     * Sets the oldChar.
     * @param oldChar The oldChar to set
     */
    public void setOldChar(final String oldChar)
    {
        this.oldChar = oldChar;
    }

    /**
     * Sets the value.
     * @param value The value to set
     */
    public void setValue(final Expression value)
    {
        this.value = value;
    }

    /**
     * Sets the var.
     * @param var The var to set
     */
    public void setVar(final String var)
    {
        this.var = var;
    }

}
