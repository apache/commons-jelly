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
package org.apache.commons.jelly.tags.jmx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A dummy MBean used for the demo
 */
public class Dummy implements DummyMBean {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(Dummy.class);

    private String name = "James";
    private int count;

    @Override
    public void doSomething() {
        ++count;
        log.info("Do something! on: " + this);
    }

    /**
     * @return int
     */
    @Override
    public int getCount() {
        return count;
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return String
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the count.
     * @param count The count to set
     */
    @Override
    public void setCount(final int count) {
        this.count = count;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + "[name=" + name + "]";
    }

}
