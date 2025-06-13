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

public class AddressTypeSerializer extends org.apache.ws.jaxme.impl.JMXmlSerializerImpl {
  public static class NameTypeSerializer extends org.apache.ws.jaxme.impl.JMXmlSerializerImpl {
    final static javax.xml.namespace.QName __ser_First_qname = new javax.xml.namespace.QName("http://ws.apache.org/jaxme/examples/misc/address", "First");

    final static javax.xml.namespace.QName __ser_Middle_qname = new javax.xml.namespace.QName("http://ws.apache.org/jaxme/examples/misc/address", "Middle");

    final static javax.xml.namespace.QName __ser_Last_qname = new javax.xml.namespace.QName("http://ws.apache.org/jaxme/examples/misc/address", "Last");

    final static javax.xml.namespace.QName __ser_Initials_qname = new javax.xml.namespace.QName("http://ws.apache.org/jaxme/examples/misc/address", "Initials");


    @Override
    protected void marshalChilds(final org.apache.ws.jaxme.JMXmlSerializer.Data data, final java.lang.Object object) throws org.xml.sax.SAXException {
      final org.apache.ws.jaxme.examples.misc.address.AddressType.NameType _1 = (org.apache.ws.jaxme.examples.misc.address.AddressType.NameType) object;
      final java.lang.String _2 = _1.getFirst();
      if (_2 != null) {
        marshalAtomicChild(data, __ser_First_qname, _1.getFirst());
      }
      final java.util.List _3 = _1.getMiddle();
      for (int _4 = 0;  _4 < _3.size();  _4++) {
        final java.lang.String _5 = (java.lang.String)_3.get(_4);
        if (_5 != null) {
          marshalAtomicChild(data, __ser_Middle_qname, (java.lang.String)_3.get(_4));
        }
      }
      final java.lang.String _6 = _1.getLast();
      if (_6 != null) {
        marshalAtomicChild(data, __ser_Last_qname, _1.getLast());
      }
      final java.lang.String _7 = _1.getInitials();
      if (_7 != null) {
        marshalAtomicChild(data, __ser_Initials_qname, _1.getInitials());
      }
    }

  }

  final static javax.xml.namespace.QName __ser_Name_qname = new javax.xml.namespace.QName("http://ws.apache.org/jaxme/examples/misc/address", "Name");

  private org.apache.ws.jaxme.examples.misc.address.impl.AddressTypeSerializer.NameTypeSerializer serName;

  @Override
protected org.xml.sax.helpers.AttributesImpl getAttributes(final org.apache.ws.jaxme.JMXmlSerializer.Data data, final java.lang.Object rlement) throws org.xml.sax.SAXException {
    final org.xml.sax.helpers.AttributesImpl _1 = super.getAttributes(data, rlement);
    final org.apache.ws.jaxme.examples.misc.address.AddressType _2 = (org.apache.ws.jaxme.examples.misc.address.AddressType) rlement;
    final java.lang.String _3 = _2.getId();
    if (_3 != null) {
      _1.addAttribute("", "id", getAttributeQName(data, "", "id"), "CDATA", _2.getId());
    }
    return _1;
  }

  @Override
public void init(final org.apache.ws.jaxme.impl.JAXBContextImpl factory) throws javax.xml.bind.JAXBException {
    super.init(factory);
    serName = new org.apache.ws.jaxme.examples.misc.address.impl.AddressTypeSerializer.NameTypeSerializer();
    serName.init(factory);
  }

  @Override
protected void marshalChilds(final org.apache.ws.jaxme.JMXmlSerializer.Data data, final Object object) throws org.xml.sax.SAXException {
    final org.apache.ws.jaxme.examples.misc.address.AddressType _1 = (org.apache.ws.jaxme.examples.misc.address.AddressType) object;
    final org.apache.ws.jaxme.examples.misc.address.AddressType.NameType _2 = _1.getName();
    if (_2 != null) {
      serName.marshal(data, __ser_Name_qname, _1.getName());
    }
  }

}
