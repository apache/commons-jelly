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
package org.apache.commons.jelly.tags.ant;

import org.apache.commons.jelly.JellyTagException;

/**
 * A tag which provides an Ant Task object on which to set Ant DataTypes or create nested types
 */
public interface TaskSource {

    /**
     * @return the Ant object which may be an Ant Task or nested element
     */
    Object getTaskObject() throws JellyTagException;

    /**
     * Allows nested tags to set a property on the task object of this tag
     */
    void setTaskProperty(String name, Object value) throws JellyTagException;
}
