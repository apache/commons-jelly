/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/taglibs/beanshell/src/java/org/apache/commons/jelly/tags/beanshell/BeanShellExpressionFactory.java,v 1.1 2002/05/21 07:58:55 jstrachan Exp $
 * $Revision: 1.1 $
 * $Date: 2002/05/21 07:58:55 $
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
 * $Id: BeanShellExpressionFactory.java,v 1.1 2002/05/21 07:58:55 jstrachan Exp $
 */
package org.apache.commons.jelly.tags.jms;

import javax.jms.Destination;
import javax.jms.Message;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Receives a JMS message.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.1 $
  */
public class ReceiveTag extends MessageOperationTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ReceiveTag.class);

    private String var;
    private long timeout = -1L;
    
    public ReceiveTag() {
    }
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws Exception {
        // evaluate body as it may contain a <destination> tag
        invokeBody(output);
        
        Destination destination = getDestination();
        if ( destination == null ) {
            throw new JellyException( "No destination specified. Either specify a 'destination' attribute or use a nested <jms:destination> tag" );
        }
        Message message = null;
        if ( timeout > 0 ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "Receiving message on destination: " + destination + " with timeout: " + timeout );
            }
            
            message = getConnection().receive( destination, timeout );
        }
        else if ( timeout == 0 ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "Receiving message on destination: " + destination + " with No Wait" );
            }
            
            message = getConnection().receiveNoWait( destination );
        }
        else {
            if ( log.isDebugEnabled() ) {
                log.debug( "Receiving message on destination: " + destination );
            }
            message = getConnection().receive( destination );
        }
        onMessage( message );
    }
    
    // Properties
    //-------------------------------------------------------------------------                                
    public String getVar() {
        return var;
    }
    
    /**
     * Sets the variable name to create for the received message, which will be null if no
     * message could be returned in the given time period.
     */
    public void setVar(String var) {
        this.var = var;
    }    
    
    public long getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout period in milliseconds to wait for a message. A value
     * of -1 will wait forever for a message.
     */    
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                            
    
    /** 
     * A strategy method which processes the incoming message, allowing derived classes
     * to implement different processing methods
     */
    protected void onMessage( Message message ) {
        if ( message != null ) {
            context.setVariable( var, message );
        }
        else {
            context.removeVariable( var );
        }
    }
}    
