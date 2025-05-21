/**
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

package org.apache.ws.jaxme.examples.misc.address.impl;

public class AddressTypeImpl implements org.apache.ws.jaxme.examples.misc.address.AddressType {

    public static class NameTypeImpl implements org.apache.ws.jaxme.examples.misc.address.AddressType.NameType {

        private String first;
        private java.util.List middle = new java.util.ArrayList();
        private String last;
        private String initials;

        @Override
        public String getFirst() {
            return first;
        }

        @Override
        public void setFirst(String first) {
            this.first = first;
        }

        @Override
        public java.util.List getMiddle() {
            return middle;
        }

        @Override
        public String getLast() {
            return last;
        }

        @Override
        public void setLast(String last) {
            this.last = last;
        }

        @Override
        public String getInitials() {
            return initials;
        }

        @Override
        public void setInitials(String initials) {
            this.initials = initials;
        }
    }

    private String id;
    private org.apache.ws.jaxme.examples.misc.address.AddressType.NameType name;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public org.apache.ws.jaxme.examples.misc.address.AddressType.NameType getName() {
        return name;
    }

    @Override
    public void setName(org.apache.ws.jaxme.examples.misc.address.AddressType.NameType name) {
        this.name = name;
    }
}
