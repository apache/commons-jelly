/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/jms/src/java/org/apache/commons/jelly/tags/jms/OnMessageTag.java,v 1.2 2003/01/26 06:24:47 morgand Exp $
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
 * $Id: OnMessageTag.java,v 1.2 2003/01/26 06:24:47 morgand Exp $
 */
package org.apache.commons.jelly.tags.jms;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * This tag creates a JMS MessageListener which will invoke this
 * tag's body whenever a JMS Message is received. The JMS Message
 * will be available via a variable, which defaults to the 'message'
 * variable name, but can be overloaded by the var attribute.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.2 $
 */
public class OnMessageTag extends TagSupport {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(OnMessageTag.class);

	private String var = "message";
	
    public OnMessageTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(XMLOutput output) throws JellyTagException {
		ConsumerTag tag = (ConsumerTag) findAncestorWithClass(ConsumerTag.class);
		if (tag == null) {
			throw new JellyTagException("This tag must be nested within a ConsumerTag like the subscribe tag");
		}			


		final JellyContext childContext = context.newJellyContext();
		final Script script = getBody();
		final XMLOutput childOutput = output; 
		
		MessageListener listener = new MessageListener() {
			public void onMessage(Message message) {
				childContext.setVariable(var, childContext);
				try {
					script.run(childContext, childOutput);
				}
				catch (Exception e) {
					log.error("Caught exception processing message: " + message + ". Exception: " + e, e);
				}
			}
		};

		// perform the consumption
		tag.setMessageListener(listener);		
    }

    
    // Properties
    //-------------------------------------------------------------------------                    

    /**
     * Sets the name of the variable used to make the JMS message available to this tags
     * body when a message is received.
     */
    public void setVar(String var) {
		this.var = var;						
    }
}
