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

package org.apache.commons.jelly.tags.jetty;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.mortbay.http.BasicAuthenticator;
import org.mortbay.http.ClientCertAuthenticator;
import org.mortbay.http.DigestAuthenticator;
import org.mortbay.http.SecurityConstraint;
import org.mortbay.http.SecurityConstraint.Authenticator;
import org.mortbay.http.handler.SecurityHandler;
import org.mortbay.jetty.servlet.FormAuthenticator;
import org.mortbay.util.Code;
import org.mortbay.xml.XmlParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Declare a security handler for a Jetty http server
 */
public class SecurityHandlerTag extends TagSupport {

    /** A form authenticator used by this tag */
    private transient FormAuthenticator _formAuthenticator;

    /** Parameter authentication method, defaults to BASIC in Jetty */
    private String _authenticationMethod;

    /** Creates a new instance of SecurityHandlerTag */
    public SecurityHandlerTag() {
    }

    /**
     * Perform the tag functionality. In this case, add a security handler
     * to the parent context, setting the authentication method if required
     * The security constraints should be specified as the body of this tag
     * using the same format as a web.xml file wrapped in a single
     * {@code <constraints>} tag to allow parsing of a well-formed snippet, e.g.
     * <pre>{@code
     * <constraints>
     *   <security-constraint>
     *    <web-resource-collection>
     *     <web-resource-name>Default</web-resource-name>
     *      <url-pattern>/</url-pattern>
     *    </web-resource-collection>
     *    <auth-constraint/>
     *   </security-constraint>
     *
     *   <security-constraint>
     *     <web-resource-collection>
     *       <url-pattern>/docRoot/resourceHandlerTest/*</url-pattern>
     *       <http-method>GET</http-method>
     *       <http-method>HEAD</http-method>
     *     </web-resource-collection>
     *     <auth-constraint>
     *       <role-name>*</role-name>
     *     </auth-constraint>
     *   </security-constraint>
     *
     *   <login-config>
     *     <auth-method>BASIC</auth-method>
     *     <realm-name>Jetty Demo Realm</realm-name>
     *   </login-config>
     *
     * </constraints>
     * }</pre>
     * @param xmlOutput where to send output
     * @throws JellyTagException when an error occurs
     */
    @Override
    public void doTag(final XMLOutput xmlOutput) throws JellyTagException {
        final HttpContextTag httpContext = (HttpContextTag) findAncestorWithClass(
            HttpContextTag.class);
        if ( httpContext == null ) {
            throw new JellyTagException( "<securityHandler> tag must be enclosed inside a <httpContext> tag" );
        }
        final SecurityHandler securityHandler = new SecurityHandler();
        if (getauthenticationMethod() != null) {
            securityHandler.setAuthMethod(getauthenticationMethod());
        }
        httpContext.addHandler(securityHandler);

        // get the security constraints from the body of this tag
        // by parsing the body of the parent (so it will be well formed)
        final String bodyText = getBodyText();
        final StringReader reader = new StringReader(bodyText);
        final InputSource inputSource = new InputSource(reader);

        // crate a non-validating parser
        final XmlParser xmlParser = new XmlParser(false);

        XmlParser.Node node = null;
        try {
            node = xmlParser.parse(inputSource);
        }
        catch (final IOException | SAXException e) {
            throw new JellyTagException(e);
        }

        final Iterator iter=node.iterator();
        XmlParser.Node currNode = null;
        while (iter.hasNext())
        {
                final Object o = iter.next();
                if (!(o instanceof XmlParser.Node)) {
                    continue;
                }

                currNode=(XmlParser.Node)o;
                final String name=currNode.getTag();

                if ("security-constraint".equals(name)) {
                    initSecurityConstraint(currNode, httpContext);
                } else if ("login-config".equals(name)) {
                    initLoginConfig(currNode, httpContext);
                } else {
                    throw new JellyTagException("Invalid element in <securityHandler> tag. Are you using the <constraints> tag?: " + currNode);
                }
        }

    }

    /**
     * Getter for property authenticationMethod.
     *
     * @return value of property authenticationMethod.
     */
    public String getauthenticationMethod() {
        return _authenticationMethod;
    }

