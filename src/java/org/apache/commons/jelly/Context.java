/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/Attic/Context.java,v 1.10 2002/04/26 12:20:12 jstrachan Exp $
 * $Revision: 1.10 $
 * $Date: 2002/04/26 12:20:12 $
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
 * $Id: Context.java,v 1.10 2002/04/26 12:20:12 jstrachan Exp $
 */
package org.apache.commons.jelly;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.jelly.parser.XMLParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** <p><code>Context</code> represents the Jelly context.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.10 $
  */
public class Context {

    /** The root URL context (where scripts are located from) */
    private URL rootContext;
    
    /** The current URL context (where relative scripts are located from) */
    private URL currentContext;
    
    /** Tag libraries found so far */
    private Map taglibs = new Hashtable();
    
    /** synchronized access to the variables in scope */
    private Map variables = new Hashtable();

    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog( Context.class );

    public Context() {
        this.currentContext = rootContext;
    }
    
    public Context(URL rootContext) {
        this.rootContext = rootContext;
        this.currentContext = rootContext;
    }
    
    public Context(URL rootContext, URL currentContext) {
        this.rootContext = rootContext;
        this.currentContext = currentContext;
    }
    
    public Context(Context parentContext) {
        this.rootContext = parentContext.rootContext;
        this.currentContext = parentContext.currentContext;
        this.taglibs = parentContext.taglibs;
        this.variables.put( "parentScope", parentContext.variables );
    }
    
    public Context(Context parentContext, URL currentContext) {
        this( parentContext );
        this.currentContext = currentContext;
    }
        
    /** @return the value of the given variable name */
    public Object getVariable( String name ) {
        return variables.get( name );
    }
    
    /** Sets the value of the given variable name */
    public void setVariable( String name, Object value ) {
        if ( value == null ) {
            variables.remove( name );
        }
        else {
            variables.put( name, value );
        }
    }    

    /** Removes the given variable */
    public void removeVariable( String name ) {
        variables.remove( name );
    }
    
    /** 
     * @return an Iterator over the current variable names in this
     * context 
     */
    public Iterator getVariableNames() {
        return variables.keySet().iterator();
    }
    
    /**
     * @return the Map of variables in this scope
     */
    public Map getVariables() {
        return variables;
    }
    
    /**
     * Sets the Map of variables to use
     */
    public void setVariables(Map variables) {
        this.variables = variables;
    }
    
    
    /**
     * A factory method to create a new child context of the
     * current context.
     */
    public Context newContext(Map newVariables) {
        // XXXX: should allow this new context to
        // XXXX: inherit parent contexts? 
        // XXXX: Or at least publish the parent scope
        // XXXX: as a Map in this new variable scope?
        newVariables.put( "parentScope", variables );
        Context answer = new Context( this );
        answer.setVariables( newVariables );
        return answer;
    }
    
    
    /** Registers the given tag library against the given namespace URI.
     * This should be called before the parser is used.
     */
    public void registerTagLibrary(String namespaceURI, TagLibrary taglib) {
        log.info( "Registering tag library to: " + namespaceURI + " taglib: " + taglib );
        
        taglibs.put( namespaceURI, taglib );
    }
    
    /** Registers the given tag library class name against the given namespace URI.
     * The class will be loaded via the given ClassLoader
     * This should be called before the parser is used.
     */
    public void registerTagLibrary(String namespaceURI, String className, ClassLoader classLoader) {
        try {
            Class theClass = classLoader.loadClass( className );
            Object object = theClass.newInstance();
            if ( object instanceof TagLibrary ) {
                registerTagLibrary( namespaceURI, (TagLibrary) object );
            }
            else {
                log.error( 
                    "The tag library object mapped to: " 
                    + namespaceURI + " is not a TagLibrary. Object = " + object 
                );
            }
        }
        catch (ClassNotFoundException e) {
            log.error( "Could not find the class: " + className, e );
        }
        catch (Exception e) {
            log.error( "Could not instantiate instance of class: " + className + ". Reason: " + e, e );
        }
    }
    
