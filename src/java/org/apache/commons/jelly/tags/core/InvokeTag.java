/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/core/InvokeTag.java,v 1.5 2003/12/25 21:52:31 polx Exp $
 * $Revision: 1.5 $
 * $Date: 2003/12/25 21:52:31 $
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
 * $Id: InvokeTag.java,v 1.5 2003/12/25 21:52:31 polx Exp $
 */
package org.apache.commons.jelly.tags.core;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**  A tag which calls a function in an object instantied by core:new.
  *
  * @author Rodney Waldhoff
  * @version $Revision: 1.5 $
  */
public class InvokeTag extends TagSupport implements ArgTagParent {

    /** the variable exported */
    private String var;
    
    /** the method to invoke */
    private String methodName;
    
    /** the object to invoke the method on */
    private Object onInstance;
    
    private List paramTypes = new ArrayList();
    private List paramValues = new ArrayList();
    
    public InvokeTag() {
    }

    /** Sets the name of the variable exported by this tag */
    public void setVar(String var) {
        this.var = var;
    }
    
    public void setMethod(String method) {
        this.methodName = method;
    }
    
    public void setOn(Object instance) {
        this.onInstance = instance;
    }
    
    public void addArgument(Class type, Object value) {
        paramTypes.add(type);
        paramValues.add(value);
    }
    
    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        if ( null == methodName) {
            throw new MissingAttributeException( "method" );
        }
        if ( null == onInstance ) {
            throw new MissingAttributeException( "on" );
        }
        
        invokeBody(output);
        
        Object[] values = paramValues.toArray();
        Class[] types = (Class[])(paramTypes.toArray(new Class[paramTypes.size()]));
        
        Object result = null;
        try {
            result = MethodUtils.invokeMethod(onInstance,methodName,values,types);
        }
        catch (NoSuchMethodException e) {
            throw new JellyTagException(e);
        }
        catch (IllegalAccessException e) {
            throw new JellyTagException(e);
        } 
        catch (InvocationTargetException e) {
            throw new JellyTagException(e);
        }
        
        paramTypes.clear();
        paramValues.clear();
        
        ArgTag parentArg = (ArgTag)(findAncestorWithClass(ArgTag.class));
        if(null != parentArg) {
            parentArg.setValue(result);
        }
        if(null != var) {
            context.setVariable(var, result);
        }
    }
}
