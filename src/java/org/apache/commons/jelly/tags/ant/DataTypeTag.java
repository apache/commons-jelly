/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/core/IfTag.java,v 1.6 2002/05/17 15:18:08 jstrachan Exp $
 * $Revision: 1.6 $
 * $Date: 2002/05/17 15:18:08 $
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
 * $Id: IfTag.java,v 1.6 2002/05/17 15:18:08 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.ant;

import java.lang.reflect.Method;

import org.apache.commons.beanutils.ConvertingWrapDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.MethodUtils;

import org.apache.commons.jelly.DynaBeanTagSupport;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.Tag;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/** 
 * A tag which manages a DataType used to configure a Task
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.6 $
 */
public class DataTypeTag extends AntTagSupport {

    /** the name of the DataType */
    private String name;
    
    /** the Ant DataType */
    private DataType dataType;

    public DataTypeTag() {
    }

    public DataTypeTag(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
        setDynaBean( new ConvertingWrapDynaBean(dataType) );
    }

    public Object getObject() {
        return this.dataType;
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws Exception {

        // run the body first to configure any nested DataType instances
        getBody().run(context, output);

        AntTagSupport parentTag = (AntTagSupport) findAncestorWithClass( AntTagSupport.class);

        if ( parentTag == null ) {
            // ignore, as all it can be is a top-level datatype with
            // an id which has -already- added it to the project thanks
            // to the setAttribute() call.
            return;
        }

        Object   targetObj = parentTag.getObject();
        DataType dataType  = getDataType();

        if ( targetObj == null ) {
            // ignore, as all it can be is a top-level datatype with
            // an id which has -already- added it to the project thanks
            // to the setAttribute() call.
            return;
        }

        if( parentTag instanceof DynaBeanTagSupport ) {
            DynaBean dynaBean = ((DynaBeanTagSupport)parent).getDynaBean();
            DynaClass dynaClass = dynaBean.getDynaClass();
            DynaProperty dynaProperty = dynaClass.getDynaProperty(name);

            if ( dynaProperty != null ) {
                // lets set the bean property
                try {
                    dynaBean.set( name, dataType );
                    return;
                } catch (Exception e) {
                    // ignore, maybe something else will work.
                }
            }
        }

        if ( targetObj instanceof Path
             &&
             dataType instanceof Path ) {
            ((Path)targetObj).append( (Path)dataType );
            return;
        }
        
        IntrospectionHelper ih = IntrospectionHelper.getHelper( targetObj.getClass() );
        
        try
        {
            ih.storeElement( getAntProject(),
                             targetObj,
                             dataType,
                             getName() );
        }
        catch (Exception e) {
            String dataTypeName = dataType.getClass().getName();
            String baseName = dataTypeName.substring( dataTypeName.lastIndexOf( "." ) + 1 );

            String methName = "add" + baseName;

            Method m = MethodUtils.getAccessibleMethod( targetObj.getClass(),
                                                        methName,
                                                        dataType.getClass() );

            if ( m == null ) {
                String lname = baseName.toLowerCase();
                methName = "add" + lname.substring( 0, 1 ).toUpperCase() + lname.substring( 1 );

                m = MethodUtils.getAccessibleMethod( targetObj.getClass(),
                                                     methName,
                                                     dataType.getClass() );
            }

            if ( m != null ) {
                try
                {
                    m.invoke( targetObj, new Object[] { dataType } );
                    return;
                }
                catch (Exception i) {
                    i.printStackTrace();
                }
            }
        }
    }
    
    // Properties
    //-------------------------------------------------------------------------                
    
    /** 
     * @return the name of the DataType 
     */
    public String getName() {
        return name;
    }
    
    /** 
     * Sets the name of the DataType 
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /** 
     * @return the Ant dataType
     */
    public DataType getDataType() {
        return dataType;
    }
    
    /** 
     * Sets the Ant dataType
     */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
        setDynaBean( new ConvertingWrapDynaBean(dataType) );
    }

    public void setAttribute(String name, Object value) throws Exception {
        if ( "id".equals( name ) ) {
            getAntProject().addReference( (String) value, dataType );
            return;
        }

        if ( "refid".equals( name ) ) {

            Object refd = getAntProject().getReferences().get( value );


            this.dataType.setRefid( new Reference( (String) value ) );
            return;
        }

        super.setAttribute( name, value );
    }

    public String toString() {
        return "[DataTypeTag: name=" + getName()
            + "; dataType=" + getDataType() + "]";
    }
    
}
