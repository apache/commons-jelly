/*
 * Copyright 2002,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Created on 19 nov. 2004
 */
package org.apache.commons.jelly.core;

import junit.framework.TestCase;

/**
 * TODO Do documentation.
 * @version 0.0
 */
public class TestModularExceptionBean extends TestCase {

    /**
     * @param arg0
     */
    public TestModularExceptionBean(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /*public static void main(String[] args) {
        junit.swingui.TestRunner.run(TestModularExceptionBean.class);
    }*/

    public final void testThrowIt() {

        ModularExceptionBean bean = new ModularExceptionBean();
        bean.setException(NullPointerException.class.getName());
        try {
            bean.throwIt("myTest");
            fail("Exception must be caught");
        } catch ( NullPointerException e) {
            
        } catch (Throwable e) {
            fail("must be catched by upper tryCatch");
        }
        
    }

}
