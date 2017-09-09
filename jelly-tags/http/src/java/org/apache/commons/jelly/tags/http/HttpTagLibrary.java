/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jelly.tags.http;

import java.util.Map;

import org.apache.commons.jelly.TagLibrary;

/**
 * The set of jelly tags provided by Latka
 *
 * @author dion
 * @version $Id$
 */
public class HttpTagLibrary extends TagLibrary {

    /**
     * Creates a new instance of LatkaTagLibrary
     */
    public HttpTagLibrary() {
        registerTag("body", BodyTag.class);
        registerTag("delete", DeleteTag.class);
        registerTag("get", GetTag.class);
        registerTag("head", HeadTag.class);
        registerTag("header", HeaderTag.class);
        registerTag("mppost", MultipartPostTag.class);
        registerTag("options", OptionsTag.class);
        registerTag("parameter", ParameterTag.class);
        registerTag("part", PartTag.class);
        registerTag("post", PostTag.class);
        registerTag("put", PutTag.class);
        registerTag("session", SessionTag.class);
    }

    /**
     * @see TagLibarary#getTagClasses()
     *
     * @return a Map of tag name to tag class
     */
    public Map getTagClasses() {
        return super.getTagClasses();
    }

}