    /** 
     * @return the TagLibrary for the given namespace URI or null if one could not be found
     */
    public TagLibrary getTagLibrary(String namespaceURI) {
        return (TagLibrary) taglibs.get( namespaceURI );
    }


    /** 
     * Attempts to parse the script from the given uri using the 
     * {#link getResource()} method then returns the compiled script.
     */
    public Script compileScript(String uri) throws Exception {
        XMLParser parser = new XMLParser();
        parser.setContext( this );
        
        InputStream in = getResourceAsStream( uri );
        if ( in == null ) {
            throw new JellyException( "Could not find Jelly script: " + uri );
        }
        Script script = parser.parse( in );
        return script.compile();
    }

    /** 
     * Attempts to parse the script from the given URL using the 
     * {#link getResource()} method then returns the compiled script.
     */
    public Script compileScript(URL url) throws Exception {
        XMLParser parser = new XMLParser();
        parser.setContext( this );
        
        Script script = parser.parse( url.toString() );
        return script.compile();
    }

    /** 
     * Attempts to parse the script from the given uri using the 
     * Context.getResource() API then compiles it and runs it.
     */
    public void runScript(String uri, XMLOutput output) throws Exception {
        URL url = getResource(uri);
        if ( url == null ) {
            throw new JellyException( "Could not find Jelly script: " + url );
        }
        Script script = compileScript( url );
        
        URL newContextURL = getContextURL( url );
        Context newContext = new Context( this, newContextURL );
        script.run( newContext, output );
    }

    /**
     * Returns a URL for the given resource from the specified path.
     * If the uri starts with "/" then the path is taken as relative to 
     * the current context root. If the uri is a well formed URL then it
     * is used. Otherwise the uri is interpreted as relative to the current
     * context (the location of the current script).
     */
    public URL getResource(String uri) throws MalformedURLException {
        if ( uri.startsWith( "/" ) ) {
            // append this uri to the context root
            return createRelativeURL( rootContext, uri.substring(1) );
        }
        else {
            try {
                return new URL( uri );
            }
            catch (MalformedURLException e) {
                // lets try find a relative resource
                try {
                    return createRelativeURL( currentContext, uri );
                }
                catch (MalformedURLException e2) {
                    throw e;
                }
            }
        }
    }
    
    /**
     * Attempts to open an InputStream to the given resource at the specified path.
     * If the uri starts with "/" then the path is taken as relative to 
     * the current context root. If the uri is a well formed URL then it
     * is used. Otherwise the uri is interpreted as relative to the current
     * context (the location of the current script).
     *
     * @return null if this resource could not be loaded, otherwise the resources 
     *  input stream is returned.
     */
    public InputStream getResourceAsStream(String uri) {
        try {
            URL url = getResource( uri );
            return url.openStream();
        }
        catch (Exception e) {
            if ( log.isTraceEnabled() ) {
                log.trace( "Caught exception attempting to open: " + uri + ". Exception: " + e, e );
            }
            return null;
        }
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                
    
    /**
     * @return a new relative URL from the given root and with the addition of the
     * extra relative URI
     *
     * @param rootURL is the root context from which the relative URI will be applied
     * @param relativeURI is the relative URI (without a leading "/")
     * @throws MalformedURLException if the URL is invalid.
     */
    protected URL createRelativeURL(URL rootURL, String relativeURI) throws MalformedURLException {
        String urlText = null;
        if ( rootURL == null ) {
            String userDir = System.getProperty( "user.dir" );
            urlText = "file://" + userDir + relativeURI; 
        }
        else {
            urlText = rootURL.toString() + relativeURI;
        }
        log.info( "Attempting to open url: " + urlText );
        return new URL( urlText );
    }
    
    /** 
     * Strips off the name of a script to create a new context URL
     */
    protected URL getContextURL( URL url ) throws MalformedURLException {
        String text = url.toString();
        int idx = text.lastIndexOf( '/' );
        text = text.substring( 0, idx + 1 );
        return new URL( text );
    }
}
