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
import java.util.Iterator;
import java.util.List;

/**
 * A sample bean that we can construct via Jelly tags
 */
public class Customer {

    private String name;
    private String location;
    private List orders = new ArrayList();

    public Customer() {
    }

    public Customer(String name) {
        setName(name);
    }

    public Customer(String name, String location) {
        setName(name);
        setLocation(location);
    }

    public Customer(String name, String location, Order anOrder) {
        setName(name);
        setLocation(location);
        addOrder(anOrder);
    }

    public Customer(Customer cust) {
        setName(cust.getName());
        setLocation(cust.getLocation());
        List list = cust.getOrders();
        if (null != list) {
            for(Iterator iter = list.iterator();iter.hasNext();) {
                addOrder((Order)iter.next());
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + "[name=" + name + ";location=" + location + "]";
    }

    /**
     * Creates a new Order object
     */
    public Order createOrder() {
        return new Order();
    }

    public List getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
    }

    /**
     * Returns the location.
     * @return String
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the name.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the location.
     * @param location The location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
