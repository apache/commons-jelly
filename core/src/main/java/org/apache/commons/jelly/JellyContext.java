/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.jelly.parser.XMLParser;
import org.apache.commons.jelly.util.ClassLoaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
  * <p>{@code JellyContext} represents the Jelly context.</p>
  */
public class JellyContext {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(JellyContext.class);

    /** Default for inheritance of variables **/
    private static final boolean DEFAULT_INHERIT = true;

    /** Default for export of variables **/
    private static final boolean DEFAULT_EXPORT = false;

    /** Default for DTD calling out to external entities. */
    private static final boolean DEFAULT_ALLOW_DTD_CALLS_TO_EXTERNAL_ENTITIES = false;

    /** String used to denote a script can't be parsed */
    private static final String BAD_PARSE = "Could not parse Jelly script";

    /**
     * The class loader to use for instantiating application objects.
     * If not specified, the context class loader, or the class loader
     * used to load this class itself, is used, based on the value of the
     * {@code useContextClassLoader} variable.
     */
    protected ClassLoader classLoader;

    /**
     * Do we want to use the Context ClassLoader when loading classes
     * for instantiating new objects?  Default is {@code false}.
     */
    protected boolean useContextClassLoader = false;

    /** The root URL context (where scripts are located from) */
    private URL rootURL;

    /** The current URL context (where relative scripts are located from) */
    private URL currentURL;

    /** Tag libraries found so far */
    private final Map taglibs = new Hashtable();

    /** Synchronized access to the variables in scope */
    private Map variables = new Hashtable();

    /** The parent context */
    private JellyContext parent;

    /** Do we inherit variables from parent context? */
    private boolean inherit = JellyContext.DEFAULT_INHERIT;

    /** Do we export our variables to parent context? */
    private boolean export  = JellyContext.DEFAULT_EXPORT;

    /** Do we allow our doctype definitions to call out to external entities? */
    private boolean allowDtdToCallExternalEntities = JellyContext.DEFAULT_ALLOW_DTD_CALLS_TO_EXTERNAL_ENTITIES;

    /** Should we export tag libraries to our parents context */
    private boolean exportLibraries = true;

    /** Should we cache Tag instances, per thread, to reduce object construction overhead? */
    private boolean cacheTags = false;

    /**
     * True if exceptions should be suppressed; introduced in 1.1 beta and immediately deprecated
     * because future versions will _never_ suppress exceptions (required here for backwards
     * compatibility)
	 * @deprecated after v1.1, exceptions will never be suppressed
     */
    @Deprecated
    private boolean suppressExpressionExceptions;

    /**
     * Create a new context with the currentURL set to the rootURL
     */
    public JellyContext() {
        this.currentURL = rootURL;
        init();
    }

    /**
     * Create a new context with the given parent context.
     * The parent's rootURL and currentURL are set on the child, and the parent's variables are
     * available in the child context under the name {@code parentScope}.
     *
     * @param parent the parent context for the newly created context.
     */
    public JellyContext(final JellyContext parent) {
        this.parent = parent;
        this.rootURL = parent.rootURL;
        this.currentURL = parent.currentURL;
        this.variables.put("parentScope", parent.variables);
        this.cacheTags = parent.cacheTags;
        this.suppressExpressionExceptions = parent.suppressExpressionExceptions;
        init();
    }

    /**
     * Create a new context with the given parent context.
     * The parent's rootURL are set on the child, and the parent's variables are
     * available in the child context under the name {@code parentScope}.
     *
     * @param parentJellyContext the parent context for the newly created context.
     * @param currentURL the root URL used in resolving relative resources
     */
    public JellyContext(final JellyContext parentJellyContext, final URL currentURL) {
        this(parentJellyContext);
        this.currentURL = currentURL;
    }

    /**
     * Create a new context with the given parent context.
     * The parent's variables are available in the child context under the name {@code parentScope}.
     *
     * @param parentJellyContext the parent context for the newly created context.
     * @param rootURL the root URL used in resolving absolute resources i.e. those starting with '/'
     * @param currentURL the root URL used in resolving relative resources
     */
    public JellyContext(final JellyContext parentJellyContext, final URL rootURL, final URL currentURL) {
        this(parentJellyContext, currentURL);
        this.rootURL = rootURL;
    }

