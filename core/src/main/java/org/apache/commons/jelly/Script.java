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
 * <p>{@code Script} represents a Jelly script.
 * A Script <strong>must</strong> be thread safe so care should be taken on the
 * implementations of Scripts. However Tags are only used in a single thread
 * (each thread will have create its own Tags for the Script it is running)
 * so multi threading is not a concern for Tag developers.</p>
 */
public interface Script {

    /** Called by the parser to allow a more efficient
     * representation of the script to be used.
     */
    Script compile() throws JellyException;

    /** Evaluates the body of a tag */
    void run(JellyContext context, XMLOutput output) throws JellyTagException;

}
