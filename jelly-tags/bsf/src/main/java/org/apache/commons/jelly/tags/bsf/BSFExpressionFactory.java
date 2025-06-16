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

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.expression.ExpressionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Represents a factory of BSF expressions
  */
public class BSFExpressionFactory implements ExpressionFactory {

    /** The logger of messages */
    private final Log log = LogFactory.getLog( getClass() );

    private String language = "javascript";
    private BSFManager manager;
    private BSFEngine engine;
    private final JellyContextRegistry registry = new JellyContextRegistry();

    public BSFExpressionFactory() {
    }

    // Properties
    //-------------------------------------------------------------------------

    /** Factory method */
    protected BSFEngine createBSFEngine() throws BSFException {
        return getBSFManager().loadScriptingEngine( getLanguage() );
    }

    /** Factory method */
    protected BSFManager createBSFManager() {
        final BSFManager answer = new BSFManager();
        return answer;
    }

    // ExpressionFactory interface
    //-------------------------------------------------------------------------
    @Override
    public Expression createExpression(final String text) throws JellyException {
        try {
            return new BSFExpression( text, getBSFEngine(), getBSFManager(), registry );
        } catch (final BSFException e) {
            throw new JellyException("Could not obtain BSF engine",e);
        }
    }

    /** @return the BSF Engine to be used by this expression factory */
    public BSFEngine getBSFEngine() throws BSFException {
        if ( engine == null ) {
            engine = createBSFEngine();
        }
        return engine;
    }

    public BSFManager getBSFManager() {
        if ( manager == null ) {
            manager = createBSFManager();
            manager.setObjectRegistry( registry );
        }
        return manager;
    }

    /** @return the BSF language to be used */
    public String getLanguage() {
        return language;
    }

    public void setBSFEngine(final BSFEngine engine) {
        this.engine = engine;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    public void setBSFManager(final BSFManager manager) {
        this.manager = manager;
        manager.setObjectRegistry( registry );
    }

    public void setLanguage(final String language) {
        this.language = language;
    }
}
