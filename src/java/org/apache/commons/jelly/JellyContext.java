/*
 * Copyright 2002,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.jelly.parser.XMLParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.SAXException;

/** <p><code>JellyContext</code> represents the Jelly context.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.10 $
  */
public class JellyContext {

    /** The Log to which logging calls will be made. */
    private static Log log = LogFactory.getLog(JellyContext.class);

    /** The root URL context (where scripts are located from) */
    private URL rootURL;

    /** The current URL context (where relative scripts are located from) */
    private URL currentURL;

    /** Tag libraries found so far */
    private Map taglibs = new Hashtable();

    /** synchronized access to the variables in scope */
    private Map variables = new Hashtable();

    /** The parent context */
    private JellyContext parent;

    /** Default for inheritance of variables **/
    private static boolean DEFAULT_INHERIT = true;

    /** Do we inherit variables from parent context? */
    private boolean inherit = JellyContext.DEFAULT_INHERIT;
    
    /** Default for export of variables **/
    private static boolean DEFAULT_EXPORT = false;

    /** Do we export our variables to parent context? */
    private boolean export  = JellyContext.DEFAULT_EXPORT;

    /** Should we export tag libraries to our parents context */
    private boolean exportLibraries = true;
        
    /** Should we cache Tag instances, per thread, to reduce object contruction overhead? */
    private boolean cacheTags = false;
    
    /**
     * The class loader to use for instantiating application objects.
     * If not specified, the context class loader, or the class loader
     * used to load this class itself, is used, based on the value of the
     * <code>useContextClassLoader</code> variable.
     */
    protected ClassLoader classLoader;

    /**
     * Do we want to use the Context ClassLoader when loading classes
     * for instantiating new objects?  Default is <code>false</code>.
     */
    protected boolean useContextClassLoader = false;


    public JellyContext() {
        this.currentURL = rootURL;
        init();
    }

    public JellyContext(URL rootURL) {
        this( rootURL, rootURL );
    }

    public JellyContext(URL rootURL, URL currentURL) {
        this.rootURL = rootURL;
        this.currentURL = currentURL;
        init();
    }

    public JellyContext(JellyContext parent) {
        this.parent = parent;
        this.rootURL = parent.rootURL;
        this.currentURL = parent.currentURL;
        this.variables.put("parentScope", parent.variables);
        this.cacheTags = parent.cacheTags;
        init();
    }

    public JellyContext(JellyContext parentJellyContext, URL currentURL) {
        this(parentJellyContext);
        this.currentURL = currentURL;
    }

    public JellyContext(JellyContext parentJellyContext, URL rootURL, URL currentURL) {
        this(parentJellyContext, currentURL);
        this.rootURL = rootURL;
    }

	private void init() {
		variables.put("context",this);
		try {
			variables.put("systemScope", System.getProperties() );
		} catch (SecurityException e) {
			// ignore security exceptions
		}
	}
    
    /**
     * @return the parent context for this context
     */
    public JellyContext getParent() {
        return parent;
    }

    /**
     * @return the scope of the given name, such as the 'parent' scope.
     * If Jelly is used in a Servlet situation then 'request', 'session' and 'application' are other names 
     * for scopes
     */
    public JellyContext getScope(String name) {
        if ( "parent".equals( name ) ) {
            return getParent();
        }
        return null;
    }
    
    /** 
     * Finds the variable value of the given name in this context or in any other parent context.
     * If this context does not contain the variable, then its parent is used and then its parent 
     * and so forth until the context with no parent is found.
     * 
     * @return the value of the variable in this or one of its descendant contexts or null
     *  if the variable could not be found.
     */
    public Object findVariable(String name) {
        Object answer = variables.get(name);
        if ( answer == null && parent != null ) {
            answer = parent.findVariable(name);
        }
        // ### this is a hack - remove this when we have support for pluggable Scopes
        if ( answer == null ) {
            try {
                answer = System.getProperty(name);
            }
            catch (SecurityException e) {
                // ignore security exceptions
            }
        }
        
        if (log.isDebugEnabled()) {
            log.debug("findVariable: " + name + " value: " + answer );
        }
        return answer;
    }
            
        
    /** @return the value of the given variable name */
    public Object getVariable(String name) {
        Object value = variables.get(name);

        if ( value == null && isInherit() ) {
            JellyContext parent = getParent();
            if (parent != null) {                
                value = parent.getVariable( name );
            }
        }

        return value;
    }
    
