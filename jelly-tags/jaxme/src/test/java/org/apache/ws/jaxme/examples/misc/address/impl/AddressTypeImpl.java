/**
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
package org.apache.ws.jaxme.examples.misc.address.impl;

public class AddressTypeImpl implements org.apache.ws.jaxme.examples.misc.address.AddressType {
  public static class NameTypeImpl implements org.apache.ws.jaxme.examples.misc.address.AddressType.NameType {
    private java.lang.String First;
  
    private java.util.List Middle = new java.util.ArrayList();
  
    private java.lang.String Last;
  
    private java.lang.String Initials;
  
  
    @Override
    public java.lang.String getFirst() {
      return First;
    }
  
    @Override
    public void setFirst(java.lang.String pFirst) {
      First = pFirst;
    }
  
    @Override
    public java.util.List getMiddle() {
      return Middle;
    }
  
    @Override
    public java.lang.String getLast() {
      return Last;
    }
  
    @Override
    public void setLast(java.lang.String pLast) {
      Last = pLast;
    }
  
    @Override
    public java.lang.String getInitials() {
      return Initials;
    }
  
    @Override
    public void setInitials(java.lang.String pInitials) {
      Initials = pInitials;
    }
  
  }

  private java.lang.String Id;

  private org.apache.ws.jaxme.examples.misc.address.AddressType.NameType Name;

  @Override
public java.lang.String getId() {
    return Id;
  }

  @Override
public void setId(java.lang.String pId) {
    Id = pId;
  }

  @Override
public org.apache.ws.jaxme.examples.misc.address.AddressType.NameType getName() {
    return Name;
  }

  @Override
public void setName(org.apache.ws.jaxme.examples.misc.address.AddressType.NameType pName) {
    Name = pName;
  }

}
