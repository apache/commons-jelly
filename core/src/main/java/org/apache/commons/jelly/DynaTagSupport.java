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

package org.apache.commons.jelly;

/**
 * <p><code>DynaTagSupport</code> is an abstract base class
 * for any DynaTag implementation to derive from.
 * </p>
 */

public abstract class DynaTagSupport extends TagSupport implements DynaTag {

    /**
     * @return the type of the given attribute. By default just return
     * Object.class if this is not known.
     */
    @Override
    public Class getAttributeType(final String name) throws JellyTagException {
        return Object.class;
    }
}
