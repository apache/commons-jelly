/*
 * /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/swt/Attic/LayoutTag.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
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
 * LayoutTag.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
 */
package org.apache.commons.jelly.tags.swt;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Widget;

/** 
 * Creates a new Layout implementations and adds it to the parent Widget.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version 1.1
 */
public class LayoutTag extends LayoutTagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(LayoutTag.class);

    public LayoutTag(Class layoutClass) {
        super(layoutClass);
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * @return the Layout if there is one otherwise null
     */
    public Layout getLayout() {
        Object bean = getBean();
        if ( bean instanceof Layout ) {
            return (Layout) bean;
        }
        return null;
    }

    // Implementation methods
    //-------------------------------------------------------------------------                    

    /**
     * Either defines a variable or adds the current component to the parent
     */
    protected void processBean(String var, Object bean) throws Exception {
        super.processBean(var, bean);
        
        Widget parent = getParentWidget();
        if (parent instanceof Composite) {
            Composite composite = (Composite) parent;
            composite.setLayout(getLayout());
        }
        else {
            throw new JellyException("This tag must be nested within a composite widget tag");
        }
    }
    
    /**
     * @see org.apache.commons.jelly.tags.swt.LayoutTagSupport#convertValue(java.lang.Object, java.lang.String, java.lang.Object)
     */
    protected Object convertValue(Object bean, String name, Object value)
        throws Exception {
            
        if (bean instanceof FillLayout && name.equals("type") && value instanceof String) {
            int style = SwtHelper.parseStyle(SWT.class, (String) value);
            return new Integer(style);
        }
        return super.convertValue(bean, name, value);
    }

}
