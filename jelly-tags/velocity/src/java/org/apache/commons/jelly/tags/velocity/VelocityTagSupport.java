package org.apache.commons.jelly.tags.velocity;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Maven" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Maven", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
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
 * ====================================================================
 */

import org.apache.commons.jelly.TagSupport;
import org.apache.velocity.app.VelocityEngine;

/** 
 * Support methods for the Velocity tag library.  Currently this is only
 * used to get an instance of the VelocityEngine.  For each unique base
 * directory specified, a new VelocityEngine instance is stored in the
 * context (as the author hasn't figured out how to change the resource
 * loader of an already init'd VelocityEngine).
 * 
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 * @version $Id: VelocityTagSupport.java,v 1.1 2003/01/07 03:33:31 dion Exp $
 */
public abstract class VelocityTagSupport extends TagSupport
{
    /** The VelocityEngine variable name in the JellyContext.  */
    public static final String VELOCITY_ENGINE_VAR_NAME =
            "org.apache.maven.jelly.tags.velocity.VelocityEngine";

    /** 
     * Gets or creates a VelocityEngine if one doesn't already exist for
     * the specified base directory.  
     * 
     * @return A VelocityEngine with a file resource loader configured
     * for the specified base directory.
     */
    public VelocityEngine getVelocityEngine( String basedir ) throws Exception
    {
        VelocityEngine ve = ( VelocityEngine ) getContext().getVariable( 
                keyName( basedir ) );

        if ( ve == null )
        {
            ve = new VelocityEngine();
            ve.setProperty( VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this );
            ve.setProperty( VelocityEngine.FILE_RESOURCE_LOADER_PATH, basedir );
            ve.init();

            getContext().setVariable( keyName( basedir ), ve );
        }

        return ve;
    }

    /** 
     * Constructs the name of the key used to reference the
     * VelocityEngine for the specified base directory.
     * 
     * @param basedir The base directory used by the VelocityEngine
     * @return The key used to reference the VelocityEngine that has
     * been initialized with the specified base directory.
     */
    private String keyName( String basedir )
    {
        return new StringBuffer()
            .append( VELOCITY_ENGINE_VAR_NAME )
            .append( '.' )
            .append( basedir )
            .toString();
    }
}
