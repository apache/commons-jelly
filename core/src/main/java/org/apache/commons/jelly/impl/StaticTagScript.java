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
package org.apache.commons.jelly.impl;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.jelly.DynaTag;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.xml.sax.SAXException;

/**
 * <p><code>StaticTagScript</code> is a script that evaluates a StaticTag, a piece of static XML
 * though its attributes or element content may contain dynamic expressions.
 * The first time this tag evaluates, it may have become a dynamic tag, so it will check that
 * a new dynamic tag has not been generated.</p>
 */
public class StaticTagScript extends TagScript {

    public StaticTagScript() {
    }

    public StaticTagScript(final TagFactory tagFactory) {
        super(tagFactory);
    }

    /**
     * Attempts to find a dynamically created tag that has been created since this
     * script was compiled
     */
    protected Tag findDynamicTag(final JellyContext context, final StaticTag tag) throws JellyException {
        // lets see if there's a tag library for this URI...
        final TagLibrary taglib = context.getTagLibrary( tag.getUri() );
        if ( taglib != null ) {
            final Tag newTag = taglib.createTag( tag.getLocalName(), getSaxAttributes() );
            if ( newTag != null ) {
                newTag.setParent( tag.getParent() );
                newTag.setBody( tag.getBody() );
                return newTag;
            }
        }
        return tag;
    }

    // Script interface
    //-------------------------------------------------------------------------
    @Override
    public void run(final JellyContext context, final XMLOutput output) throws JellyTagException {
        try {
            startNamespacePrefixes(output);
        } catch (final SAXException e) {
            throw new JellyTagException("could not start namespace prefixes", e);
        }

        Tag tag;
        try {
            tag = getTag(context);

            // lets see if we have a dynamic tag
            if (tag instanceof StaticTag) {
                tag = findDynamicTag(context, (StaticTag) tag);
            }

            setTag(tag, context);
        } catch (final JellyException e) {
            throw new JellyTagException(e);
        }

        final URL rootURL = context.getRootURL();
        final URL currentURL = context.getCurrentURL();
        try {
            if (tag == null) {
                return;
            }
            tag.setContext(context);
            setContextURLs(context);

            final DynaTag dynaTag = (DynaTag) tag;

            // ### probably compiling this to 2 arrays might be quicker and smaller
            for (final Iterator iter = attributes.entrySet().iterator(); iter.hasNext();) {
                final Map.Entry entry = (Map.Entry) iter.next();
                String name = (String) entry.getKey();
                if (name.indexOf(':') != -1) {
                    name = name.substring(name.indexOf(':') + 1);
                }
                final ExpressionAttribute expat = (ExpressionAttribute) entry.getValue();
                final Expression expression = expat.exp;

                Object value;

                if (Expression.class.isAssignableFrom(dynaTag.getAttributeType(name))) {
                    value = expression;
                } else {
                    value = expression.evaluate(context);
                }

                if (expat.prefix != null && expat.prefix.length() > 0 && tag instanceof StaticTag) {
                    ((StaticTag) dynaTag).setAttribute(name, expat.prefix, expat.nsURI, value);
                } else {
                    dynaTag.setAttribute(name, value);
                }
            }

            tag.doTag(output);
        } catch (final JellyTagException e) {
            handleException(e);
        } catch (final RuntimeException e) {
            handleException(e);
        } finally {
            context.setCurrentURL(currentURL);
            context.setRootURL(rootURL);
        }

        try {
            endNamespacePrefixes(output);
        } catch (final SAXException e) {
            throw new JellyTagException("could not end namespace prefixes", e);
        }
    }
}
