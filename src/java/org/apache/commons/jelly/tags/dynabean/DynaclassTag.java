/*
 * $Header:  $
 * $Revision: 1.0 $
 * $Date:  $
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
 * $Id:  $
 */
package org.apache.commons.jelly.tags.dynabean;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.ScriptBlock;
import org.apache.commons.jelly.impl.TagScript;
import org.apache.commons.jelly.expression.Expression;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.MissingAttributeException;

import org.apache.commons.beanutils.*;

/**
 * A tag which creates and defines and creates a DynaClass
 * The DynaClass object is placed by name in the context,
 * so that a DynaBean tag can use it by name to instantiate
 * a DynaBean object
 *
 * @author Theo Niemeijer
 * @version 1.0
 */
public class DynaclassTag extends TagSupport {

    private ArrayList propList = new ArrayList();
    private DynaProperty[] props = null;
    private DynaClass dynaClass = null;

    private String name;
    private String var;

    public DynaclassTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    public void doTag(XMLOutput output) throws Exception {

        if (name == null) {
            throw new MissingAttributeException("name");
        }

        if (var == null) {
            var = name;
        }

        // Evaluate the body of the dynaclass definition
        invokeBody(output);

        // Convert the list of properties into array
        props =
            (DynaProperty[]) propList.toArray(
                new DynaProperty[propList.size()]);

        if (props == null) {
            throw new JellyException("No properties list");
        }

        if (props.length == 0) {
            throw new JellyException("No properties");
        }

        // Create the dynaclass with name and properties
        dynaClass = (DynaClass) new BasicDynaClass(name, null, props);

        // Place new dynaclass in context
        context.setVariable(getVar(), dynaClass);
    }

    // Properties
    //-------------------------------------------------------------------------
    
    /**
     * Sets the name of the new DynaClass 
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getVar() {
        return var;
    }

    /**
     * Sets the name of the variable to export the DynaClass instance
     */
    public void setVar(String var) {
        this.var = var;
    }

    protected void addDynaProperty(DynaProperty prop) {
        propList.add(prop);
    }
}
