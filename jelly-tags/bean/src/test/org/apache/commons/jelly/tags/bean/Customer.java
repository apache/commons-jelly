/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/bean/src/test/org/apache/commons/jelly/tags/bean/Customer.java,v 1.1 2003/01/14 04:01:01 dion Exp $
 * $Revision: 1.1 $
 * $Date: 2003/01/14 04:01:01 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 * 
 * $Id: Customer.java,v 1.1 2003/01/14 04:01:01 dion Exp $
 */
package org.apache.commons.jelly.tags.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * A sample bean that we can construct via Jelly tags
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public class Customer {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(Customer.class);

    private String name;
    private String city;
    private String location;
    private List orders = new ArrayList();
    
        
    public Customer() {
    }
    
    public Customer(String name) {
        setName(name);
    }
    
    public Customer(String name, String city) {
        setName(name);
        setCity(city);
    }
    
    public Customer(String name, String city, Order anOrder) {
        setName(name);
        setCity(city);
        addOrder(anOrder);
    }
    
    public Customer(Customer cust) {
        setName(cust.getName());
        setCity(cust.getCity());
        setLocation(cust.getLocation());
        List list = cust.getOrders();
        if(null != list) {
            for(Iterator iter = list.iterator();iter.hasNext();) {
                addOrder((Order)iter.next());
            }
        }
    }
    
    public String toString() {
        return super.toString() + "[name=" + name + ";city=" + city + "]";
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
     * Returns the city.
     * @return String
     */
    public String getCity() {
        return city;
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
     * Sets the city.
     * @param city The city to set
     */
    public void setCity(String city) {
        this.city = city;
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
