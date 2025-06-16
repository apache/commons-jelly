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
package org.apache.commons.jelly.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.ArgTag;
import org.apache.commons.jelly.tags.core.ArgTagParent;
import org.apache.commons.jelly.test.BaseJellyTest;

import junit.framework.TestSuite;

/*
 */
public class TestArgTag extends BaseJellyTest {

    final class MockArgTagParent extends TagSupport implements ArgTagParent {
        private final List typeList = new ArrayList();

        private final List valueList = new ArrayList();

        @Override
        public void addArgument(final Class type, final Object value) {
            typeList.add(type);
            valueList.add(value);
        }

        @Override
        public void doTag(final XMLOutput output)  {
        }

        private Class getType(final int i) {
            return (Class)typeList.get(i);
        }
        private Object getValue(final int i) {
            return valueList.get(i);
        }
    }

    final class MockScript implements Script {
        @Override
        public Script compile() throws JellyException {
            return this;
        }

        @Override
        public void run(final JellyContext context, final XMLOutput output) throws JellyTagException {
        }
    }

    public static TestSuite suite() throws Exception {
        return new TestSuite(TestArgTag.class);
    }

    private MockArgTagParent parentTag = null;

    private ArgTag argTag = null;

    public TestArgTag(final String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        parentTag = new MockArgTagParent();
        argTag = new ArgTag();
        argTag.setContext(getJellyContext());
        argTag.setParent(parentTag);
        argTag.setBody(new MockScript());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        parentTag = null;
        argTag = null;
    }

    public void testFromNull() throws Exception {
        final Class[] types = { Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Float.class, Long.class, Double.class, String.class, Object.class };
        for(int i=0;i<types.length;i++) {
            argTag.setType(types[i].getName());
            argTag.setValue(null);
            argTag.doTag(getXMLOutput());
            assertEquals(types[i],parentTag.getType(i));
            assertNull(parentTag.getValue(i));
        }
    }

    public void testToBooleanFromString() throws Exception {
        argTag.setType("boolean");
        argTag.setValue("true");
        argTag.doTag(getXMLOutput());
        assertEquals(Boolean.TYPE,parentTag.getType(0));
        assertEquals(Boolean.TRUE,parentTag.getValue(0));
    }

    public void testToByteFromNumber() throws Exception {
        argTag.setType("byte");
        argTag.setValue(Double.valueOf(17.3d));
        argTag.doTag(getXMLOutput());
        assertEquals(Byte.TYPE,parentTag.getType(0));
        assertEquals(Byte.valueOf((byte)17),parentTag.getValue(0));
    }

    public void testToByteFromString() throws Exception {
        argTag.setType("byte");
        argTag.setValue("17");
        argTag.doTag(getXMLOutput());
        assertEquals(Byte.TYPE,parentTag.getType(0));
        assertEquals(Byte.valueOf((byte)17),parentTag.getValue(0));
    }

    public void testToCharFromString() throws Exception {
        argTag.setType("char");
        argTag.setValue("X");
        argTag.doTag(getXMLOutput());
        assertEquals(Character.TYPE,parentTag.getType(0));
        assertEquals(Character.valueOf('X'),parentTag.getValue(0));
    }

    public void testToDoubleFromNumber() throws Exception {
        argTag.setType("double");
        argTag.setValue(new Long(17L));
        argTag.doTag(getXMLOutput());
        assertEquals(Double.TYPE,parentTag.getType(0));
        assertEquals(Double.valueOf(17),parentTag.getValue(0));
    }

    public void testToDoubleFromString() throws Exception {
        argTag.setType("double");
        argTag.setValue("17.3");
        argTag.doTag(getXMLOutput());
        assertEquals(Double.TYPE,parentTag.getType(0));
        assertEquals(Double.valueOf(17.3),parentTag.getValue(0));
    }

    public void testToFloatFromNumber() throws Exception {
        argTag.setType("float");
        argTag.setValue(Double.valueOf(17.3d));
        argTag.doTag(getXMLOutput());
        assertEquals(Float.TYPE,parentTag.getType(0));
        assertEquals(Float.valueOf((float)17.3),parentTag.getValue(0));
    }

    public void testToFloatFromString() throws Exception {
        argTag.setType("float");
        argTag.setValue("17.3");
        argTag.doTag(getXMLOutput());
        assertEquals(Float.TYPE,parentTag.getType(0));
        assertEquals(Float.valueOf((float)17.3),parentTag.getValue(0));
    }

    public void testToIntFromNumber() throws Exception {
        argTag.setType("int");
        argTag.setValue(Double.valueOf(17.3d));
        argTag.doTag(getXMLOutput());
        assertEquals(Integer.TYPE,parentTag.getType(0));
        assertEquals(Integer.valueOf(17),parentTag.getValue(0));
    }

    public void testToIntFromString() throws Exception {
        argTag.setType("int");
        argTag.setValue("17");
        argTag.doTag(getXMLOutput());
        assertEquals(Integer.TYPE,parentTag.getType(0));
        assertEquals(Integer.valueOf(17),parentTag.getValue(0));
    }

    public void testToLongFromNumber() throws Exception {
        argTag.setType("long");
        argTag.setValue(Double.valueOf(17.3d));
        argTag.doTag(getXMLOutput());
        assertEquals(Long.TYPE,parentTag.getType(0));
        assertEquals(Long.valueOf(17),parentTag.getValue(0));
    }


    public void testToLongFromString() throws Exception {
        argTag.setType("long");
        argTag.setValue("17");
        argTag.doTag(getXMLOutput());
        assertEquals(Long.TYPE,parentTag.getType(0));
        assertEquals(Long.valueOf(17),parentTag.getValue(0));
    }

    public void testToObjectArray() throws Exception {
        argTag.setType(Object[].class.getName());
        argTag.setValue(new Object[] {"testObjectArray"});
        argTag.doTag(getXMLOutput());
        assertEquals(Object[].class,parentTag.getType(0));
        assertEquals("testObjectArray",((Object[]) parentTag.getValue(0))[0]);
    }

    public void testToObjectArrayClass() throws Exception {
        argTag.setType(Class.class.getName());
        argTag.setValue(Object[].class.getName());
        argTag.doTag(getXMLOutput());
        assertEquals(Class.class,parentTag.getType(0));
        assertEquals(Object[].class,parentTag.getValue(0));
    }
    public void testToPrimitiveFromNull() throws Exception {
        final String[] types = { "boolean", "char", "byte", "short", "int", "float", "long", "double" };
        for (final String type : types) {
            argTag.setType(type);
            argTag.setValue(null);
            try {
                argTag.doTag(getXMLOutput());
                fail("Expected JellyException");
            } catch (final JellyException e) {
                // expected
            }
        }
    }

    public void testToShortFromNumber() throws Exception {
        argTag.setType("short");
        argTag.setValue(Double.valueOf(17.3d));
        argTag.doTag(getXMLOutput());
        assertEquals(Short.TYPE,parentTag.getType(0));
        assertEquals(Short.valueOf((short)17),parentTag.getValue(0));
    }

    public void testToShortFromString() throws Exception {
        argTag.setType("short");
        argTag.setValue("17");
        argTag.doTag(getXMLOutput());
        assertEquals(Short.TYPE,parentTag.getType(0));
        assertEquals(Short.valueOf((short)17),parentTag.getValue(0));
    }

}