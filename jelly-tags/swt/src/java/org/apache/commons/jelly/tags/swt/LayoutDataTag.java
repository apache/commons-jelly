/*
 * /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/swt/LayoutDataTag.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
 * 1.1
 * 2002/12/18 15:27:49
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
 * LayoutDataTag.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
 */
package org.apache.commons.jelly.tags.swt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/** 
 * Creates a LayoutData object and sets it on the parent Widget.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version 1.1
 */
public class LayoutDataTag extends LayoutTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(LayoutDataTag.class);

    public LayoutDataTag(Class layoutDataClass) {
        super(layoutDataClass);
    }

    // Implementation methods
    //-------------------------------------------------------------------------                    

    /**
     * Either defines a variable or adds the current component to the parent
     */
    protected void processBean(String var, Object bean)
        throws JellyTagException {
        super.processBean(var, bean);

        Widget parent = getParentWidget();

        if (parent instanceof Control) {
            Control control = (Control) parent;
            control.setLayoutData(getBean());
        } else {
            throw new JellyTagException("This tag must be nested within a control widget tag");
        }
    }

    /**
     * @see org.apache.commons.jelly.tags.core.UseBeanTag#newInstance(java.lang.Class, java.util.Map, org.apache.commons.jelly.XMLOutput)
     */
    protected Object newInstance(
        Class theClass,
        Map attributes,
        XMLOutput output)
        throws JellyTagException {

        String text = (String) attributes.remove("style");
        if (text != null) {
            int style = SwtHelper.parseStyle(theClass, text);

            // now lets try invoke a constructor
            Class[] types = { int.class };

            try {
                Constructor constructor = theClass.getConstructor(types);
                if (constructor != null) {
                    Object[] values = { new Integer(style)};
                    return constructor.newInstance(values);
                }
            } catch (NoSuchMethodException e) {
                throw new JellyTagException(e);
            } catch (InstantiationException e) {
                throw new JellyTagException(e);
            } catch (IllegalAccessException e) {
                throw new JellyTagException(e);
            } catch (InvocationTargetException e) {
                throw new JellyTagException(e);
            }
        }
        return super.newInstance(theClass, attributes, output);
    }

    /**
     * @see org.apache.commons.jelly.tags.swt.LayoutTagSupport#convertValue(java.lang.Object, java.lang.String, java.lang.Object)
     */
    protected Object convertValue(Object bean, String name, Object value)
        throws JellyTagException {

        if (bean instanceof GridData) {
            if (name.endsWith("Alignment") && value instanceof String) {
                int style =
                    SwtHelper.parseStyle(bean.getClass(), (String) value);
                return new Integer(style);
            }
        }
        return super.convertValue(bean, name, value);
    }

}
