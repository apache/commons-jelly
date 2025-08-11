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
package org.apache.commons.jelly.impl;

import org.apache.commons.jelly.TagLibrary;

/**
 * <p>{@code TagLibraryResolver} represents an object capable of
 * resolving a URI to a TagLibrary instance.</p>
 */
public interface TagLibraryResolver {

    /**
     * Attempts to resolve the given URI to be associated with a TagLibrary
     * otherwise null is returned to indicate no tag library could be found
     * so that the namespace URI should be treated as just vanilla XML.
     */
    TagLibrary resolveTagLibrary(String uri);
}