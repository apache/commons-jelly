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
package org.apache.commons.jelly.tags.interaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jline.ConsoleReader;
import jline.History;
import jline.SimpleCompletor;

  /**
  * Jelly Tag that asks the user a question, and puts his answer into a variable,
  * with the attribute "answer". This variable may be reused further as any other
  * Jelly variable.
  *
   */
public class AskTag extends TagSupport {

    private static Log logger = LogFactory.getLog(AskTag.class);

    /** The history of previous user-inputs.*/
    private static History consoleHistory = new History();

    /** The question to ask to the user. */
    private String question;

    /**
     * The variable in which we will stock the user's input.
     * This defaults to "interact.answer".
     */
    private String answer = "interact.answer";

    /** The default value, if the user doesn't answer. */
    private String defaultInput;

    /** The prompt to display before the user input. */
    private String prompt = ">";

    /** A list of predefined commands for tab completion. */
    private List completor;

    /** Whether to complete with previous completions as well. */
    private boolean useHistoryCompletor = true;

    /**
     * Perform functionality provided by the tag.
     *
     * @param output
     *            the place to write output
     */
    @Override
    public void doTag(final XMLOutput output) {
        if (question != null) {
            if (defaultInput != null) {
                System.out.println(question + " [" + defaultInput + "]");
            } else {
                System.out.println(question);
            }
            // The prompt should be just before the user's input,
            // but it doesn't work ...
            //System.out.print(prompt + " ");
        }

        ConsoleReader consoleReader;
        String input = null;

        try {
            consoleReader = new ConsoleReader();
        } catch (final IOException e) {
            logger.warn("couldnt create console reader", e);
            consoleReader = null;
        }

        try {
            if (consoleReader != null
                    && consoleReader.getTerminal().isSupported()) {

                // resue the static history, so our commands are remembered
                consoleReader.setHistory(consoleHistory);

                // hate the bell!
                consoleReader.setBellEnabled(false);

                // add old commands as tab completion history
                final List oldCommandsAsList = useHistoryCompletor
                    ? new ArrayList(consoleHistory.getHistoryList()) : new ArrayList(0);
                // add predefined commands if given
                if (completor != null && !completor.isEmpty()) {
                    oldCommandsAsList.addAll(completor);
                }
                final String[] oldCommands = new String[oldCommandsAsList.size()];
                oldCommandsAsList.toArray(oldCommands);
                consoleReader.addCompletor (new SimpleCompletor (oldCommands));

                // read the input!
                input = consoleReader.readLine();

                // trim the input for tab completion
                input = input.trim();

                if (defaultInput != null && input.trim().isEmpty()) {
                    input = defaultInput;
                }
            } else {
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(System.in));
                input = reader.readLine();
            }

        } catch (final IOException ex) {
            logger.warn("error setting up the console reader", ex);
        }

        context.setVariable(answer, input);
    }

    /**
     * Sets the name of the variable that will hold the answer.
     * This defaults to "interact.answer".
     *
     * @param answer
     *            the name of the variable that will hold the answer
     */
    public void setAnswer(final String answer) {
        this.answer = answer;
    }

    /**
     * Sets the list of predefined commands.
     *
     * @param list
     *            the list of commands used for tab completion.
     */
    public void setCompletor(final List list) {
        this.completor = list;
    }

    /**
     * Sets the default answer to the question. If it is present, it will appear
     * inside [].
     *
     * @param defaultInput
     *            the default answer to the question
     */
    public void setDefault(final String defaultInput) {
        this.defaultInput = defaultInput;
    }

    /**
     * Sets the prompt that will be displayed before the user's input.
     *
     * @param prompt
     *            the prompt that will be displayed before the user's input.
     */
    public void setPrompt(final String prompt) {
        this.prompt = prompt;
    }

    /**
     * Sets the question to ask to the user. If a "default" attribute is
     * present, it will appear inside [].
     *
     * @param question
     *            The question to ask to the user
     */
    public void setQuestion(final String question) {
        this.question = question;
    }

    /**
     * Sets whether the completion should also happen on previously
     * entered lines (default true).
     *
     * @param should whether it should
     */
    public void setUseHistoryCompletor(final boolean should) {
        this.useHistoryCompletor = should;
    }
}
