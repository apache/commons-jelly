/**
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
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
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
 * $Id: AskTag.java,v 1.2 2003/10/09 21:21:19 rdonkin Exp $
 */
package org.apache.commons.jelly.tags.interaction;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * Jelly Tag that asks the user a question, and puts his answer into
 * a variable, with the attribute "answer".
 * This variable may be reused further as any other Jelly variable.
 * @author <a href="mailto:smor@hasgard.net">Stéphane Mor</a>
 */
public class AskTag extends TagSupport
{
    /** The question to ask to the user */
    private String question;

    /** 
     * The variable in which we will stock the user's input.
     * This defaults to "interact.answer".
     */
    private String answer = "interact.answer";

    /** The default value, if the user doesn't answer */
    private String defaultInput;

    /** The user's input */
    private String input = "";

    /** The prompt to display before the user input */
    private String prompt = ">";

    /**
     * Sets the question to ask to the user. If a "default" attribute
     * is present, it will appear inside [].
     * @param question The question to ask to the user
     */
    public void setQuestion(String question)
    {
        this.question = question;
    }

    /**
     * Sets the name of the variable that will hold the answer
     * This defaults to "interact.answer".
     * @param answer the name of the variable that will hold the answer
     */
    public void setAnswer(String answer)
    {
        this.answer = answer;
    }

    /**
     * Sets the default answer to the question.
     * If it is present, it will appear inside [].
     * @param default the default answer to the question
     */
    public void setDefault(String defaultInput)
    {
        this.defaultInput = defaultInput;
    }

    /**
     * Sets the prompt that will be displayed before the user's input.
     * @param promt the prompt that will be displayed before the user's input.
     */
    public void setPrompt(String prompt)
    {
        this.prompt = prompt;
    }


    /**
     * Perform functionality provided by the tag
     * @param output the place to write output
     */
    public void doTag(XMLOutput output) 
    {
        if (question != null)
        {
            if (defaultInput != null)
            {
                System.out.println(question + " [" + defaultInput + "]");
            }
            else
            {
                System.out.println(question);
            }
            // The prompt should be just before the user's input, 
            // but it doesn't work ...
            //System.out.print(prompt + " ");
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            input = br.readLine();
            if (defaultInput != null && input.trim().equals(""))
            {
                input = defaultInput;
            }
        } catch (IOException ioe) {
        }
        context.setVariable(answer, input);
    }
}
