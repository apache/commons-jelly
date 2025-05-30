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

package org.apache.commons.jelly;

import java.io.PrintStream;
import java.io.PrintWriter;

/** 
 * <p><code>JellyException</code> is the root of all Jelly exceptions.</p>
 */

public class JellyException extends Exception implements LocationAware {
    
    /** The underlying cause of the exception */
    private Throwable cause;

    /** The Jelly file which caused the problem */
    private String fileName;

    /** The tag name which caused the problem */
    private String elementName;

    /** The line number in the script of the error */
    private int lineNumber = -1;
    
    /** The column number in the script of the error */
    private int columnNumber = -1;
    
    public JellyException() {
    }

    public JellyException(String message) {
        super(message);
    }

    public JellyException(String reason, String fileName, String elementName, int columnNumber, int lineNumber) {
        super(reason);
        this.fileName = fileName;
        this.elementName = elementName;
        this.columnNumber = columnNumber;
        this.lineNumber = lineNumber;
    }
    
    public JellyException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }
    
    public JellyException(String reason, Throwable cause, String fileName, String elementName, int columnNumber, int lineNumber) {
        super( (reason == null ? cause.getClass().getName() : reason) );
        this.cause = cause;
        this.fileName = fileName;
        this.elementName = elementName;
        this.columnNumber = columnNumber;
        this.lineNumber = lineNumber;
    }
    
    public JellyException(Throwable cause) {
        super(cause.getLocalizedMessage());
        this.cause = cause;
    }
    
    public JellyException(Throwable cause, String fileName, String elementName, int columnNumber, int lineNumber) {
        this(cause.getLocalizedMessage(), cause, fileName, elementName, columnNumber, lineNumber);
    }
    
    @Override
    public Throwable getCause() {
        return cause;
    }

    
    /** 
     * @return the column number of the tag 
     */
    @Override
    public int getColumnNumber() {
        return columnNumber;
    }
    
    /** 
     * @return the element name which caused the problem
     */
    @Override
    public String getElementName() {
        return elementName;
    }

    /** 
     * @return the Jelly file which caused the problem 
     */
    @Override
    public String getFileName() {
        return fileName;
    }
    
    /** 
     * @return the line number of the tag 
     */
    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String getMessage() {
        if (fileName == null && lineNumber == -1 && columnNumber == -1 && elementName == null) {
            return getReason();
        } else {
            return fileName + ":" + lineNumber + ":" + columnNumber + ": <" + elementName + "> " + getReason();
        }
    }

    public String getReason() {
        return super.getMessage();
    }
    

    @Override
    public void printStackTrace() {
        super.printStackTrace();
        if (cause != null) {
            System.out.println("Root cause");
            cause.printStackTrace();
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            super.printStackTrace(s);
            if  (cause != null) {
                s.println("Root cause");
                cause.printStackTrace(s);
            }
        }
    }
    
    
    // #### overload the printStackTrace methods...
    @Override
    public void printStackTrace(PrintWriter s) { 
        synchronized (s) {
            super.printStackTrace(s);
            if  (cause != null) {
                s.println("Root cause");
                cause.printStackTrace(s);
            }
        }
    }

    /** 
     * Sets the column number of the tag 
     */
    @Override
    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    /** 
     * Sets the element name which caused the problem
     */
    @Override
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }
        
    /** 
     * Sets the Jelly file which caused the problem 
     */
    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /** 
     * Sets the line number of the tag 
     */
    @Override
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

}
