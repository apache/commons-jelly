/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
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
 */
package org.apache.commons.jelly.tags.ojb;

import ojb.broker.PersistenceBroker;
import ojb.broker.PersistenceBrokerException;
import ojb.broker.PersistenceBrokerFactory;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * <p>Tag handler for &lt;Driver&gt; in JSTL, used to create
 * a simple DataSource for prototyping.</p>
 * 
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.4 $
 */
public class BrokerTag extends TagSupport {
    
    /** The variable name to export. */
    private String var;

    /** The persistence broker instance */
    private PersistenceBroker broker;
    
    public BrokerTag() {
    }


    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws Exception {
        if ( var == null ) {
            var = "org.apache.commons.jelly.ojb.Broker";
        }
        if ( broker != null ) {
            context.setVariable(var, broker);            
            getBody().run(context, output);                
        }
        else {
            broker = PersistenceBrokerFactory.createPersistenceBroker();            
            context.setVariable(var, broker);            
            
            try {
                getBody().run(context, output);                
            }
            finally {            
                broker.clearCache();
                PersistenceBrokerFactory.releaseInstance(broker);
                broker = null;
                context.removeVariable(var);            
            }
        }
    }

    // Properties
    //-------------------------------------------------------------------------                
    /** Sets the variable name to define for this expression
     */
    public void setVar(String var) {
        this.var = var;
    }
    
    /** @return the persistence broker instance */
    public PersistenceBroker getBroker() {
        return broker;
    }
    
    /** Sets the persistence broker instance */
    public void setBroker(PersistenceBroker broker) {
        this.broker = broker;
    }    
}
