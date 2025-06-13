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
package org.apache.commons.jelly.tags.define;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import org.apache.tools.ant.types.FileSet;

/**
 * An example Runnable bean that is framework neutral and just performs
 * some useful function.
 */
public class MyRunnable implements Runnable {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(MyRunnable.class);

    private int x;
    private String y;
    private final List fileSets = new ArrayList();

    public MyRunnable() {
    }

    // Adder methods
    //-------------------------------------------------------------------------
    /*

    Commented out method to remove test-only dependency on ant

    public void addFileset(FileSet fileSet) {
        fileSets.add(fileSet);
    }
    */

    // Runnable interface
    //-------------------------------------------------------------------------
    @Override
    public void run() {
        log.info( "About to do something where x = " + getX() + " y = " + getY() );
        log.info( "FileSets are: " + fileSets );
    }

    // Properties
    //-------------------------------------------------------------------------
    public int getX() {
        return x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(final String y) {
        this.y = y;
    }
}
