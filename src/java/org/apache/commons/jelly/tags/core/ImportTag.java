/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/core/ImportTag.java,v 1.1 2002/06/05 18:03:38 werken Exp $
 * $Revision: 1.1 $
 * $Date: 2002/06/05 18:03:38 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * $Id: ImportTag.java,v 1.1 2002/06/05 18:03:38 werken Exp $
 */

package org.apache.commons.jelly.tags.core;

import java.net.URL;

import org.apache.commons.jelly.JellyContext;

import org.apache.commons.jelly.MissingAttributeException;

import org.apache.commons.jelly.Script;

import org.apache.commons.jelly.TagSupport;

import org.apache.commons.jelly.XMLOutput;

/** Imports another script.
 *
 *  <p>
 *  By default, the imported script does not have access to
 *  the parent script's variable context.  This behaviour
 *  may be modified using the <code>inherit</code> attribute.
 *  </p>
 *
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @version $Revision: 1.1 $
 */

public class ImportTag extends TagSupport {

    private String uri;

    private boolean shouldInherit;

    public ImportTag() {
        this.shouldInherit = false;
    }

    public void setInherit(String inherit) {
        if ( "true".equals( inherit ) ) {
            this.shouldInherit = true;
        }
    }

    public boolean getInherit() {
        return this.shouldInherit;
    }


    // Tag interface

    //------------------------------------------------------------------------- 

    public void doTag(XMLOutput output) throws Exception {

        if (uri == null) {

            throw new MissingAttributeException( "uri" );

        }

        // we need to create a new JellyContext of the URI

        // take off the script name from the URL

        context.runScript(uri, output, true, getInherit() );
    }

    // Properties

    //-------------------------------------------------------------------------                

    /** Sets the URI (relative URI or absolute URL) for the script to evaluate. */

    public void setUri(String uri) {

        this.uri = uri;

    }

}
