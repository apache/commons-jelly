/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/validate/src/java/org/apache/commons/jelly/tags/validate/VerifierTag.java,v 1.2 2003/01/26 08:44:58 morgand Exp $
 * $Revision: 1.2 $
 * $Date: 2003/01/26 08:44:58 $
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
 * $Id: VerifierTag.java,v 1.2 2003/01/26 08:44:58 morgand Exp $
 */
package org.apache.commons.jelly.tags.validate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.SAXException;

/** 
 * This tag creates a new Verifier of a schema as a variable
 * so that it can be used by a &lt;validate&gt; tag.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.2 $
 */
public class VerifierTag extends TagSupport {

    /** the variable name to export the Verifier as */
    private String var;
    
    /** The URI to load the schema from */
    private String uri;
    
    /** The system ID to use when parsing the schema */
    private String systemId;

    /** The factory used to create new schema verifier objects */
    private VerifierFactory factory;
    
    // Tag interface
    //-------------------------------------------------------------------------                    
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {    
        if ( var == null ) {
            throw new MissingAttributeException("var");
        }

        if ( factory == null ) {
            factory = new com.sun.msv.verifier.jarv.TheFactoryImpl();
        }
        InputStream in = null;
        if ( uri != null ) {
            in = context.getResourceAsStream( uri );
            if ( in == null ) {
                throw new JellyTagException( "Could not find resource for uri: " + uri );
            }
        }
        else {
            String text = getBodyText();
            byte[] data = text.getBytes();
            in = new ByteArrayInputStream( text.getBytes() );
        }

        Verifier verifier = null;
        try {
            Schema schema = null;
            if (systemId != null) {
                schema = factory.compileSchema(in, systemId);
            }
            else if ( uri != null ) {
                schema = factory.compileSchema(in, uri);
            }
            else{
                schema = factory.compileSchema(in);
            }
            
            if ( schema == null ) {
                throw new JellyTagException( "Could not create a valid schema" );
            }

            verifier = schema.newVerifier();
        } 
        catch (VerifierConfigurationException e) {
            throw new JellyTagException(e);
        }
        catch (SAXException e) {
            throw new JellyTagException(e);
        } 
        catch (IOException e) {
            throw new JellyTagException(e);
        }
        
        context.setVariable(var, verifier);
    }
    
    // Properties
    //-------------------------------------------------------------------------                    

    /** 
     * Sets the name of the variable that will be set to the new Verifier
     * 
     * @jelly:required
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * Sets the URI of the schema file to parse. If no URI is specified then the
     * body of this tag is used as the source of the schema
     * 
     * @jelly:optional
     */    
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Sets the system ID used when parsing the schema
     * 
     * @jelly:optional
     */    
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /** 
     * Sets the factory used to create new schema verifier objects.
     * If none is provided then the default MSV factory is used.
     * 
     * @jelly:optional
     */
    public void setFactory(VerifierFactory factory) {
        this.factory = factory;
    }
        
    // Implementation methods
    //-------------------------------------------------------------------------                    
    

}
