/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/define/DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 * $Revision: 1.7 $
 * $Date: 2002/05/17 15:18:12 $
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
 * $Id: DynamicTag.java,v 1.7 2002/05/17 15:18:12 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.util.Map;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.jelly.tags.swing.impl.GridBagConstraintBean;

/** 
 * This class represents a {@link GridBagConstraints} constraints as passed in
 * the second argument of {@link Container#add(Component,Object)}.
 * It supports inheritence between such tags in the following fashion:
 * <ul>
 * 	<li>either using a <code>basedOn</code> attribute which is
 * 		supposed to provide a reference to another {@link GbcTag}.</li>
 * 	<li>either using a parent {@link GbcTag}.</li>
 * </ul>
 * The first version takes precedence.
 * A Grid-bag-constraint inherits from another simply by setting other attributes
 * as is done in {@link GridBagConstraintBean#setBasedOn}.
 * <p>
 * In essence, it looks really like nothing else than a bean-class...
 * with {@link #getConstraints}.
 * Probably a shorter java-source is do-able.
 * <p>
 * TODO: this class should probably be extended with special treatment for dimensions
 * using the converter package.
 *
 * @author <a href="mailto:paul@activemath.org">Paul Libbrecht</a>
 * @version $Revision: $
 */
public class GbcTag extends UseBeanTag implements ContainerTag {

    public GridBagConstraints getConstraints() {
        return (GridBagConstraints) getBean();
    }
    
    
    // ContainerTag interface
    //-------------------------------------------------------------------------                    
    
    /**
     * Adds a child component to this parent
     */
    public void addChild(Component component, Object constraints) throws JellyException {
        GridBagLayoutTag tag = (GridBagLayoutTag) findAncestorWithClass( GridBagLayoutTag.class );
        if (tag == null) {
            throw new JellyException( "this tag must be nested within a <tr> tag" );
        }
        tag.addLayoutComponent(component, getConstraints());
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                    
    
    /**
     * A class may be specified otherwise the Factory will be used.
     */
    protected Class convertToClass(Object classObject) 
    throws MissingAttributeException, ClassNotFoundException {
        if (classObject == null) {
            return null;
        }
        else {
            return super.convertToClass(classObject);
        }
    }
    
    /**
     * A class may be specified otherwise the Factory will be used.
     */
    protected Object newInstance(Class theClass, Map attributes, XMLOutput output) throws JellyException {
        if (theClass != null ) {
            try {
                return theClass.newInstance();
            } catch (IllegalAccessException e) {
                throw new JellyException(e);
            } catch (InstantiationException e) {
                throw new JellyException(e);
            }
        }
        else {
            return new GridBagConstraintBean();
        }
    }
}

