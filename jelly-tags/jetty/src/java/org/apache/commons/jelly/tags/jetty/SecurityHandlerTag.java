/*
 * $Header: /home/cvs/jakarta-commons/latka/src/java/org/apache/commons/latka/jelly/SecurityHandlerTag.java,v 1.3 2002/07/14 12:38:22 dion Exp $
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

import org.mortbay.http.BasicAuthenticator;
import org.mortbay.http.ClientCertAuthenticator;
import org.mortbay.http.DigestAuthenticator;
import org.mortbay.http.SecurityConstraint.Authenticator;
import org.mortbay.http.SecurityConstraint;
import org.mortbay.http.handler.SecurityHandler;
import org.mortbay.jetty.servlet.FormAuthenticator;
import org.mortbay.util.Code;
import org.mortbay.xml.XmlParser;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

/**
 * Declare a security handler for a Jetty http server
 *
 * @author  rtl
 * @version $Id: SecurityHandlerTag.java,v 1.3 2002/07/14 12:38:22 dion Exp $
 */
public class SecurityHandlerTag extends TagSupport {

    /** a form authenticator used by this tag */
    private transient FormAuthenticator _formAuthenticator;

    /** parameter authentication method, defaults to BASIC in Jetty */
    private String _authenticationMethod;

    /** Creates a new instance of SecurityHandlerTag */
    public SecurityHandlerTag() {
    }

    /**
     * Perform the tag functionality. In this case, add a security handler
     * to the parent context, setting the authentication method if required
     * The security constraints should be specified as the body of this tag
     * using the same format as a web.xml file wrapped in a single
     * <constraints> tag to allow parsing of a well-formed snippet, e.g.
     *
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
     *
     * @param xmlOutput where to send output
     * @throws Exception when an error occurs
     */
    public void doTag(XMLOutput xmlOutput) throws JellyTagException {
        HttpContextTag httpContext = (HttpContextTag) findAncestorWithClass(
            HttpContextTag.class);
        if ( httpContext == null ) {
            throw new JellyTagException( "<securityHandler> tag must be enclosed inside a <httpContext> tag" );
        }
        SecurityHandler securityHandler = new SecurityHandler();
        if (getauthenticationMethod() != null) {
            securityHandler.setAuthMethod(getauthenticationMethod());
        }
        httpContext.addHandler(securityHandler);

        // get the security constraints from the body of this tag
        // by parsing the body of the parent (so it will be well formed)
        String bodyText = getBodyText();
        StringReader reader = new StringReader(bodyText);
        InputSource inputSource = new InputSource(reader);

        // crate a non-validating parser
        XmlParser xmlParser = new XmlParser(false);

        XmlParser.Node node = null;
        try {
            node = xmlParser.parse(inputSource);
        } 
        catch (IOException e) {
            throw new JellyTagException(e);
        } 
        catch (SAXException e) {
            throw new JellyTagException(e);
        }

        Iterator iter=node.iterator();
        XmlParser.Node currNode = null;
        while (iter.hasNext())
        {
                Object o = iter.next();
                if (!(o instanceof XmlParser.Node))
                    continue;

                currNode=(XmlParser.Node)o;
                String name=currNode.getTag();

                if ("security-constraint".equals(name)) {
                    initSecurityConstraint(currNode, httpContext);
                } else if ("login-config".equals(name)) {
                    initLoginConfig(currNode, httpContext);
                } else {
                    throw new JellyTagException("Invalid element in <securityHandler> tag. Are you using the <constraints> tag?: " + currNode);
                }
        }

    }

