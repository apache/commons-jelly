/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/jms/src/java/org/apache/commons/jelly/tags/jms/SubscribeTag.java,v 1.3 2003/10/09 21:21:20 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/09 21:21:20 $
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
 * $Id: SubscribeTag.java,v 1.3 2003/10/09 21:21:20 rdonkin Exp $
 */
package org.apache.commons.jelly.tags.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageListener;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Performs a subscription to some JMS connection to a destination maybe with a selector.
 * A JMS MessageListener can be specified, or a special child tag can explicitly set it on 
 * its parent (so a special tag could construct a MessageListener object and register it with this tag).
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.3 $
 */
public class SubscribeTag extends MessageOperationTag implements ConsumerTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(SubscribeTag.class);

    /** the JMS Selector for the subscription */
    private String selector;
    
    /** The JMS MessageListener used to create the subscription */
    private MessageListener messageListener;
    
    public SubscribeTag() {
    }
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws JellyTagException {

        // evaluate body as it may contain child tags to register a MessageListener
        invokeBody(output);
        
        MessageListener listener = getMessageListener();
        if (listener == null) {
            throw new JellyTagException( "No messageListener attribute is specified so could not subscribe" );
        }

        // clear the listener for the next tag invocation, if caching is employed
        setMessageListener(null);

        
        Destination destination = null;
        try {
            destination = getDestination();
        } 
        catch (JMSException e) {
            throw new JellyTagException(e);
        }
        
        if ( destination == null ) {
            throw new JellyTagException( "No destination specified. Either specify a 'destination' attribute or use a nested <jms:destination> tag" );
        }
        
        if ( log.isDebugEnabled() ) {
            log.debug( "About to consume to: " + destination + " with listener: " + listener );
        }
            
        log.info( "About to consume to: " + destination + " with listener: " + listener );
        
        try {
            if (selector == null ) {            
                getConnection().addListener( destination, listener );
            }
            else {
                getConnection().addListener( destination, selector, listener );
            }
        }
        catch (JMSException e) {
            throw new JellyTagException(e);
        }
    }
    
    // Properties
    //-------------------------------------------------------------------------   
    
    /**
     * Sets the optional JMS Message selector for the subscription
     */
    public void setSelector(String selector) {
        this.selector = selector;
    }                             
    

    /**
     * Returns the messageListener.
     * @return MessageListener
     */
    public MessageListener getMessageListener() {
        return messageListener;
    }

    
    /**
     * Sets the JMS messageListener used ot consume JMS messages on the given destination
     */
    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

}    
