/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/JellyException.java,v 1.10 2002/10/30 13:19:57 jstrachan Exp $
 * $Revision: 1.10 $
 * $Date: 2002/10/30 13:19:57 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * $Id: JellyException.java,v 1.10 2002/10/30 13:19:57 jstrachan Exp $
 */

package org.apache.commons.jelly;

import java.io.PrintStream;
import java.io.PrintWriter;

/** 
 * <p><code>JellyException</code> is the root of all Jelly exceptions.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.10 $
 */

public class JellyException extends Exception {
    
    /** the underlying cause of the exception */
    private Throwable cause;

    /** the Jelly file which caused the problem */
    private String fileName;

    /** the tag name which caused the problem */
    private String elementName;

    /** the line number in the script of the error */
    private int lineNumber = -1;
    
    /** the column number in the script of the error */
    private int columnNumber = -1;
    
    public JellyException() {
    }

    public JellyException(String message) {
        super(message);
    }

    public JellyException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }
    
    public JellyException(Throwable cause) {
        super(cause.getLocalizedMessage());
        this.cause = cause;
    }
    
    public JellyException(Throwable cause, String fileName, String elementName, int columnNumber, int lineNumber) {
        this(cause.getLocalizedMessage(), cause, fileName, elementName, columnNumber, lineNumber);
    }
    
    public JellyException(String reason, Throwable cause, String fileName, String elementName, int columnNumber, int lineNumber) {
        super( (reason==null?cause.getClass().getName():reason) );
        this.cause = cause;
        this.fileName = fileName;
        this.elementName = elementName;
        this.columnNumber = columnNumber;
        this.lineNumber = lineNumber;
    }
    
    public JellyException(String reason, String fileName, String elementName, int columnNumber, int lineNumber) {
        super(reason);
        this.fileName = fileName;
        this.elementName = elementName;
        this.columnNumber = columnNumber;
        this.lineNumber = lineNumber;
    }
    
    public Throwable getCause() {
        return cause;
    }

    
    /** 
     * @return the line number of the tag 
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /** 
     * Sets the line number of the tag 
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /** 
     * @return the column number of the tag 
     */
    public int getColumnNumber() {
        return columnNumber;
    }
    
    /** 
     * Sets the column number of the tag 
     */
    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    /** 
     * @return the Jelly file which caused the problem 
     */
    public String getFileName() {
        return fileName;
    }

    /** 
     * Sets the Jelly file which caused the problem 
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    

    /** 
     * @return the element name which caused the problem
     */
    public String getElementName() {
        return elementName;
    }

    /** 
     * Sets the element name which caused the problem
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }
    
    
    public String getMessage() {
        return super.getMessage() + " File: " + fileName + " At tag <" + elementName + ">: line: " 
            + lineNumber + " column: " + columnNumber;
    }

    public String getReason() {
        return super.getMessage();
    }

    // #### overload the printStackTrace methods...
    public void printStackTrace(PrintWriter s) { 
        synchronized (s) {
            super.printStackTrace(s);
            if  (cause != null) {
                s.println("Root cause");
                cause.printStackTrace(s);
            }
        }
    }
        
    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            super.printStackTrace(s);
            if  (cause != null) {
                s.println("Root cause");
                cause.printStackTrace(s);
            }
        }
    }
}