    /**
     * Create a new context with the given rootURL
     * @param rootURL the root URL used in resolving absolute resources i.e. those starting with '/'
     */
    public JellyContext(final URL rootURL) {
        this( rootURL, rootURL );
    }

    /**
     * Create a new context with the given rootURL and currentURL
     * @param rootURL the root URL used in resolving absolute resources i.e. those starting with '/'
     * @param currentURL the root URL used in resolving relative resources
     */
    public JellyContext(final URL rootURL, final URL currentURL) {
        this.rootURL = rootURL;
        this.currentURL = currentURL;
        init();
    }

    /** Clears variables set by Tags.
     * @see #clearVariables()
      */
    public void clear() {
        clearVariables();
    }

    /** Clears variables set by Tags (variables set while running a Jelly script)
     * @see #clear()
     */
    protected void clearVariables() {
        variables.clear();
    }

    /**
     * Attempts to parse the script from the given InputSource using the
     * {@link #getResource} method then returns the compiled script.
     */
    public Script compileScript(final InputSource source) throws JellyException {
        final XMLParser parser = getXMLParser();
        parser.setContext(this);

        Script script = null;
        try {
            script = parser.parse(source);
        } catch (final IOException | SAXException e) {
            throw new JellyException(JellyContext.BAD_PARSE, e);
        }

        return script.compile();
    }

    /**
     * Attempts to parse the script from the given uri using the
     * {@link #getResource} method then returns the compiled script.
     */
    public Script compileScript(final String uri) throws JellyException {
        final XMLParser parser = getXMLParser();
        parser.setContext(this);
        final InputStream in = getResourceAsStream(uri);
        if (in == null) {
            throw new JellyException("Could not find Jelly script: " + uri);
        }
        Script script = null;
        try {
            script = parser.parse(in);
        } catch (final IOException | SAXException e) {
            throw new JellyException(JellyContext.BAD_PARSE, e);
        }

        return script.compile();
    }

    /**
     * Attempts to parse the script from the given URL using the
     * {@link #getResource} method then returns the compiled script.
     */
    public Script compileScript(final URL url) throws JellyException {
        final XMLParser parser = getXMLParser();
        parser.setContext(this);

        Script script = null;
        try {
            script = parser.parse(url.toString());
        } catch (final IOException | SAXException e) {
            throw new JellyException(JellyContext.BAD_PARSE, e);
        }

        return script.compile();
    }

