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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A sample bean that we can construct via Jelly tags
 */
public class Manager {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(Manager.class);

    private final List customers = new ArrayList();

    boolean invoked = false;

    public Manager() {
    }

    public void addCustomer(final Customer customer) {
        customers.add(customer);
    }

    public List getCustomers() {
        return customers;
    }

    /**
     * @return boolean
     */
    public boolean isInvoked() {
        return invoked;
    }

    public void removeCustomer(final Customer customer) {
        customers.remove(customer);
    }

    /**
     * The invoke method which is called when the bean is constructed
     */
    public void run() {
        invoked = true;

        log.info("Invoked the run() method with customers: " + customers);
    }

    /**
     * Sets the invoked.
     * @param invoked The invoked to set
     */
    public void setInvoked(final boolean invoked) {
        this.invoked = invoked;
    }

    @Override
    public String toString() {
        return super.toString() + "[customers=" + customers + "]";
    }

}