    /** 
     * @return the value of the given variable name in the given variable scope 
     * @param name is the name of the variable
     * @param scopeName is the optional scope name such as 'parent'. For servlet environments
     * this could be 'application', 'session' or 'request'.
     */
    public Object getVariable(String name, String scopeName) {
        JellyContext scope = getScope(scopeName);
        if ( scope != null ) {
            return scope.getVariable(name);
        }
        return null;
    }
    
    

    /** Sets the value of the given variable name */
    public void setVariable(String name, Object value) {
        if ( isExport() ) {
            getParent().setVariable( name, value );
            return;
        }
        if (value == null) {
            variables.remove(name);
        }
        else {
            variables.put(name, value);
        }
    }

    /** 
     * Sets the value of the given variable name in the given variable scope 
     * @param name is the name of the variable
     * @param scopeName is the optional scope name such as 'parent'. For servlet environments
     *  this could be 'application', 'session' or 'request'.
     * @param value is the value of the attribute
     */
    public void setVariable(String name, String scopeName, Object value) {
        JellyContext scope = getScope(scopeName);
        if ( scope != null ) {
            scope.setVariable(name, value);
        }
    }
    
    /** Removes the given variable */
    public void removeVariable(String name) {
        variables.remove(name);
    }

    /** 
     * Removes the given variable in the specified scope.
     * 
     * @param name is the name of the variable
     * @param scopeName is the optional scope name such as 'parent'. For servlet environments
     *  this could be 'application', 'session' or 'request'.
     */
    public void removeVariable(String name, String scopeName) {
        JellyContext scope = getScope(scopeName);
        if ( scope != null ) {
            scope.removeVariable(name);
        }
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
        // I have seen this fail when the passed Map contains a key, value 
        // pair where the value is null
        for (Iterator iter = variables.entrySet().iterator(); iter.hasNext();) {
            Map.Entry element = (Map.Entry) iter.next();
            if (element.getValue() != null) {
                this.variables.put(element.getKey(), element.getValue());
            }
        }
        //this.variables.putAll( variables );
    }

    /**
     * A factory method to create a new child context of the
     * current context.
     */
    public JellyContext newJellyContext(Map newVariables) {
        // XXXX: should allow this new context to
        // XXXX: inherit parent contexts? 
        // XXXX: Or at least publish the parent scope
        // XXXX: as a Map in this new variable scope?
        newVariables.put("parentScope", variables);
        JellyContext answer = createChildContext();
        answer.setVariables(newVariables);
        return answer;
    }

    /**
     * A factory method to create a new child context of the
     * current context.
     */
    public JellyContext newJellyContext() {
        return createChildContext();
    }

    /** Registers the given tag library against the given namespace URI.
     * This should be called before the parser is used.
     */
    public void registerTagLibrary(String namespaceURI, TagLibrary taglib) {
        if (log.isDebugEnabled()) {
            log.debug("Registering tag library to: " + namespaceURI + " taglib: " + taglib);
        }
        taglibs.put(namespaceURI, taglib);
        
        if (isExportLibraries() && parent != null) {
            parent.registerTagLibrary( namespaceURI, taglib );
        }
    }

    /** Registers the given tag library class name against the given namespace URI.
     * The class will be loaded via the given ClassLoader
     * This should be called before the parser is used.
     */
    public void registerTagLibrary(
        String namespaceURI,
        String className) {
            
        if (log.isDebugEnabled()) {
            log.debug("Registering tag library to: " + namespaceURI + " taglib: " + className);
        }
        taglibs.put(namespaceURI, className);
        
        if (isExportLibraries() && parent != null) {
            parent.registerTagLibrary( namespaceURI, className );
        }
    }

    public boolean isTagLibraryRegistered(String namespaceURI) {
        boolean answer = taglibs.containsKey( namespaceURI );
        if (answer) {
            return true;
        }
        else if ( parent != null ) {
            return parent.isTagLibraryRegistered(namespaceURI);
        }
        else {
            return false;
        }
    }

