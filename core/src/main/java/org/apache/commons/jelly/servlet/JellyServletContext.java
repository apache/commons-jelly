/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jelly.servlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.apache.commons.jelly.JellyContext;

/**
 * @version 1.1
 */
public class JellyServletContext extends JellyContext {

    private ServletContext ctx;

    public JellyServletContext() {
    }

    public JellyServletContext(final JellyContext parent, final ServletContext ctx) {
        super(parent);
        this.ctx = ctx;
    }

    public JellyServletContext(final ServletContext ctx) {
        this.ctx = ctx;
    }

    @Override
    protected JellyContext createChildContext() {
        return new JellyServletContext(this, ctx);
    }

    /**
     * Allow access of relative URIs when performing &lt;j:include&gt;.
     *
     * @param s
     * @return The {@code InputStream} returned to the servlet, or {@code null} if no resource exists at the specified path
     * @throws MalformedURLException
     */
    @Override
    public URL getResource(final String s) throws MalformedURLException {
        return ctx.getResource(s);
    }

    /**
     * Allow access of relative URIs when performing &lt;j:include&gt;.
     *
     * @param s
     * @return The {@code InputStream} returned to the servlet, or {@code null} if no resource exists at the specified path.
     */
    @Override
    public InputStream getResourceAsStream(final String s) {
        return ctx.getResourceAsStream(s);
    }
}
