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

public class AddressTypeHandler extends org.apache.ws.jaxme.impl.JMHandlerImpl {
  public static class NameTypeHandler extends org.apache.ws.jaxme.impl.JMHandlerImpl {
    /** The current level of nested elements. 0, if outside the root element.
     *
     */
    private int level;

    /** The current state. The following values are valid states:
     *  0 = Before parsing the element
     *  1 = Parsing an unknown element
     *  2 = After parsing the element
     *  3 = While parsing the child element {http://ws.apache.org/jaxme/examples/misc/address}First
     *  4 = While parsing the child element {http://ws.apache.org/jaxme/examples/misc/address}Middle
     *  5 = While parsing the child element {http://ws.apache.org/jaxme/examples/misc/address}Last
     *  6 = While parsing the child element {http://ws.apache.org/jaxme/examples/misc/address}Initials
     *
     */
    private int state;

    /** The current handler for parsing child elements or simple content.
     *
     */
    private org.apache.ws.jaxme.JMHandler handler;


    @Override
    public void characters(final char[] chars, final int offset, final int len) throws org.xml.sax.SAXException {
      if (handler == null) {
        super.characters(chars, offset, len);
      } else {
        handler.characters(chars, offset, len);
      }
    }

    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws org.xml.sax.SAXException {
      if (handler == null) {
        if (level > 1) {
          super.endElement(namespaceURI, localName, qName);
        }
      } else {
        handler.endElement(namespaceURI, localName, qName);
      }
      switch (--level) {
        case 0:
          break;
        case 1:
          final org.apache.ws.jaxme.examples.misc.address.AddressType.NameType _1 = (org.apache.ws.jaxme.examples.misc.address.AddressType.NameType) getResult();
          switch (state) {
            case 3:
              if (handler != null) {
                handler.endDocument();
              }
              _1.setFirst((java.lang.String) handler.getResult());
              break;
            case 4:
              if (handler != null) {
                handler.endDocument();
              }
              _1.getMiddle().add(handler.getResult());
              break;
            case 5:
              if (handler != null) {
                handler.endDocument();
              }
              _1.setLast((java.lang.String) handler.getResult());
              break;
            case 6:
              if (handler != null) {
                handler.endDocument();
              }
              _1.setInitials((java.lang.String) handler.getResult());
              break;
            default:
              throw new java.lang.IllegalStateException("Illegal state: " + state);
          }
      }
    }

    protected org.apache.ws.jaxme.examples.misc.address.AddressType.NameType newResult() throws org.xml.sax.SAXException {
      return new org.apache.ws.jaxme.examples.misc.address.impl.AddressTypeImpl.NameTypeImpl();
    }

    @Override
    public void startDocument() throws org.xml.sax.SAXException {
      level = 0;
      state = 0;
      handler = null;
    }

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final org.xml.sax.Attributes attr) throws org.xml.sax.SAXException {
      switch (level++) {
        case 0:
          setResult(newResult());
          if (attr != null) {
            for (int _1 = 0;  _1 < attr.getLength();  _1++) {
              super.addAttribute(attr.getURI(_1), attr.getLocalName(_1), attr.getValue(_1));
            }
          }
          break;
        case 1:
          if ("http://ws.apache.org/jaxme/examples/misc/address".equals(namespaceURI) && (localName != null)) {
            switch (localName) {
            case "First":
                switch (state) {
                    case 0:
                      state = 3;
                      handler = getData().getAtomicHandler();
                      handler.startDocument();
                      handler.startElement(namespaceURI, localName, qName, attr);
                      break;
                    default:
                      validationEvent(javax.xml.bind.ValidationEvent.WARNING, "The element " + qName + " was unexpected at this place.", org.apache.ws.jaxme.ValidationEvents.EVENT_UNEXPECTED_CHILD_STATE);
                      break;
                  }
                break;
            case "Middle":
                switch (state) {
                    case 3:
                    case 4:
                      state = 4;
                      handler = getData().getAtomicHandler();
                      handler.startDocument();
                      handler.startElement(namespaceURI, localName, qName, attr);
                      break;
                    default:
                      validationEvent(javax.xml.bind.ValidationEvent.WARNING, "The element " + qName + " was unexpected at this place.", org.apache.ws.jaxme.ValidationEvents.EVENT_UNEXPECTED_CHILD_STATE);
                      break;
                  }
                break;
            case "Last":
                switch (state) {
                    case 3:
                    case 4:
                      state = 5;
                      handler = getData().getAtomicHandler();
                      handler.startDocument();
                      handler.startElement(namespaceURI, localName, qName, attr);
                      break;
                    default:
                      validationEvent(javax.xml.bind.ValidationEvent.WARNING, "The element " + qName + " was unexpected at this place.", org.apache.ws.jaxme.ValidationEvents.EVENT_UNEXPECTED_CHILD_STATE);
                      break;
                  }
                break;
            case "Initials":
                switch (state) {
                    case 5:
                      state = 6;
                      handler = getData().getAtomicHandler();
                      handler.startDocument();
                      handler.startElement(namespaceURI, localName, qName, attr);
                      break;
                    default:
                      validationEvent(javax.xml.bind.ValidationEvent.WARNING, "The element " + qName + " was unexpected at this place.", org.apache.ws.jaxme.ValidationEvents.EVENT_UNEXPECTED_CHILD_STATE);
                      break;
                  }
                break;
            default:
                break;
            }
        }
          break;
        default:
          if (handler == null) {
            super.startElement(namespaceURI, localName, qName, attr);
          } else {
            handler.startElement(namespaceURI, localName, qName, attr);
          }
      }
    }

  }

  /** The current level of nested elements. 0, if outside the root element.
   *
   */
  private int level;

  /** The current state. The following values are valid states:
   *  0 = Before parsing the element
   *  1 = Parsing an unknown element
   *  2 = After parsing the element
   *  3 = While parsing the child element {http://ws.apache.org/jaxme/examples/misc/address}Name
   *
   */
  private int state;

  /** The current handler for parsing child elements or simple content.
   *
   */
  private org.apache.ws.jaxme.JMHandler handler;

  private org.apache.ws.jaxme.JMHandler handlerName;

  @Override