    /** 
     * @return the TagLibrary for the given namespace URI or null if one could not be found
     */
    public TagLibrary getTagLibrary(String namespaceURI) {

        // use my own mapping first, so that namespaceURIs can 
        // be redefined inside child contexts...
        
        Object answer = taglibs.get(namespaceURI);

        if ( answer == null && parent != null ) {
            answer = parent.getTagLibrary( namespaceURI );
        }

        if ( answer instanceof TagLibrary ) {
            return (TagLibrary) answer;
        }
        else if ( answer instanceof String ) {
            String className = (String) answer;
            Class theClass = null;
            try {
                theClass = getClassLoader().loadClass(className);
            }
            catch (ClassNotFoundException e) {
                log.error("Could not find the class: " + className, e);
            }
            if ( theClass != null ) {
                try {
                    Object object = theClass.newInstance();
                    if (object instanceof TagLibrary) {
                        taglibs.put(namespaceURI, object);
                        return (TagLibrary) object;
                    }                
                    else {
                        log.error(
                            "The tag library object mapped to: "
                                + namespaceURI
                                + " is not a TagLibrary. Object = "
                                + object);
                    }
                }
                catch (Exception e) {
                    log.error(
                        "Could not instantiate instance of class: " + className + ". Reason: " + e,
                        e);
                }
            }
        }

        return null;
    }

    /** 
     * Attempts to parse the script from the given uri using the 
     * {@link #getResource} method then returns the compiled script.
     */
    public Script compileScript(String uri) throws JellyException {
        XMLParser parser = getXMLParser();
        parser.setContext(this);
        InputStream in = getResourceAsStream(uri);
        if (in == null) {
            throw new JellyException("Could not find Jelly script: " + uri);
        }
        Script script = null;
        try {
            script = parser.parse(in);
        } catch (IOException e) {
            throw new JellyException("Could not parse Jelly script",e);
        } catch (SAXException e) {
            throw new JellyException("Could not parse Jelly script",e);
        }
        
        return script.compile();
    }

    /** 
     * Attempts to parse the script from the given URL using the 
     * {@link #getResource} method then returns the compiled script.
     */
    public Script compileScript(URL url) throws JellyException {
        XMLParser parser = getXMLParser();
        parser.setContext(this);
        
        Script script = null;
        try {
            script = parser.parse(url.toString());
        } catch (IOException e) {
            throw new JellyException("Could not parse Jelly script",e);
        } catch (SAXException e) {
            throw new JellyException("Could not parse Jelly script",e);
        }
        
        return script.compile();
    }

    /**
     * @return a thread pooled XMLParser to avoid the startup overhead
     * of the XMLParser
     */
    protected XMLParser getXMLParser() {
        XMLParser parser = createXMLParser();
        return parser;
    }

	/**
	 * Factory method to allow JellyContext implementations to overload how an XMLParser
	 * is created - such as to overload what the default ExpressionFactory should be.
	 */
    protected XMLParser createXMLParser() {
        return new XMLParser();
    }

    /** 
     * Parses the script from the given File then compiles it and runs it.
     * 
     * @return the new child context that was used to run the script
     */
    public JellyContext runScript(File file, XMLOutput output) throws JellyException {
        try {
            return runScript(file.toURL(), output, JellyContext.DEFAULT_EXPORT,
                JellyContext.DEFAULT_INHERIT);
        } catch (MalformedURLException e) {
            throw new JellyException(e.toString());
        }
    }

    /** 
     * Parses the script from the given URL then compiles it and runs it.
     * 
     * @return the new child context that was used to run the script
     */
    public JellyContext runScript(URL url, XMLOutput output) throws JellyException {
        return runScript(url, output, JellyContext.DEFAULT_EXPORT,
            JellyContext.DEFAULT_INHERIT);
    }

    /** 
     * Parses the script from the given uri using the 
     * JellyContext.getResource() API then compiles it and runs it.
     * 
     * @return the new child context that was used to run the script
     */
    public JellyContext runScript(String uri, XMLOutput output) throws JellyException {
        URL url = null;
        try {
            url = getResource(uri);
        } catch (MalformedURLException e) {
            throw new JellyException(e.toString());
        }
        
        if (url == null) {
            throw new JellyException("Could not find Jelly script: " + url);
        }
        return runScript(url, output, JellyContext.DEFAULT_EXPORT,
            JellyContext.DEFAULT_INHERIT);
    }

