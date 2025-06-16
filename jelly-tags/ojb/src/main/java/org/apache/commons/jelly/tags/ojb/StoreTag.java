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

/**
 * <p>This Store tag will store the given object in ObjectBridge using
 * the given broker or it will use the parent broker tags broker instance.</p>
 */
public class StoreTag extends TagSupport {

    /** The value to persist */
    private Object value;

    /** The persistence broker instance */
    private PersistenceBroker broker;

    public StoreTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        if ( value == null ) {
            throw new JellyTagException( "No value is supplied!" );
        }
        getBroker().store( value );
    }

    // Properties
    //-------------------------------------------------------------------------

    /** @return the persistence broker instance */
    public PersistenceBroker getBroker() {
        if (broker == null) {
            final BrokerTag brokerTag = (BrokerTag) findAncestorWithClass( BrokerTag.class );
            if ( brokerTag != null ) {
                broker = brokerTag.getBroker();
            }
            else {
                broker = (PersistenceBroker) context.getVariable(
                    "org.apache.commons.jelly.ojb.Broker"
                );
            }
        }
        return broker;
    }

    /** Sets the persistence broker instance */
    public void setBroker(final PersistenceBroker broker) {
        this.broker = broker;
    }

    /** Sets the value to be persisted */
    public void setValue(final Object value) {
        this.value = value;
    }
}

