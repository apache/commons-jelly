/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/test/org/apache/commons/jelly/core/TestArgTag.java,v 1.1 2002/11/30 07:41:21 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2002/11/30 07:41:21 $
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
 * $Id: TestArgTag.java,v 1.1 2002/11/30 07:41:21 rwaldhoff Exp $
 */
package org.apache.commons.jelly.core;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestSuite;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.ArgTag;
import org.apache.commons.jelly.tags.core.ArgTagParent;

/**
 * @author Rodney Waldhoff
 * @version $Revision: 1.1 $ $Date: 2002/11/30 07:41:21 $
 */
public class TestArgTag extends BaseJellyTest {

    public TestArgTag(String name) {
        super(name);
    }

    public static TestSuite suite() throws Exception {
        return new TestSuite(TestArgTag.class);        
    }

    public void setUp() throws Exception {
        super.setUp();
        parentTag = new MockArgTagParent();
        argTag = new ArgTag();
        argTag.setContext(getJellyContext());
        argTag.setParent(parentTag);
        argTag.setBody(new MockScript());
    }    
    
    public void tearDown() throws Exception {
        super.tearDown();
        parentTag = null;
        argTag = null;
    }    
    
    public void testToBooleanFromString() throws Exception {
        argTag.setType("boolean");
        argTag.setValue("true");
        argTag.doTag(getXMLOutput());
        assertEquals(Boolean.TYPE,parentTag.getType(0));
        assertEquals(Boolean.TRUE,parentTag.getValue(0));
    }
    
    public void testToCharFromString() throws Exception {
        argTag.setType("char");
        argTag.setValue("X");
        argTag.doTag(getXMLOutput());
        assertEquals(Character.TYPE,parentTag.getType(0));
        assertEquals(new Character('X'),parentTag.getValue(0));
    }

    public void testToByteFromString() throws Exception {
        argTag.setType("byte");
        argTag.setValue("17");
        argTag.doTag(getXMLOutput());
        assertEquals(Byte.TYPE,parentTag.getType(0));
        assertEquals(new Byte((byte)17),parentTag.getValue(0));
    }

    public void testToByteFromNumber() throws Exception {
        argTag.setType("byte");
        argTag.setValue(new Double(17.3d));
        argTag.doTag(getXMLOutput());
        assertEquals(Byte.TYPE,parentTag.getType(0));
        assertEquals(new Byte((byte)17),parentTag.getValue(0));
    }

    public void testToShortFromString() throws Exception {
        argTag.setType("short");
        argTag.setValue("17");
        argTag.doTag(getXMLOutput());
        assertEquals(Short.TYPE,parentTag.getType(0));
        assertEquals(new Short((short)17),parentTag.getValue(0));
    }

    public void testToShortFromNumber() throws Exception {
        argTag.setType("short");
        argTag.setValue(new Double(17.3d));
        argTag.doTag(getXMLOutput());
        assertEquals(Short.TYPE,parentTag.getType(0));
        assertEquals(new Short((short)17),parentTag.getValue(0));
    }

    public void testToIntFromString() throws Exception {
        argTag.setType("int");
        argTag.setValue("17");
        argTag.doTag(getXMLOutput());
        assertEquals(Integer.TYPE,parentTag.getType(0));
        assertEquals(new Integer((int)17),parentTag.getValue(0));
    }

    public void testToIntFromNumber() throws Exception {
        argTag.setType("int");
        argTag.setValue(new Double(17.3d));
        argTag.doTag(getXMLOutput());
        assertEquals(Integer.TYPE,parentTag.getType(0));
        assertEquals(new Integer((int)17),parentTag.getValue(0));
    }
    
    public void testToFloatFromString() throws Exception {
        argTag.setType("float");
        argTag.setValue("17.3");
        argTag.doTag(getXMLOutput());
        assertEquals(Float.TYPE,parentTag.getType(0));
        assertEquals(new Float((float)17.3),parentTag.getValue(0));
    }

    public void testToFloatFromNumber() throws Exception {
        argTag.setType("float");
        argTag.setValue(new Double(17.3d));
        argTag.doTag(getXMLOutput());
        assertEquals(Float.TYPE,parentTag.getType(0));
        assertEquals(new Float((float)17.3),parentTag.getValue(0));
    }
    
    public void testToLongFromString() throws Exception {
        argTag.setType("long");
        argTag.setValue("17");
        argTag.doTag(getXMLOutput());
        assertEquals(Long.TYPE,parentTag.getType(0));
        assertEquals(new Long((int)17),parentTag.getValue(0));
    }

    public void testToLongFromNumber() throws Exception {
        argTag.setType("long");
        argTag.setValue(new Double(17.3d));
        argTag.doTag(getXMLOutput());
        assertEquals(Long.TYPE,parentTag.getType(0));
        assertEquals(new Long((long)17),parentTag.getValue(0));
    }
    
    public void testToDoubleFromString() throws Exception {
        argTag.setType("double");
        argTag.setValue("17.3");
        argTag.doTag(getXMLOutput());
        assertEquals(Double.TYPE,parentTag.getType(0));
        assertEquals(new Double((double)17.3),parentTag.getValue(0));
    }

    public void testToDoubleFromNumber() throws Exception {
        argTag.setType("double");
        argTag.setValue(new Long(17L));
        argTag.doTag(getXMLOutput());
        assertEquals(Double.TYPE,parentTag.getType(0));
        assertEquals(new Double((double)17),parentTag.getValue(0));
    }

    public void testToPrimitiveFromNull() throws Exception {
        String[] types = { "boolean", "char", "byte", "short", "int", "float", "long", "double" };
        for(int i=0;i<types.length;i++) {
            argTag.setType(types[i]);
            argTag.setValue(null);
            try {
                argTag.doTag(getXMLOutput());
                fail("Expected JellyException");
            } catch (JellyException e) {
                // expected
            }
        }
    }

    public void testFromNull() throws Exception {
        Class[] types = { Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Float.class, Long.class, Double.class, String.class, Object.class };
        for(int i=0;i<types.length;i++) {
            argTag.setType(types[i].getName());
            argTag.setValue(null);
            argTag.doTag(getXMLOutput());
            assertEquals(types[i],parentTag.getType(i));
            assertNull(parentTag.getValue(i));
        }
    }

    private MockArgTagParent parentTag = null;
    private ArgTag argTag = null;    
    
    class MockArgTagParent extends TagSupport implements ArgTagParent {        
        public void addArgument(Class type, Object value) {
            typeList.add(type);
            valueList.add(value);
        }

        public void doTag(XMLOutput output) throws Exception {
        }
        
        private Class getType(int i) {
            return (Class)(typeList.get(i));
        }
        
        private Object getValue(int i) {
            return valueList.get(i);
        }
        
        private List typeList = new ArrayList();
        private List valueList = new ArrayList();
    }
    
    class MockScript implements Script {
        public Script compile() throws Exception {
            return this;
        }

        public void run(JellyContext context, XMLOutput output) throws Exception {
        }
    }
    
}