    /**
     * Factory method to create a new child of this context
     */
    protected JellyContext createChildContext() {
        return new JellyContext(this);
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
    protected URL createRelativeURL(final URL rootURL, final String relativeURI)
        throws MalformedURLException {
        URL url = rootURL;
        if (url == null) {
            final File file = new File(System.getProperty("user.dir"));
            url = file.toURL();
        }
        final String urlText = url.toString() + relativeURI;
        if ( log.isDebugEnabled() ) {
            log.debug("Attempting to open url: " + urlText);
        }
        return new URL(urlText);
    }

    /**
     * Factory method to allow JellyContext implementations to overload how an XMLParser
     * is created - such as to overload what the default ExpressionFactory should be.
     */
    protected XMLParser createXMLParser() {
        return new XMLParser(allowDtdToCallExternalEntities);
    }

    /**
     * Finds the variable value of the given name in this context or in any other parent context.
     * If this context does not contain the variable, then its parent is used and then its parent
     * and so forth until the context with no parent is found.
     *
     * @return the value of the variable in this or one of its descendant contexts or null
     *  if the variable could not be found.
     */
    public Object findVariable(final String name) {
        Object answer = variables.get(name);
        final boolean definedHere = answer != null || variables.containsKey(name);

        if (definedHere) {
            return answer;
        }

        if ( answer == null && parent != null ) {
            answer = parent.findVariable(name);
        }
        // ### this is a hack - remove this when we have support for pluggable Scopes
        if ( answer == null ) {
            answer = getSystemProperty(name);
        }

        if (log.isDebugEnabled()) {
            log.debug("findVariable: " + name + " value: " + answer );
        }
        return answer;
    }

    /**
     * Gets the class loader to be used for instantiating application objects
     * when required.  This is determined based upon the following rules:
     * <ul>
     * <li>The class loader set by {@code setClassLoader()}, if any</li>
     * <li>The thread context class loader, if it exists and the
     *     {@code useContextClassLoader} property is set to true</li>
     * <li>The class loader used to load the XMLParser class itself.</li>
     * </ul>
     */
    public ClassLoader getClassLoader() {
        return ClassLoaderUtils.getClassLoader(classLoader, useContextClassLoader, getClass());
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
     * Strips off the name of a script to create a new context URL
     */
    protected URL getJellyContextURL(final InputSource source) throws MalformedURLException {
        String text = source.getSystemId();
        if (text != null) {
            final int idx = text.lastIndexOf('/');
            text = text.substring(0, idx + 1);
            return new URL(text);
        }
        return null;

    }

    /**
     * Strips off the name of a script to create a new context URL
     */
    protected URL getJellyContextURL(final URL url) throws MalformedURLException {
        String text = url.toString();
        final int idx = text.lastIndexOf('/');
        text = text.substring(0, idx + 1);
        return new URL(text);
    }

    /**
     * @return the parent context for this context
     */
    public JellyContext getParent() {
        return parent;
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
    public URL getResource(final String uri) throws MalformedURLException {
        if (uri.startsWith("/")) {
            // append this uri to the context root
            return createRelativeURL(rootURL, uri.substring(1));
        }
        try {
            return new URL(uri);
        }
        catch (final MalformedURLException e) {
            // lets try find a relative resource
            try {
                return createRelativeURL(currentURL, uri);
            } catch (final MalformedURLException e2) {
                throw e;
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
    public InputStream getResourceAsStream(final String uri) {
        try {
            final URL url = getResource(uri);
            return url.openStream();
        }
        catch (final Exception e) {
            if (log.isTraceEnabled()) {
                log.trace(
                    "Caught exception attempting to open: " + uri + ". Exception: " + e,
                    e);
            }
            return null;
        }
    }

    /**
     * @return the current root context URL from which all absolute resource URIs
     *  will be relative to. For example in a web application the root URL will
     *  map to the web directory which contains the WEB-INF directory.
     */
    public URL getRootURL() {
        return rootURL;
    }

    /**
     * @return the scope of the given name, such as the 'parent' scope.
     * If Jelly is used in a Servlet situation then 'request', 'session' and 'application' are other names
     * for scopes
     */
    public JellyContext getScope(final String name) {
        if ( "parent".equals( name ) ) {
            return getParent();
        }
        return null;
    }

    /**
     * Gets a system property and handle security exceptions
     * @param name the name of the property to retrieve
     * @return the value of the property, or null if a SecurityException occurs
     */
    private Object getSystemProperty(final String name) {
        try {
            return System.getProperty(name);
        }
        catch (final SecurityException e) {
            log.debug("security exception accessing system properties", e);
        }
        return null;
    }

    /**
     * @return the TagLibrary for the given namespace URI or null if one could not be found
     */
    public TagLibrary getTagLibrary(final String namespaceURI) {

        // use my own mapping first, so that namespaceURIs can
        // be redefined inside child contexts...

        Object answer = taglibs.get(namespaceURI);

        if ( answer == null && parent != null ) {
            answer = parent.getTagLibrary( namespaceURI );
        }

        if ( answer instanceof TagLibrary ) {
            return (TagLibrary) answer;
        }
        if ( answer instanceof String ) {
            final String className = (String) answer;
            Class theClass = null;
            try {
                theClass = getClassLoader().loadClass(className);
            }
            catch (final ClassNotFoundException e) {
                log.error("Could not find the class: " + className, e);
            }
            if ( theClass != null ) {
                try {
                    final Object object = theClass.getConstructor().newInstance();
                    if (object instanceof TagLibrary) {
                        taglibs.put(namespaceURI, object);
                        return (TagLibrary) object;
                    }
                    log.error(
                        "The tag library object mapped to: "
                            + namespaceURI
                            + " is not a TagLibrary. Object = "
                            + object);
                }
                catch (final Exception e) {
                    log.error(
                        "Could not instantiate instance of class: " + className + ". Reason: " + e,
                        e);
                }
            }
        }

        return null;
    }

    /**
     * Gets the boolean as to whether the context classloader should be used.
     */
    public boolean getUseContextClassLoader() {
        return useContextClassLoader;
    }

    /** @return the value of the given variable name */
    public Object getVariable(final String name) {
        Object value = variables.get(name);
        final boolean definedHere = value != null || variables.containsKey(name);

        if (definedHere) {
            return value;
        }

        if ( value == null && isInherit() ) {
            final JellyContext parentContext = getParent();
            if (parentContext != null) {
                value = parentContext.getVariable( name );
            }
        }

        // ### this is a hack - remove this when we have support for pluggable Scopes
        if ( value == null ) {
            value = getSystemProperty(name);
        }

        return value;
    }

    /**
     * @return the value of the given variable name in the given variable scope
     * @param name is the name of the variable
     * @param scopeName is the optional scope name such as 'parent'. For servlet environments
     * this could be 'application', 'session' or 'request'.
     */
    public Object getVariable(final String name, final String scopeName) {
        final JellyContext scope = getScope(scopeName);
        if ( scope != null ) {
            return scope.getVariable(name);
        }
        return null;
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
     * @return a thread pooled XMLParser to avoid the startup overhead
     * of the XMLParser
     */
    protected XMLParser getXMLParser() {
        final XMLParser parser = createXMLParser();
        return parser;
    }

    /**
     * Initialize the context.
     * This includes adding the context to itself under the name {@code context} and
     * making the System Properties available as {@code systemScope}
     */
    private void init() {
        variables.put("context", this);
        try {
            variables.put("systemScope", System.getProperties());
        } catch (final SecurityException e) {
            log.debug("security exception accessing system properties", e);
        }
    }

    /**
     * @return whether we should allow our doctype definitions to call out to external entities.
     */
    public boolean isAllowDtdToCallExternalEntities() {
        return this.allowDtdToCallExternalEntities;
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
     * @return whether we should export variable definitions to our parent context
     */
    public boolean isExport() {
        return this.export;
    }

    /**
     * Returns whether we export tag libraries to our parents context
     * @return boolean
     */
    public boolean isExportLibraries() {
        return exportLibraries;
    }

    /**
     * @return whether we should inherit variables from our parent context
     */
    public boolean isInherit() {
        return this.inherit;
    }

    /**
	 * @return the suppressExpressionExceptions
	 * @deprecated after v1.1, exceptions will never be suppressed
	 */
	@Deprecated
    public boolean isSuppressExpressionExceptions() {
		return suppressExpressionExceptions;
	}

    public boolean isTagLibraryRegistered(final String namespaceURI) {
        final boolean answer = taglibs.containsKey( namespaceURI );
        if (answer) {
            return true;
        }
        if (parent != null) {
            return parent.isTagLibraryRegistered(namespaceURI);
        }
        return false;
    }

    /**
     * A factory method to create a new child context of the
     * current context.
     */
    public JellyContext newJellyContext() {
        return createChildContext();
    }

    /**
     * A factory method to create a new child context of the
     * current context.
     */
    public JellyContext newJellyContext(final Map newVariables) {
        // XXXX: should allow this new context to
        // XXXX: inherit parent contexts?
        // XXXX: Or at least publish the parent scope
        // XXXX: as a Map in this new variable scope?
        newVariables.put("parentScope", variables);
        final JellyContext answer = createChildContext();
        answer.setVariables(newVariables);
        return answer;
    }

    /** Registers the given tag library class name against the given namespace URI.
     * The class will be loaded via the given ClassLoader
     * This should be called before the parser is used.
     */
    public void registerTagLibrary(
        final String namespaceURI,
        final String className) {

        if (log.isDebugEnabled()) {
            log.debug("Registering tag library to: " + namespaceURI + " taglib: " + className);
        }
        taglibs.put(namespaceURI, className);

        if (isExportLibraries() && parent != null) {
            parent.registerTagLibrary( namespaceURI, className );
        }
    }

    // Properties
    //-------------------------------------------------------------------------

    /** Registers the given tag library against the given namespace URI.
     * This should be called before the parser is used.
     */
    public void registerTagLibrary(final String namespaceURI, final TagLibrary taglib) {
        if (log.isDebugEnabled()) {
            log.debug("Registering tag library to: " + namespaceURI + " taglib: " + taglib);
        }
        taglibs.put(namespaceURI, taglib);

        if (isExportLibraries() && parent != null) {
            parent.registerTagLibrary( namespaceURI, taglib );
        }
    }

    /** Removes the given variable */
    public void removeVariable(final String name) {
        variables.remove(name);
    }

    /**
     * Removes the given variable in the specified scope.
     *
     * @param name is the name of the variable
     * @param scopeName is the optional scope name such as 'parent'. For servlet environments
     *  this could be 'application', 'session' or 'request'.
     */
    public void removeVariable(final String name, final String scopeName) {
        final JellyContext scope = getScope(scopeName);
        if ( scope != null ) {
            scope.removeVariable(name);
        }
    }

    /**
     * Parses the script from the given File then compiles it and runs it.
     *
     * @return the new child context that was used to run the script
     */
    public JellyContext runScript(final File file, final XMLOutput output) throws JellyException {
        try {
            return runScript(file.toURL(), output, JellyContext.DEFAULT_EXPORT,
                JellyContext.DEFAULT_INHERIT);
        } catch (final MalformedURLException e) {
            throw new JellyException(e.toString());
        }
    }

    /**
     * Parses the script from the given file then compiles it and runs it.
     *
     * @return the new child context that was used to run the script
     */
    public JellyContext runScript(final File file, final XMLOutput output,
                          final boolean export, final boolean inherit) throws JellyException {
        try {
            return runScript(file.toURL(), output, export, inherit);
        } catch (final MalformedURLException e) {
            throw new JellyException(e.toString());
        }
    }

    /**
     * Parses the script from the given InputSource then compiles it and runs it.
     *
     * @return the new child context that was used to run the script
     */
    public JellyContext runScript(final InputSource source, final XMLOutput output) throws JellyException {
        return runScript(source, output, JellyContext.DEFAULT_EXPORT,
            JellyContext.DEFAULT_INHERIT);
    }

    /**
     * Parses the script from the given InputSource then compiles it and runs it.
     *
     * @return the new child context that was used to run the script
     */
    public JellyContext runScript(final InputSource source, final XMLOutput output,
                          final boolean export, final boolean inherit) throws JellyException {
        final Script script = compileScript(source);

        URL newJellyContextURL = null;
        try {
            newJellyContextURL = getJellyContextURL(source);
        } catch (final MalformedURLException e) {
            throw new JellyException(e.toString());
        }

        final JellyContext newJellyContext = newJellyContext();
        newJellyContext.setRootURL( newJellyContextURL );
        newJellyContext.setCurrentURL( newJellyContextURL );
        newJellyContext.setExport( export );
        newJellyContext.setInherit( inherit );

        if ( inherit ) {
            // use the same variable scopes
            newJellyContext.variables = this.variables;
        }

        if (log.isDebugEnabled() ) {
            log.debug( "About to run script: " + source.getSystemId() );
            log.debug( "root context URL: " + newJellyContext.rootURL );
            log.debug( "current context URL: " + newJellyContext.currentURL );
        }

        script.run(newJellyContext, output);

        return newJellyContext;
    }

    /**
     * Parses the script from the given uri using the
     * JellyContext.getResource() API then compiles it and runs it.
     *
     * @return the new child context that was used to run the script
     */
    public JellyContext runScript(final String uri, final XMLOutput output) throws JellyException {
        URL url = null;
        try {
            url = getResource(uri);
        } catch (final MalformedURLException e) {
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
    public JellyContext runScript(final String uri, final XMLOutput output,
                          final boolean export, final boolean inherit) throws JellyException {
        URL url = null;
        try {
            url = getResource(uri);
        } catch (final MalformedURLException e) {
            throw new JellyException(e.toString());
        }

        if (url == null) {
            throw new JellyException("Could not find Jelly script: " + url);
        }

        return runScript(url, output, export, inherit);
    }

    /**
     * Parses the script from the given URL then compiles it and runs it.
     *
     * @return the new child context that was used to run the script
     */
    public JellyContext runScript(final URL url, final XMLOutput output) throws JellyException {
        return runScript(url, output, JellyContext.DEFAULT_EXPORT,
            JellyContext.DEFAULT_INHERIT);
    }

    /**
     * Parses the script from the given URL then compiles it and runs it.
     *
     * @return the new child context that was used to run the script
     */
    public JellyContext runScript(final URL url, final XMLOutput output,
                          final boolean export, final boolean inherit) throws JellyException {
        return runScript(new InputSource(url.toString()), output, export, inherit);
    }

    /**
     * Sets whether we should allow our doctype definitions to call out to external entities.
     */
    public void setAllowDtdToCallExternalEntities(final boolean allowDtdToCallExternalEntities) {
        this.allowDtdToCallExternalEntities = allowDtdToCallExternalEntities;
    }

    /**
     * Sets whether caching of Tag instances, per thread, is enabled.
     * Caching Tags can boost performance, on some JVMs, by reducing the cost of
     * object construction when running Jelly inside a multi-threaded application server
     * such as a Servlet engine.
     *
     * @param cacheTags Whether caching should be enabled or disabled.
     */
    public void setCacheTags(final boolean cacheTags) {
        this.cacheTags = cacheTags;
    }

    /**
     * Sets the class loader to be used for instantiating application objects
     * when required.
     *
     * @param classLoader The new class loader to use, or {@code null}
     *  to revert to the standard rules
     */
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Sets the current URL context of the current script that is executing.
     *  This URL context is used to deduce relative scripts when relative URIs are
     *  used in calls to {@link #getResource} to process relative scripts.
     */
    public void setCurrentURL(final URL currentURL) {
        this.currentURL = currentURL;
    }

    /**
     * Sets whether we should export variable definitions to our parent context
     */
    public void setExport(final boolean export) {
        this.export = export;
    }

    /**
     * Sets whether we export tag libraries to our parents context
     * @param exportLibraries The exportLibraries to set
     */
    public void setExportLibraries(final boolean exportLibraries) {
        this.exportLibraries = exportLibraries;
    }

    /**
     * Sets whether we should inherit variables from our parent context
     */
    public void setInherit(final boolean inherit) {
        this.inherit = inherit;
    }

    /**
     * Change the parent context to the one provided
     * @param context the new parent context
     */
    protected void setParent(final JellyContext context)
    {
        parent = context;
        this.variables.put("parentScope", parent.variables);
        // need to re-export tag libraries to the new parent
        if (isExportLibraries() && parent != null) {
            for (final Iterator keys = taglibs.keySet().iterator(); keys.hasNext();)
            {
                final String namespaceURI = (String) keys.next();
                final Object tagLibOrClassName = taglibs.get(namespaceURI);
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

    /**
     * Sets the current root context URL from which all absolute resource URIs
     *  will be relative to. For example in a web application the root URL will
     *  map to the web directory which contains the WEB-INF directory.
     */
    public void setRootURL(final URL rootURL) {
        this.rootURL = rootURL;
    }

    /**
	 * @param suppressExpressionExceptions the suppressExpressionExceptions to set
	 * @deprecated after v1.1, exceptions will never be suppressed
	 */
	@Deprecated
    public void setSuppressExpressionExceptions(final boolean suppressExpressionExceptions) {
		this.suppressExpressionExceptions = suppressExpressionExceptions;
	}

    /**
     * Determine whether to use the Context ClassLoader (the one found by
     * calling {@code Thread.currentThread().getContextClassLoader()})
     * to resolve/load classes.  If not
     * using Context ClassLoader, then the class-loading defaults to
     * using the calling-class' ClassLoader.
     *
     * @param use determines whether to use JellyContext ClassLoader.
     */
    public void setUseContextClassLoader(final boolean use) {
        useContextClassLoader = use;
    }

    /** Sets the value of the named variable */
    public void setVariable(final String name, final Object value) {
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
    public void setVariable(final String name, final String scopeName, final Object value) {
        final JellyContext scope = getScope(scopeName);
        if ( scope != null ) {
            scope.setVariable(name, value);
        }
    }

	/**
     * Sets the Map of variables to use
     */
    public void setVariables(final Map variables) {
        // I have seen this fail when the passed Map contains a key, value
        // pair where the value is null
        for (final Iterator iter = variables.entrySet().iterator(); iter.hasNext();) {
            final Map.Entry element = (Map.Entry) iter.next();
            if (element.getValue() != null) {
                this.variables.put(element.getKey(), element.getValue());
            }
        }
        //this.variables.putAll( variables );
    }

}
