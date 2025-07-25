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
import org.apache.commons.jelly.XMLOutput;

/**
 * Synchronize a block inside of a thread using the passed in mutex. The
 * mutex object passed in does not have to have been created using the
 * mutex tag, it can be any object at all.
 */

public class SynchronizeTag extends UseMutexTag {
    /** Synchronize on the mutex */
    @Override
    protected void useMutex(final Object mutex, final XMLOutput output) throws JellyTagException {
        synchronized (mutex) {
            invokeBody(output);
        }
    }
}