    /** 
     * Parses the script from the given uri using the 
     * JellyContext.getResource() API then compiles it and runs it.
     * 
     * @return the new child context that was used to run the script
     */
    public JellyContext runScript(String uri, XMLOutput output,
                          boolean export, boolean inherit) throws JellyException {
        URL url = null;
        try {
            url = getResource(uri);
        } catch (MalformedURLException e) {
            throw new JellyException(e.toString());
        }
        
        if (url == null) {
            throw new JellyException("Could not find Jelly script: " + url);
        }
        
        return runScript(url, output, export, inherit);
    }

    /** 
     * Parses the script from the given file then compiles it and runs it.
     * 
     * @return the new child context that was used to run the script
     */
    public JellyContext runScript(File file, XMLOutput output,
                          boolean export, boolean inherit) throws JellyException {
        try {
            return runScript(file.toURL(), output, export, inherit);
        } catch (MalformedURLException e) {
            throw new JellyException(e.toString());
        }
    }

    /** 
     * Parses the script from the given URL then compiles it and runs it.
     * 
     * @return the new child context that was used to run the script
     */
    public JellyContext runScript(URL url, XMLOutput output,
                          boolean export, boolean inherit) throws JellyException {
        Script script = compileScript(url);
        
        URL newJellyContextURL = null;
        try {
            newJellyContextURL = getJellyContextURL(url);
        } catch (MalformedURLException e) {
            throw new JellyException(e.toString());
        }
        
        JellyContext newJellyContext = newJellyContext();
        newJellyContext.setRootURL( newJellyContextURL );
        newJellyContext.setCurrentURL( newJellyContextURL );
        newJellyContext.setExport( export );
        newJellyContext.setInherit( inherit );
            
        if ( inherit ) {
            // use the same variable scopes
            newJellyContext.variables = this.variables;
        } 

        if (log.isDebugEnabled() ) {
            log.debug( "About to run script: " + url );
            log.debug( "root context URL: " + newJellyContext.rootURL );
            log.debug( "current context URL: " + newJellyContext.currentURL );
        }

        script.run(newJellyContext, output);
        
        return newJellyContext;
    }

    /**
     * Returns a URL for the given resource from the specified path.
     * If the uri starts with "/" then the path is taken as relative to 
     * the current context root.
     * If the uri is a well formed URL then it is used.
     * If the uri is a file that exists and can be read then it is used.
     * Otherwise the uri is interpreted as relative to the current context (the
     * location of the current script).
     */
    public URL getResource(String uri) throws MalformedURLException {
        if (uri.startsWith("/")) {
            // append this uri to the context root
            return createRelativeURL(rootURL, uri.substring(1));
        }
        else {
            try {
                return new URL(uri);
            }
            catch (MalformedURLException e) {
                // lets try find a relative resource
                try {
                    return createRelativeURL(currentURL, uri);
                } catch (MalformedURLException e2) {
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
            URL url = getResource(uri);
            return url.openStream();
        }
        catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.trace(
                    "Caught exception attempting to open: " + uri + ". Exception: " + e,
                    e);
            }
            return null;
        }
    }


    // Properties
    //-------------------------------------------------------------------------                

    /**
     * @return the current root context URL from which all absolute resource URIs
     *  will be relative to. For example in a web application the root URL will
     *  map to the web directory which contains the WEB-INF directory.
     */
    public URL getRootURL() {
        return rootURL;
    }
    
    /**
     * Sets the current root context URL from which all absolute resource URIs
     *  will be relative to. For example in a web application the root URL will
     *  map to the web directory which contains the WEB-INF directory.
     */
    public void setRootURL(URL rootURL) {
        this.rootURL = rootURL;
    }
    

    /** 
     * @return the current URL context of the current script that is executing. 
     *  This URL context is used to deduce relative scripts when relative URIs are
     *  used in calls to {@link #getResource} to process relative scripts.
     */ 
    public URL getCurrentURL() {
        return currentURL;
    }
    
    /** 
     * Sets the current URL context of the current script that is executing. 
     *  This URL context is used to deduce relative scripts when relative URIs are
     *  used in calls to {@link #getResource} to process relative scripts.
     */ 
    public void setCurrentURL(URL currentURL) { 
        this.currentURL = currentURL;
    }
    
    /**
     * Returns whether caching of Tag instances, per thread, is enabled.
     * Caching Tags can boost performance, on some JVMs, by reducing the cost of
     * object construction when running Jelly inside a multi-threaded application server
     * such as a Servlet engine.
     * 
     * @return whether caching of Tag instances is enabled.
     */
    public boolean isCacheTags() {
        return cacheTags;
    }

