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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;

/**
 * Support for tag handlers for &lt;setLocale&gt;, the bundle setting
 * tag in JSTL.
 */
public class SetBundleTag extends TagSupport {

    private String var;

    private Expression basename;

    private String scope;

    /** Creates a new instance of SetBundleTag */
    public SetBundleTag() {
    }

    /**
     * Evaluates this tag after all the tags properties have been initialized.
     *
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        Object basenameInput = null;
        if (this.basename != null) {
            basenameInput = this.basename.evaluate(context);
        }

        final LocalizationContext locCtxt = BundleTag.getLocalizationContext(
            context, (String) basenameInput);

        final String varname = var != null ? var : Config.FMT_LOCALIZATION_CONTEXT;

        if (scope != null) {
            context.setVariable(varname, scope, locCtxt);
        }
        else {
            context.setVariable(varname, locCtxt);
        }
    }

    public void setBasename(final Expression basename) {
        this.basename = basename;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }

    public void setVar(final String var) {
        this.var = var;
    }
}