    /*
     * This is the code from Jetty's WebApplicationContext
     * with the HttpContextTag parameter added
     *
     *
     * Process a parsed XML node to setup the authenticator and realm
     * for an http server
     *
     * @param node the parsed XML starting node of the login configuration
     * @param httpContext the tag to add the authenticator and realm to
    */
    protected void initLoginConfig(final XmlParser.Node node,
                                   final HttpContextTag httpContext)
    {
        final XmlParser.Node method=node.get("auth-method");
        if (method!=null)
        {
            Authenticator authenticator=null;
            final String m=method.toString(false,true);

            if (m != null) {
                switch (m) {
                case SecurityConstraint.__FORM_AUTH:
                    authenticator=_formAuthenticator=new FormAuthenticator();
                    break;
                case SecurityConstraint.__BASIC_AUTH:
                    authenticator=new BasicAuthenticator();
                    break;
                case SecurityConstraint.__DIGEST_AUTH:
                    authenticator=new DigestAuthenticator();
                    break;
                case SecurityConstraint.__CERT_AUTH:
                    authenticator=new ClientCertAuthenticator();
                    break;
                default:
                    Code.warning("UNKNOWN AUTH METHOD: "+m);
                    break;
                }
            } else {
                Code.warning("UNKNOWN AUTH METHOD: "+m);
            }

            httpContext.setAuthenticator(authenticator);
        }

        final XmlParser.Node name=node.get("realm-name");
        if (name!=null) {
            httpContext.setRealmName(name.toString(false,true));
        }

        final XmlParser.Node formConfig = node.get("form-login-config");
        if (formConfig != null)
        {
            if (_formAuthenticator==null) {
                Code.warning("FORM Authentication miss-configured");
            } else
            {
                final XmlParser.Node loginPage = formConfig.get("form-login-page");
                if (loginPage != null) {
                    _formAuthenticator.setLoginPage(loginPage.toString(false,true));
                }
                final XmlParser.Node errorPage = formConfig.get("form-error-page");
                if (errorPage != null) {
                    _formAuthenticator.setErrorPage(errorPage.toString(false,true));
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    // Property accessors/mutators
    //--------------------------------------------------------------------------

    /*
     * This is the code from Jetty's WebApplicationContext
     * with the HttpContextTag parameter added
     *
     * Process a parsed XML node to setup the security constraints
     * for an http server
     *
     * @param node the parsed XML starting node of the constraints
     * @param httpContext the tag to add the security constraint to
    */
    protected void initSecurityConstraint(final XmlParser.Node node,
                                          final HttpContextTag httpContext)
    {
        final SecurityConstraint scBase = new SecurityConstraint();

        final XmlParser.Node auths=node.get("auth-constraint");
        if (auths!=null)
        {
            scBase.setAuthenticate(true);
            // auth-constraint
            final Iterator iter= auths.iterator("role-name");
            while(iter.hasNext())
            {
                final String role=((XmlParser.Node)iter.next()).toString(false,true);
                scBase.addRole(role);
            }
        }

        XmlParser.Node data=node.get("user-data-constraint");
        if (data!=null)
        {
            data=data.get("transport-guarantee");
            final String guarantee = data.toString(false,true).toUpperCase();
            if (guarantee==null || guarantee.length()==0 ||
                "NONE".equals(guarantee)) {
                scBase.setDataConstraint(SecurityConstraint.DC_NONE);
            } else if ("INTEGRAL".equals(guarantee)) {
                scBase.setDataConstraint(SecurityConstraint.DC_INTEGRAL);
            } else if ("CONFIDENTIAL".equals(guarantee)) {
                scBase.setDataConstraint(SecurityConstraint.DC_CONFIDENTIAL);
            } else
            {
                Code.warning("Unknown user-data-constraint:"+guarantee);
                scBase.setDataConstraint(SecurityConstraint.DC_CONFIDENTIAL);
            }
        }

        final Iterator iter= node.iterator("web-resource-collection");
        while(iter.hasNext())
        {
            final XmlParser.Node collection=(XmlParser.Node)iter.next();
            final String name=collection.getString("web-resource-name",false,true);
            final SecurityConstraint sc = (SecurityConstraint)scBase.clone();
            sc.setName(name);

            Iterator iter2= collection.iterator("http-method");
            while(iter2.hasNext()) {
                sc.addMethod(((XmlParser.Node)iter2.next())
                             .toString(false,true));
            }

            iter2= collection.iterator("url-pattern");
            while(iter2.hasNext())
            {
                final String url=
                    ((XmlParser.Node)iter2.next()).toString(false,true);
                httpContext.addSecurityConstraint(url,sc);
            }
        }
    }

    /**
     * Setter for property authenticationMethod.
     *
     * @param authenticationMethod Type of authentication (BASIC, FORM, DIGEST, CLIENT-CERT)
     * Note that only BASIC and CLIENT-CERT are supported by Jetty as of v4.1.1
     */
    public void setauthenticationMethod(final String authenticationMethod) {
        _authenticationMethod = authenticationMethod;
    }

}
