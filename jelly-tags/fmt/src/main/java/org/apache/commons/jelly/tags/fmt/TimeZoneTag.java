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

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;

/**
 * Support for tag handlers for &lt;timeZone&gt;, the time zone loading
 * tag in JSTL.
 *
 * task decide how to implement setResponseLocale
 */
public class TimeZoneTag extends TagSupport {

    /*
     * Determines and returns the time zone to be used by the given action.
     *
     * <p> If the given action is nested inside a &lt;timeZone&gt; action,
     * the time zone is taken from the enclosing &lt;timeZone&gt; action.
     *
     * <p> Otherwise, the time zone configuration setting
     * <code>javax.servlet.jsp.jstl.core.Config.FMT_TIME_ZONE</code>
     * is used.
     *
     * @param jc the page containing the action for which the
     * time zone needs to be determined
     * @param fromTag the action for which the time zone needs to be
     * determined
     *
     * @return the time zone, or {@code null} if the given action is not
     * nested inside a &lt;timeZone&gt; action and no time zone configuration
     * setting exists
     */
    static TimeZone getTimeZone(final JellyContext jc, final Tag fromTag) {
        TimeZone tz = null;

        final Tag t = findAncestorWithClass(fromTag, TimeZoneTag.class);
        if (t != null) {
            // use time zone from parent <timeZone> tag
            final TimeZoneTag parent = (TimeZoneTag) t;
            tz = parent.getTimeZone();
        } else {
            // get time zone from configuration setting
            final Object obj = jc.getVariable(Config.FMT_TIME_ZONE);
            if (obj != null) {
                if (obj instanceof TimeZone) {
                    tz = (TimeZone) obj;
                } else {
                    tz = TimeZone.getTimeZone((String) obj);
                }
            }
        }

        return tz;
    }
    private TimeZone timeZone;

    //*********************************************************************
    // Constructor and initialization

    private Expression value;                    // 'value' attribute

    //*********************************************************************
    // Collaboration with subtags

    public TimeZoneTag() {
    }

    //*********************************************************************
    // Tag logic

    /**
     * Evaluates this tag after all the tags properties have been initialized.
     *
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
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

        invokeBody(output);
    }

    //*********************************************************************
    // Package-scoped utility methods

    public TimeZone getTimeZone() {
        return timeZone;
    }

    /** Setter for property value.
     * @param value New value of property value.
     *
     */
    public void setValue(final Expression value) {
        this.value = value;
    }
}