    /* ------------------------------------------------------------
     * This is the code from Jetty's WebApplicationContext
     * with the HttpContextTag parameter added
     *
     * Process a parsed xml node to setup the security constraints
     * for an http server
     *
     * @param node the parsed xml starting node of the constraints
     * @param httpContext the tag to add the security constraint to
    */
    protected void initSecurityConstraint(XmlParser.Node node,
                                          HttpContextTag httpContext)
    {
        SecurityConstraint scBase = new SecurityConstraint();

        XmlParser.Node auths=node.get("auth-constraint");
        if (auths!=null)
        {
            scBase.setAuthenticate(true);
            // auth-constraint
            Iterator iter= auths.iterator("role-name");
            while(iter.hasNext())
            {
                String role=((XmlParser.Node)iter.next()).toString(false,true);
                scBase.addRole(role);
            }
        }

        XmlParser.Node data=node.get("user-data-constraint");
        if (data!=null)
        {
            data=data.get("transport-guarantee");
            String guarantee = data.toString(false,true).toUpperCase();
            if (guarantee==null || guarantee.length()==0 ||
                "NONE".equals(guarantee))
                scBase.setDataConstraint(SecurityConstraint.DC_NONE);
            else if ("INTEGRAL".equals(guarantee))
                scBase.setDataConstraint(SecurityConstraint.DC_INTEGRAL);
            else if ("CONFIDENTIAL".equals(guarantee))
                scBase.setDataConstraint(SecurityConstraint.DC_CONFIDENTIAL);
            else
            {
                Code.warning("Unknown user-data-constraint:"+guarantee);
                scBase.setDataConstraint(SecurityConstraint.DC_CONFIDENTIAL);
            }
        }

        Iterator iter= node.iterator("web-resource-collection");
        while(iter.hasNext())
        {
            XmlParser.Node collection=(XmlParser.Node)iter.next();
            String name=collection.getString("web-resource-name",false,true);
            SecurityConstraint sc = (SecurityConstraint)scBase.clone();
            sc.setName(name);

            Iterator iter2= collection.iterator("http-method");
            while(iter2.hasNext())
                sc.addMethod(((XmlParser.Node)iter2.next())
                             .toString(false,true));

            iter2= collection.iterator("url-pattern");
            while(iter2.hasNext())
            {
                String url=
                    ((XmlParser.Node)iter2.next()).toString(false,true);
                httpContext.addSecurityConstraint(url,sc);
            }
        }
    }

    /* ------------------------------------------------------------
     * This is the code from Jetty's WebApplicationContext
     * with the HttpContextTag parameter added
     *
     *
     * Process a parsed xml node to setup the authenticator and realm
     * for an http server
     *
     * @param node the parsed xml starting node of the login configuration
     * @param httpContext the tag to add the authenticator and realm to
    */
    protected void initLoginConfig(XmlParser.Node node,
                                   HttpContextTag httpContext)
    {
        XmlParser.Node method=node.get("auth-method");
        if (method!=null)
        {
            Authenticator authenticator=null;
            String m=method.toString(false,true);

            if (SecurityConstraint.__FORM_AUTH.equals(m))
                authenticator=_formAuthenticator=new FormAuthenticator();
            else if (SecurityConstraint.__BASIC_AUTH.equals(m))
                authenticator=new BasicAuthenticator();
            else if (SecurityConstraint.__DIGEST_AUTH.equals(m))
                authenticator=new DigestAuthenticator();
            else if (SecurityConstraint.__CERT_AUTH.equals(m))
                authenticator=new ClientCertAuthenticator();
            else
                Code.warning("UNKNOWN AUTH METHOD: "+m);

            httpContext.setAuthenticator(authenticator);
        }

        XmlParser.Node name=node.get("realm-name");
        if (name!=null)
            httpContext.setRealmName(name.toString(false,true));

        XmlParser.Node formConfig = node.get("form-login-config");
        if(formConfig != null)
        {
            if (_formAuthenticator==null)
                Code.warning("FORM Authentication miss-configured");
            else
            {
                XmlParser.Node loginPage = formConfig.get("form-login-page");
                if (loginPage != null)
                    _formAuthenticator.setLoginPage(loginPage.toString(false,true));
                XmlParser.Node errorPage = formConfig.get("form-error-page");
                if (errorPage != null)
                    _formAuthenticator.setErrorPage(errorPage.toString(false,true));
            }
        }
    }

    //--------------------------------------------------------------------------
    // Property accessors/mutators
    //--------------------------------------------------------------------------

    /**
     * Getter for property authenticationMethod.
     *
     * @return value of property authenticationMethod.
     */
    public String getauthenticationMethod() {
        return _authenticationMethod;
    }

    /**
     * Setter for property authenticationMethod.
     *
     * @param authenticationMethod Type of authentication (BASIC, FORM, DIGEST, CLIENT-CERT)
     * Note that only BASIC and CLIENT-CERT are supported by Jetty as of v4.1.1
     */
    public void setauthenticationMethod(String authenticationMethod) {
        _authenticationMethod = authenticationMethod;
    }


}
