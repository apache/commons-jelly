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

import javax.jms.MessageListener;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.messenger.tool.StopWatchMessageListener;

/** 
 * This tag can be used to measure the amount of time it takes to process JMS messages.
 * This tag can be wrapped around any custom JMS tag which consumes JMS messages.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.1 $
 */
public class StopwatchTag extends MessageOperationTag implements ConsumerTag {

    /** the underlying MessageListener */
    private MessageListener messageListener;
        
    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog( StopwatchTag.class );
    
    /** the message group size */
    private int groupSize = 1000;

    public StopwatchTag() {
    }
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws Exception {

        // evaluate body as it may contain child tags to register a MessageListener
        invokeBody(output);
        
        MessageListener listener = getMessageListener();

		ConsumerTag tag = (ConsumerTag) findAncestorWithClass(ConsumerTag.class);
		if (tag == null) {
			throw new JellyException("This tag must be nested within a ConsumerTag like the subscribe tag");
		}			

        // clear the listener for the next tag invocation, if caching is employed
        setMessageListener(null);

		StopWatchMessageListener stopWatch = new StopWatchMessageListener(listener);
		stopWatch.setGroupSize(groupSize);
		stopWatch.setLog(log);

		// perform the consumption
		tag.setMessageListener(stopWatch);		
    }
    
    // Properties
    //-------------------------------------------------------------------------   

    /**
     * @return the number of messages in the group before the performance statistics are logged
     */
    public int getGroupSize() {
        return groupSize;
    }    
        
    /**
     * Sets the number of messages in the group before the performance statistics are logged
     */
    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }    
    
    
    /**
     * @return the logger to which statistic messages will be sent
     */
    public Log getLog() {
        return log;
    }
    
    /**
     * Sets the logger to which statistic messages will be sent
     */
    public void setLog(Log log) {
        this.log = log;
    }
        
    /**
     * @return the MessageListener which this listener delegates to
     */
    public MessageListener getMessageListener() {
        return messageListener;    
    }
    
    /**
     * Sets the JMS messageListener used to consume JMS messages on the given destination
     */
    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

}    
