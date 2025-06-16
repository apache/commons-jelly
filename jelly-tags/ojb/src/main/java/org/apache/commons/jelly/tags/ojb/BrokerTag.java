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
package org.apache.commons.jelly.tags.ojb;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerFactory;

/**
 * <p>Tag handler for &lt;Driver&gt; in JSTL, used to create
 * a simple DataSource for prototyping.</p>
 */
public class BrokerTag extends TagSupport {

    /** The variable name to export. */
    private String var;

    /** The persistence broker instance */
    private PersistenceBroker broker;

    public BrokerTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        if ( var == null ) {
            var = "org.apache.commons.jelly.ojb.Broker";
        }
        if ( broker != null ) {
            context.setVariable(var, broker);
            invokeBody(output);
        }
        else {
            broker = PersistenceBrokerFactory.defaultPersistenceBroker();
            context.setVariable(var, broker);

            try {
                invokeBody(output);
            }
            finally {
                broker.close();
                broker = null;
                context.removeVariable(var);
            }
        }
    }

    /** @return the persistence broker instance */
    public PersistenceBroker getBroker() {
        return broker;
    }

    /** Sets the persistence broker instance */
    public void setBroker(final PersistenceBroker broker) {
        this.broker = broker;
    }

    // Properties
    //-------------------------------------------------------------------------
    /** Sets the variable name to define for this expression
     */
    public void setVar(final String var) {
        this.var = var;
    }
}
