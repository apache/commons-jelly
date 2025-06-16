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
package org.apache.commons.jelly.tags.junit;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Represents a collection of TestCases.. This tag is analagous to
 * JUnit's TestSuite class.
 */
public class SuiteTag extends TagSupport {

    /** The test suite this tag created */
    private TestSuite suite;

    /** The name of the variable of the test suite */
    private String var;

    /** The name of the test suite to create */
    private String name;

    public SuiteTag() {
    }

    /**
     * Adds a new Test to this suite
     */
    public void addTest(final Test test) {
        getSuite().addTest(test);
    }

    /**
     * Factory method to create a new TestSuite
     */
    protected TestSuite createSuite() {
        if ( name == null ) {
            return new TestSuite();
        }
        return new TestSuite(name);
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        suite = createSuite();

        final TestSuite parent = (TestSuite) context.getVariable("org.apache.commons.jelly.junit.suite");
        if ( parent == null ) {
            context.setVariable("org.apache.commons.jelly.junit.suite", suite );
        }
        else {
            parent.addTest( suite );
        }

        invokeBody(output);

        if ( var != null ) {
            context.setVariable(var, suite);
        }
    }

    /**
     * @return the name of this test suite
     */
    public String getName() {
        return name;
    }

    // Properties
    //-------------------------------------------------------------------------
    public TestSuite getSuite() {
        return suite;
    }

    /**
     * Sets the name of this test suite
     */
    public void setName(final String name) {
        this.name = name;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the name of the test suite whichi is exported
     */
    public void setVar(final String var) {
        this.var = var;
    }
}
