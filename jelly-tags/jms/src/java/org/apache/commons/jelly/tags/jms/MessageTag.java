/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/jms/src/java/org/apache/commons/jelly/tags/jms/MessageTag.java,v 1.2 2003/01/26 06:24:47 morgand Exp $
 * $Revision: 1.2 $
 * $Date: 2003/01/26 06:24:47 $
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
 * $Id: MessageTag.java,v 1.2 2003/01/26 06:24:47 morgand Exp $
 */
package org.apache.commons.jelly.tags.jms;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.JMSException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import org.apache.commons.messenger.Messenger;

/** A tag which creates a JMS message
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.2 $
  */
public class MessageTag extends TagSupport {

    /** The name of the Message variable that is created */
    private String var;    
    
    /** The JMS Message created */
    private Message message;
    
    /** The Messenger used to access the JMS connection */
    private Messenger connection;
    
    public MessageTag() {
    }
    
    /** Adds a JMS property to the message */
    public void addProperty(String name, Object value) throws JellyTagException {
        Message message = getMessage();
        
        try {
            message.setObjectProperty(name, value);
        } catch (JMSException e) {
            throw new JellyTagException(e);
        }
    }
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws JellyTagException {                
        if ( var == null ) {
            // expose message to parent message consumer
            SendTag tag = (SendTag) findAncestorWithClass( SendTag.class );
            if ( tag == null ) {
                throw new JellyTagException("<jms:message> tags must either have the 'var' attribute specified or be used inside a <jms:send> tag");
            }
            
            tag.setMessage( getMessage() );
        }
        else {
            
            context.setVariable( var, getMessage() );

        }        
    }
    
    // Properties
    //-------------------------------------------------------------------------                            
    
    /** Sets the name of the variable that the message will be exported to */
    public void setVar(String var) {
        this.var = var;        
    }
    
    public Messenger getConnection() throws JellyTagException {
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
    
    public Message getMessage() throws JellyTagException {
        if ( message == null ) {
            message = createMessage();
        }
        return message;
    }
    

    // JMS related properties

    /**
     * Sets the JMS Correlation ID to be used on the message 
     */    
    public void setCorrelationID(String correlationID) throws JellyTagException {
        try {
            getMessage().setJMSCorrelationID(correlationID);
        } 
        catch (JMSException e) {
            throw new JellyTagException(e);
        }
    }
    
    /**
     * Sets the reply-to destination to add to the message
     */
    public void setReplyTo(Destination destination) throws JellyTagException {
        try {
            getMessage().setJMSReplyTo(destination);
        } 
        catch (JMSException e) {
            throw new JellyTagException(e);
        }
    }
    
    /**
     * Sets the type name of the message
     */
    public void setType(String type) throws JellyTagException {
        try {
            getMessage().setJMSType(type);
        } 
        catch (JMSException e) {
            throw new JellyTagException(e);
        }
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                            
    protected Messenger findConnection() throws JellyTagException {
        ConnectionContext messengerTag = (ConnectionContext) findAncestorWithClass( ConnectionContext.class );
        if ( messengerTag == null ) {
            throw new JellyTagException("This tag must be within a <jms:connection> tag or the 'connection' attribute should be specified");
        }
        
        try {
            return messengerTag.getConnection();
        } 
        catch (JMSException e) {
            throw new JellyTagException(e);
        }
    }
    
    protected Message createMessage() throws JellyTagException {
        try {
            return getConnection().createMessage();
        } catch (JMSException e) {
            throw new JellyTagException(e);
        }
    }    
}    
