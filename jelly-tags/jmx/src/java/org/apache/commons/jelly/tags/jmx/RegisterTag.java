/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/jmx/src/java/org/apache/commons/jelly/tags/jmx/RegisterTag.java,v 1.1 2003/03/20 17:12:41 jstrachan Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/20 17:12:41 $
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
 * $Id: RegisterTag.java,v 1.1 2003/03/20 17:12:41 jstrachan Exp $
 */

package org.apache.commons.jelly.tags.jmx;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.CollectionTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Registers a JavaBean or JMX MBean with a server..
 * 
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public class RegisterTag extends TagSupport implements CollectionTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(RegisterTag.class);

    private ObjectName name;    
    private MBeanServer server;

    public RegisterTag() {
    }

    
    // CollectionTag interface
    //-------------------------------------------------------------------------                    
    public void addItem(Object bean) throws JellyTagException {
        try {
            register(server, bean);            
        } 
        catch (Exception e) {
            throw new JellyTagException("Failed to register bean: " + bean, e);                
        } 
    }
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (name == null) {
            throw new MissingAttributeException("name");
        }
        if (server == null) {
            ServerTag serverTag = (ServerTag) findAncestorWithClass(ServerTag.class);
            if (serverTag == null) {
                throw new JellyTagException("This class must be nested inside a <server> tag");
            }
            server = serverTag.getServer();
        }
        invokeBody(output);
	}

    
    // Properties
    //-------------------------------------------------------------------------                    
    
   
    /**
     * @return ObjectName
     */
    public ObjectName getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    public void setName(ObjectName name) {
        this.name = name;
    }

    /**
     * @return MBeanServer
     */
    public MBeanServer getServer() {
        return server;
    }

    /**
     * Sets the MBeanServer. If this attribute is not supplied then the parent &lt;server&gt; tag
     * is used to get the MBeanServer instance to use.
     * 
     * @param server The MBeanServer to register the mbeans with.
     */
    public void setServer(MBeanServer server) {
        this.server = server;
    }

    // Implementation methods
    //-------------------------------------------------------------------------   
                     
    /**
     * Registers the given bean with the MBeanServer
     */
    protected void register(MBeanServer server, Object bean) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        server.registerMBean(bean, getName());
    }

}
