/*
 * $Header: /home/cvs/jakarta-commons/latka/src/java/org/apache/commons/latka/jelly/ResponseCodeTag.java,v 1.3 2002/07/14 12:38:22 dion Exp $
 * $Revision: 1.3 $
 * $Date: 2002/07/14 12:38:22 $
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

package org.apache.commons.jelly.tags.jetty;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import org.mortbay.http.HttpResponse;

/**
 * Set the response code in the request handler of a Jetty http server
 *
 * @author  rtl
 */
public class ResponseCodeTag extends TagSupport {

    /** parameter value */
    private int _value;

    /**
     * Perform the tag functionality. In this case, set the response code in the
     * http response found in the jelly context
     *
     * @param xmlOutput where to send output
     * @throws Exception when an error occurs
     */
    public void doTag(XMLOutput xmlOutput) throws Exception {

        if (getValue() <= 100) {
            throw new JellyException("<responseCode> tag must have a value of at least 100");
        }

        // get the response from the context
        HttpResponse httpResponse = (HttpResponse) getContext().getVariable("response");
        if (null == httpResponse) {
            throw new JellyException("HttpResponse variable not available in Jelly context");
        }

        // set response code
        httpResponse.setStatus(getValue());

    }

    //--------------------------------------------------------------------------
    // Property accessors/mutators
    //--------------------------------------------------------------------------

    /**
     * Getter for property value.
     *
     * @return value of property value.
     */
    public int getValue() {
        return _value;
    }

    /**
     * Setter for property value.
     *
     * @param value New value of property value.
     */
    public void setValue(int value) {
        _value = value;
    }

}

