/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/core/ImportTag.java,v 1.7 2003/09/04 01:54:27 proyal Exp $
 * $Revision: 1.7 $
 * $Date: 2003/09/04 01:54:27 $
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
 * $Id: ImportTag.java,v 1.7 2003/09/04 01:54:27 proyal Exp $
 */

package org.apache.commons.jelly.tags.core;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
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
 * @version $Revision: 1.7 $
 */

public class ImportTag extends TagSupport {

    /** 
     * the location of the script being imported, relative to the
     * current script
     */
    private String uri;
    
    /**
     * Whether the imported script has access to the caller's variables
     */
    private boolean inherit;
    
    /**
     * The file to be imported. Mutually exclusive with uri.
     * uri takes precedence.
     */
    private String file;

    /**
     * Create a new Import tag.
     */
    public ImportTag() {
    }


    // Tag interface
    //-------------------------------------------------------------------------
    /**
     * Perform tag processing
     * @param output the destination for output
     * @throws MissingAttributeException if a required attribute is missing
     * @throws JellyTagException on any other errors
     */ 
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (uri == null && file == null) {
            throw new MissingAttributeException( "uri" );
        }

        try {
            if (uri != null) {
                // we need to create a new JellyContext of the URI
                // take off the script name from the URL
                context.runScript(uri, output, true, isInherit() );
            } else {
                context.runScript(new java.io.File(file), output, true,
                  isInherit());
            }
        }
        catch (JellyException e) {
            throw new JellyTagException("could not import script",e);
        }
    }

    // Properties
    //-------------------------------------------------------------------------                

    /**
     * @return whether property inheritence is enabled
     */
    public boolean isInherit() {
        return inherit;
    }

    /**
     * Sets whether property inheritence is enabled or disabled
     */
    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    /** 
     * Sets the URI (relative URI or absolute URL) for the script to evaluate.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }


    /**
     * Sets the file for the script to evaluate.
     * @param file The file to set
     */
    public void setFile(String file) {
        this.file = file;
    }

}
