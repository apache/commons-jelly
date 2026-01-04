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

import org.apache.commons.jelly.XMLOutput;

/**
 * This calls mutex.notify() or mutex.notifyAll() on the mutex passed
 * in via the "mutex" attribute.
 */

public class NotifyTag extends UseMutexTag {

    /** True means mutex.notifyAll() will be called */
    private boolean notifyAll = false;

    /**
     * If set to true the notify will notify all waiting threads
     */
    public void setNotifyAll(final boolean notifyAll) {
        this.notifyAll = notifyAll;
    }

    /** Perform the notify */
    @Override
    public void useMutex(final Object mutex, final XMLOutput output) {
        if (notifyAll) {
            mutex.notifyAll();
        } else {
            mutex.notify();
        }
    }
}