    /**
     * Sets whether caching of Tag instances, per thread, is enabled.
     * Caching Tags can boost performance, on some JVMs, by reducing the cost of
     * object construction when running Jelly inside a multi-threaded application server
     * such as a Servlet engine.
     * 
     * @param cacheTags Whether caching should be enabled or disabled.
     */
    public void setCacheTags(boolean cacheTags) {
        this.cacheTags = cacheTags;
    }
    
    /**
     * Returns whether we export tag libraries to our parents context
     * @return boolean
     */
    public boolean isExportLibraries() {
        return exportLibraries;
    }

    /**
     * Sets whether we export tag libraries to our parents context
     * @param exportLibraries The exportLibraries to set
     */
    public void setExportLibraries(boolean exportLibraries) {
        this.exportLibraries = exportLibraries;
    }
    

    /**
     * Sets whether we should export variable definitions to our parent context
     */
    public void setExport(boolean export) {
        this.export = export;
    }

    public boolean isExport() {
        return this.export;
    }

    /**
     * Sets whether we should inherit variables from our parent context
     */
    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    public boolean isInherit() {
        return this.inherit;
    }


    /**
     * Return the class loader to be used for instantiating application objects
     * when required.  This is determined based upon the following rules:
     * <ul>
     * <li>The class loader set by <code>setClassLoader()</code>, if any</li>
     * <li>The thread context class loader, if it exists and the
     *     <code>useContextClassLoader</code> property is set to true</li>
     * <li>The class loader used to load the XMLParser class itself.
     * </ul>
     */
    public ClassLoader getClassLoader() {
        if (this.classLoader != null) {
            return (this.classLoader);
        }
        if (this.useContextClassLoader) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                return (classLoader);
            }
        }
        return (this.getClass().getClassLoader());
    }
    
    /**
     * Set the class loader to be used for instantiating application objects
     * when required.
     *
     * @param classLoader The new class loader to use, or <code>null</code>
     *  to revert to the standard rules
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    /**
     * Return the boolean as to whether the context classloader should be used.
     */
    public boolean getUseContextClassLoader() {
        return useContextClassLoader;
    }

    /**
     * Determine whether to use the Context ClassLoader (the one found by
     * calling <code>Thread.currentThread().getContextClassLoader()</code>)
     * to resolve/load classes.  If not
     * using Context ClassLoader, then the class-loading defaults to
     * using the calling-class' ClassLoader.
     *
     * @param use determines whether to use JellyContext ClassLoader.
     */
    public void setUseContextClassLoader(boolean use) {
        useContextClassLoader = use;
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
    protected URL createRelativeURL(URL rootURL, String relativeURI)
        throws MalformedURLException {
        if (rootURL == null) {
            File file = new File(System.getProperty("user.dir"));
            rootURL = file.toURL();
        }
        String urlText = rootURL.toString() + relativeURI;
        if ( log.isDebugEnabled() ) {
            log.debug("Attempting to open url: " + urlText);
        }
        return new URL(urlText);
    }

    /** 
     * Strips off the name of a script to create a new context URL
     */
    protected URL getJellyContextURL(URL url) throws MalformedURLException {
        String text = url.toString();
        int idx = text.lastIndexOf('/');
        text = text.substring(0, idx + 1);
        return new URL(text);
    }

    /**
     * Factory method to create a new child of this context
     */
    protected JellyContext createChildContext() {
        return new JellyContext(this);
    }

    /**
     * Change the parent context to the one provided
     * @param context the new parent context
     */
    protected void setParent(JellyContext context)
    {
        parent = context;
        this.variables.put("parentScope", parent.variables);
        // need to re-export tag libraries to the new parent
        if (isExportLibraries() && parent != null) {
            for (Iterator keys = taglibs.keySet().iterator(); keys.hasNext();)
            {
                String namespaceURI = (String) keys.next();
                Object tagLibOrClassName = taglibs.get(namespaceURI);
                if (tagLibOrClassName instanceof TagLibrary)
                {
                    parent.registerTagLibrary( namespaceURI, (TagLibrary) tagLibOrClassName );
                }
                else
                {
                    parent.registerTagLibrary( namespaceURI, (String) tagLibOrClassName );
                }
            }
        }

    }

}
