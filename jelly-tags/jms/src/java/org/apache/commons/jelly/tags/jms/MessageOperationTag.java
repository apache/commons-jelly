/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/jms/src/java/org/apache/commons/jelly/tags/jms/MessageOperationTag.java,v 1.1 2003/01/07 16:11:01 dion Exp $
 * $Revision: 1.1 $
 * $Date: 2003/01/07 16:11:01 $
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
 * $Id: MessageOperationTag.java,v 1.1 2003/01/07 16:11:01 dion Exp $
 */
package org.apache.commons.jelly.tags.jms;

import javax.jms.Destination;
import javax.jms.JMSException;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.messenger.Messenger;

/** An abstract base class for JMS Message operation tags such as send, receive or call.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.1 $
  */
public abstract class MessageOperationTag extends TagSupport implements ConnectionContext {

    /** The Messenger used to access the JMS connection */
    private Messenger connection;
    
    /** The Destination */
    private Destination destination;

    /** The String subject used to find a destination */
    private String subject;
    
    public MessageOperationTag() {
    }
    
    // Properties
    //-------------------------------------------------------------------------                                
    public Messenger getConnection() throws JellyException, JMSException {
        if ( connection == null ) {
            return findConnection();
        }
        return connection;
    }
    
    /**
     * Sets the Messenger (the JMS connection pool) that will be used to send the message
     */
    public void setConnection(Messenger connection) {
        this.connection = connection;
    }
    
    public Destination getDestination() throws JellyException, JMSException {
        if (destination == null) {
            // if we have a subject defined, lets use it to find the destination
            if (subject != null) {
                destination = findDestination(subject);
            }
        }
        return destination;
    }
    
    /**
     * Sets the JMS Destination to be used by this tag
     */
    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    /**
     * Sets the subject as a String which is used to create the 
     * JMS Destination to be used by this tag
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    // Implementation methods
    //-------------------------------------------------------------------------                            
    
    /**
     * Strategy Method allowing derived classes to change this behaviour
     */
    protected Messenger findConnection() throws JellyException, JMSException {
        ConnectionContext messengerTag = (ConnectionContext) findAncestorWithClass( ConnectionContext.class );
        if ( messengerTag == null ) {
            throw new JellyException("This tag must be within a <jms:connection> tag or the 'connection' attribute should be specified");
        }
        return messengerTag.getConnection();
    }

    /**
     * Strategy Method allowing derived classes to change this behaviour
     */
    protected Destination findDestination(String subject) throws JellyException, JMSException {
        return getConnection().getDestination(subject);
    }
}    
