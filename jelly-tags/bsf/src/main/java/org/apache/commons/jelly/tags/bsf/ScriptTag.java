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
package org.apache.commons.jelly.tags.bsf;

import java.util.Iterator;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.LocationAware;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A tag which evaluates its body using the current scripting language
 */
public class ScriptTag extends TagSupport implements LocationAware {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ScriptTag.class.getName() + ".evaluating");

    private BSFEngine engine;
    private final BSFManager manager;
    private String elementName;
    private String fileName;
    private int columnNumber;
    private int lineNumber;

    public ScriptTag(final BSFEngine engine, final BSFManager manager) {
        this.engine = engine;
        this.manager = manager;
    }

    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        final String text = getBodyText();

        log.debug(text);

        // XXXX: unfortunately we must synchronize evaluations
        // so that we can swizzle in the context.
        // maybe we could create an expression from a context
        // (and so create a BSFManager for a context)
        synchronized (getRegistry()) {
            getRegistry().setJellyContext(context);

            try {
                // XXXX: hack - there must be a better way!!!
                for ( final Iterator iter = context.getVariableNames(); iter.hasNext(); ) {
                    final String name = (String) iter.next();
                    final Object value = context.getVariable( name );
                    manager.declareBean( name, value, value.getClass() );
                }
                engine.exec(fileName, lineNumber, columnNumber, text);
            }
            catch (final BSFException e) {
                throw new JellyTagException("Error occurred with script: " + e, e);
            }
        }
    }

    /**
     * @return int
     */
    @Override
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * @return String
     */
    @Override
    public String getElementName() {
        return elementName;
    }

    /**
     * @return BSFEngine
     */
    public BSFEngine getEngine() {
        return engine;
    }

    /**
     * @return String
     */
    @Override
    public String getFileName() {
        return fileName;
    }

    /**
     * @return int
     */
    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    private JellyContextRegistry getRegistry()
    {
        return (JellyContextRegistry) this.manager.getObjectRegistry();
    }

    /**
     * Sets the columnNumber.
     * @param columnNumber The columnNumber to set
     */
    @Override
    public void setColumnNumber(final int columnNumber) {
        this.columnNumber = columnNumber;
    }

    /**
     * Sets the elementName.
     * @param elementName The elementName to set
     */
    @Override
    public void setElementName(final String elementName) {
        this.elementName = elementName;
    }

    /**
     * Sets the engine.
     * @param engine The engine to set
     */
    public void setEngine(final BSFEngine engine) {
        this.engine = engine;
    }

    /**
     * Sets the fileName.
     * @param fileName The fileName to set
     */
    @Override
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * Sets the lineNumber.
     * @param lineNumber The lineNumber to set
     */
    @Override
    public void setLineNumber(final int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
