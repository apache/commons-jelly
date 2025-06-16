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
package org.apache.commons.jelly.tags.jsl;

import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.rule.Stylesheet;

/**
 * This class is a specialization of the Stylesheet from dom4j's rule engine
 * that adds some Jelly specific features.
 */
public class JellyStylesheet extends Stylesheet {

    /** The Log to which logging calls will be made. */
    private final Log log = LogFactory.getLog(JellyStylesheet.class);

    private XMLOutput output;

    public JellyStylesheet() {
        setValueOfAction(
            node -> {
                final String text = node.getStringValue();
                if ( text != null && text.length() > 0 ) {
                    getOutput().write( text );
                }
            }
        );
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Returns the output.
     * @return XMLOutput
     */
    public XMLOutput getOutput() {
        return output;
    }

    /**
     * Sets the output.
     * @param output The output to set
     */
    public void setOutput(final XMLOutput output) {
        this.output = output;
    }

}
