/*
 * $Header:$
 * $Revision:$
 * $Date:$
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
 * $Id:$
 */
package org.apache.commons.jelly.tags.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;

/** A tag which creates a new object of the given type
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision:$
  */
public class NewTag extends BaseClassLoaderTag implements ArgTagParent {

    /** the variable exported */
    private String var;
    
    /** the class name of the object to instantiate */
    private String className;
    
    private List paramTypes = new ArrayList();
    private List paramValues = new ArrayList();
    
    public NewTag() {
    }

    /** Sets the name of the variable exported by this tag */
    public void setVar(String var) {
        this.var = var;
    }
    
    /** Sets the class name of the object to instantiate */
    public void setClassName(String className) {
        this.className = className;
    }
    
    public void addArgument(Class type, Object value) {
        paramTypes.add(type);
        paramValues.add(value);
    }
    
    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws Exception {
        ArgTag parentArg = null;
        if ( var == null ) {
            parentArg = (ArgTag)(findAncestorWithClass(ArgTag.class));
            if(null == parentArg) {
                throw new MissingAttributeException( "var" );
            }
        }
        if ( className == null ) {
            throw new MissingAttributeException( "className" );
        }
        invokeBody(output);

        Class theClass = getClassLoader().loadClass( className );
        Object object = null;
        if(paramTypes.size() == 0) {
            object = theClass.newInstance();
        } else {
            Object[] values = paramValues.toArray();
            Class[] types = (Class[])(paramTypes.toArray(new Class[paramTypes.size()]));
            object = ConstructorUtils.invokeConstructor(theClass,values,types);
            paramTypes.clear();
            paramValues.clear();
        }
        if(null != var) {
            context.setVariable(var, object);
        } else {
            parentArg.setValueObject(object);
        }
    }
}
