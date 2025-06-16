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

package org.apache.commons.jelly.tags.threads;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * Base class for tags that will "use" mutexes.
 */

public abstract class UseMutexTag extends TagSupport {
    /** The mutex to use in some way. */
    private Object mutex = null;

    /** Calls useMutex after checking to make sure that <em>setMutex</em> was called */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        // either use the set thread or search for a parent thread to use
        if (mutex == null) {
            throw new JellyTagException("no mutex set");
        }

        useMutex(mutex, output);
    }

    /** Gets the mutex */
    public Object getMutex() {
        return mutex;
    }

    /** Sets the mutex. Any object can be used as a mutex. */
    public void setMutex(final Object mutex) {
        this.mutex = mutex;
    }

    /** Implement this method to do something with the mutex */
    protected abstract void useMutex(Object mutex, XMLOutput output) throws JellyTagException;
}