public void addAttribute(String uri, final String localName, final String value) throws org.xml.sax.SAXException {
    if (uri == null) {
      uri = "";
    }
    final org.apache.ws.jaxme.examples.misc.address.AddressType _1 = (org.apache.ws.jaxme.examples.misc.address.AddressType) getResult();
    if ("".equals(uri) && "id".equals(localName)) {
        _1.setId(value);
        return;
      }
    super.addAttribute(uri, localName, value);
  }

  @Override
public void characters(final char[] buffer, final int offset, final int length) throws org.xml.sax.SAXException {
    if (handler == null) {
      super.characters(buffer, offset, length);
    } else {
      handler.characters(buffer, offset, length);
    }
  }

  @Override
public void endElement(final String namespaceURI, final String localName, final String qName) throws org.xml.sax.SAXException {
    if (handler == null) {
      if (level > 1) {
        super.endElement(namespaceURI, localName, qName);
      }
    } else {
      handler.endElement(namespaceURI, localName, qName);
    }
    switch (--level) {
      case 0:
        break;
      case 1:
        final org.apache.ws.jaxme.examples.misc.address.AddressType _1 = (org.apache.ws.jaxme.examples.misc.address.AddressType) getResult();
        switch (state) {
          case 3:
            if (handler != null) {
              handler.endDocument();
            }
            _1.setName((org.apache.ws.jaxme.examples.misc.address.AddressType.NameType) handler.getResult());
            break;
          default:
            throw new java.lang.IllegalStateException("Illegal state: " + state);
        }
    }
  }

  protected org.apache.ws.jaxme.JMHandler getHandlerForName() throws org.xml.sax.SAXException {
    if (handlerName == null) {
      try {
        handlerName = new org.apache.ws.jaxme.examples.misc.address.impl.AddressTypeHandler.NameTypeHandler();
        handlerName.init(getData());
      } catch (final javax.xml.bind.JAXBException _1) {
        throw new org.xml.sax.SAXException(_1);
      }
    }
    return handlerName;
  }

  @Override
public void init(final org.apache.ws.jaxme.JMHandler.Data data) throws javax.xml.bind.JAXBException {
    super.init(data);
    if (handlerName != null) {
      handlerName.init(data);
    }
  }

  protected org.apache.ws.jaxme.examples.misc.address.AddressType newResult() throws org.xml.sax.SAXException {
    try {
      return (org.apache.ws.jaxme.examples.misc.address.AddressType) getData().getFactory().getElement(org.apache.ws.jaxme.examples.misc.address.AddressType.class);
    } catch (final javax.xml.bind.JAXBException _1) {
      throw new org.xml.sax.SAXException(_1);
    }
  }

  @Override
public void startDocument() throws org.xml.sax.SAXException {
    level = 0;
    state = 0;
    handler = null;
  }

  @Override
public void startElement(final String namespaceURI, final String localName, final String qName, final org.xml.sax.Attributes attr) throws org.xml.sax.SAXException {
    switch (level++) {
      case 0:
        setResult(newResult());
        if (attr != null) {
          for (int _1 = 0;  _1 < attr.getLength();  _1++) {
            addAttribute(attr.getURI(_1), attr.getLocalName(_1), attr.getValue(_1));
          }
        }
        break;
      case 1:
        if ("http://ws.apache.org/jaxme/examples/misc/address".equals(namespaceURI) && "Name".equals(localName)) {
            switch (state) {
              case 0:
                state = 3;
                handler = getHandlerForName();
                handler.startDocument();
                handler.startElement(namespaceURI, localName, qName, attr);
                break;
              default:
                validationEvent(javax.xml.bind.ValidationEvent.WARNING, "The element " + qName + " was unexpected at this place.", org.apache.ws.jaxme.ValidationEvents.EVENT_UNEXPECTED_CHILD_STATE);
                break;
            }
          }
        break;
      default:
        if (handler == null) {
          super.startElement(namespaceURI, localName, qName, attr);
        } else {
          handler.startElement(namespaceURI, localName, qName, attr);
        }
    }
  }

}
