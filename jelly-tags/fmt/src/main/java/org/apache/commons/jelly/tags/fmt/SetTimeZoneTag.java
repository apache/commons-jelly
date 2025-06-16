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
package org.apache.commons.jelly.tags.fmt;

import java.util.TimeZone;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;

/**
 * Support for tag handlers for &lt;setTimeZone&gt;, the time zone setting
 * tag in JSTL.
 */
public class SetTimeZoneTag extends TagSupport {

    private Expression value;

    private String var;

    private String scope;

    /** Creates a new instance of SetLocaleTag */
    public SetTimeZoneTag() {
    }

    /**
     * Evaluates this tag after all the tags properties have been initialized.
     *
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        TimeZone timeZone = null;

        Object valueInput = null;
        if (this.value != null) {
            valueInput = this.value.evaluate(context);
        }

        if (valueInput == null) {
            timeZone = TimeZone.getTimeZone("GMT");
        }
        else if (valueInput instanceof String) {
            if (((String) valueInput).trim().isEmpty()) {
                timeZone = TimeZone.getTimeZone("GMT");
            } else {
                timeZone = TimeZone.getTimeZone((String) valueInput);
            }
        } else {
            timeZone = (TimeZone) valueInput;
        }

        if (scope != null) {
            context.setVariable(Config.FMT_TIME_ZONE, scope, timeZone);
        }
        else {
            context.setVariable(Config.FMT_TIME_ZONE, timeZone);
        }
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }

    public void setValue(final Expression value) {
        this.value = value;
    }

    public void setVar(final String var) {
        this.var = var;
    }
}
