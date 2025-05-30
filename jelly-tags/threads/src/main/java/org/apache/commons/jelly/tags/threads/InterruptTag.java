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

import java.util.List;

/**
 * Interrupts a thread or thread group.
 */

public class InterruptTag extends UseThreadTag {
    /** Interrupt one thread */
    protected void useThread(Thread thread, XMLOutput output) {
        thread.interrupt();
    }

    /** Interrupt all of the threads in a group */
    protected void useThreadGroup(List threadGroup, XMLOutput output) {
        for (int i = 0; i < threadGroup.size(); i++) {
            ((Thread) threadGroup.get(i)).interrupt();
        }
    }
}
