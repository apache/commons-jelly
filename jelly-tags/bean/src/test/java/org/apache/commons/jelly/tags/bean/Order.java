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
public class Order {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(Order.class);

    private Product product;
    private int amount;
    private double price;

    public Order() {
    }

    @Override
    public String toString() {
        return "Order[amount=" + amount + ";price=" + price + ";product=" + product + "]";
    }

    /**
     * Factory method to create a new Product
    public Product createProduct() {
        return new Product();
    }
     */

    public void addProduct(final Product product)
    {
        this.product = product;
    }

    /**
     * Returns the amount.
     * @return int
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Returns the price.
     * @return double
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the amount.
     * @param amount The amount to set
     */
    public void setAmount(final int amount) {
        this.amount = amount;
    }

    /**
     * Sets the price.
     * @param price The price to set
     */
    public void setPrice(final double price) {
        this.price = price;
    }

    /**
     * Returns the product.
     * @return Product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the product.
     * @param product The product to set
     */
    public void setProduct(final Product product) {
        this.product = product;
    }

}
