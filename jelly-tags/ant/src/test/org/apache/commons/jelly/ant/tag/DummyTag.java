/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/core/IfTag.java,v 1.9 2002/10/30 19:16:21 jstrachan Exp $
 * $Revision: 1.9 $
 * $Date: 2002/10/30 19:16:21 $
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
 * $Id: IfTag.java,v 1.9 2002/10/30 19:16:21 jstrachan Exp $
 */
package org.apache.commons.jelly.ant.tag;

import junit.framework.AssertionFailedError;

import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.BeanSource;
import org.apache.commons.jelly.tags.ant.AntTagLibrary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.types.Path;

/** 
 * A mock tag which is used for testing the Ant nested properties behaviour
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.9 $
 */
public class DummyTag extends TagSupport implements BeanSource {

    /** The Log to which logging calls will be made. */
    private static Log log = LogFactory.getLog(DummyTag.class);

	private String var;
	
	private boolean calledCreatepath;
	private boolean calledSetClasspath;
	private Path classpath;
	
    public DummyTag() {
    }

    // BeanSource interface
    //-------------------------------------------------------------------------
    public Object getBean() {
    	return this;
    }                

    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws Exception {
    	calledCreatepath = false;    	
    	calledSetClasspath = false;    	

        invokeBody(output);
        
        if (! calledCreatepath) {
        	throw new AssertionFailedError("call to createClasspath() was not made");
        }
        
        if (! calledSetClasspath) {
        	throw new AssertionFailedError("call to setClasspath() was not made");
        }
        log.info( "Called with classpath: " + classpath );
        
        if (var != null) {
        	context.setVariable(var, classpath);
        }
    }
    
    // Ant Task-like nested property methods
    //-------------------------------------------------------------------------
    public Path createClasspath() {
    	log.info("called createClasspath()");
    	calledCreatepath = true;    	
    	return new Path( AntTagLibrary.getProject(context) );
    }
    
    public void setClasspath(Path classpath) {
    	log.info("called setClasspath()");
    	calledSetClasspath = true;    	
    	this.classpath = classpath;
    } 
    
    // Tag properties
    //-------------------------------------------------------------------------
    
    public void setVar(String var) {
    	this.var = var;
    }
}
