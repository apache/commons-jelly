/*
 * $Header: /home/cvs/jakarta-commons/latka/src/java/org/apache/commons/latka/jelly/BodyTag.java,v 1.3 2002/07/14 16:44:10 dion Exp $
 * $Revision: 1.3 $
 * $Date: 2002/07/14 16:44:10 $
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
 */

package org.apache.commons.jelly.tags.http;

import java.net.MalformedURLException;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * A tag to set the body for posts and puts etc
 *
 * @author  dion
 * @version $Id: BodyTag.java,v 1.3 2002/07/14 16:44:10 dion Exp $
 */
public class BodyTag extends TagSupport {
    
    /** Creates a new instance of BodyTag */
    public BodyTag() {
    }
    
    /**
     * Perform the tag functionality. In this case, get the parent http tag,
     * and if it's a post or put, set the request body from the body of this
     * tag.
     *
     * @param xmlOutput for writing output to
     * @throws Exception when any error occurs
     */
    public void doTag(XMLOutput xmlOutput) throws JellyTagException {
        HttpTagSupport httpTag = (HttpTagSupport) findAncestorWithClass(
            HttpTagSupport.class);
        
        HttpMethod httpMethod = null;
        try {
            httpMethod = httpTag.getHttpMethod();
        } catch (MalformedURLException e) {
            throw new JellyTagException(e);
        }
        
        String bodyText = getBodyText();
        if (httpMethod instanceof PostMethod) {
            PostMethod postMethod = (PostMethod) httpMethod;
            postMethod.setRequestBody(bodyText);
        } else if (httpMethod instanceof PutMethod) {
            PutMethod putMethod = (PutMethod) httpMethod;
            putMethod.setRequestBody(bodyText);
        } else {
            throw new IllegalStateException("Http method from parent was "
                + "not post or put");
        }
    }
    
}
