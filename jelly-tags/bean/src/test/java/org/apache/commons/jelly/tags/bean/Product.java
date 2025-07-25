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
package org.apache.commons.jelly.tags.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A sample bean that we can construct via Jelly tags
 */
public class Product {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(Product.class);

    private String id;
    private String name;

    public Product() {
    }

    // Properties
    //-------------------------------------------------------------------------
    /**
     * Returns the id.
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the id.
     * @param id The id to set
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Product[id=" + id + ";name=" + name + "]";
    }

}
