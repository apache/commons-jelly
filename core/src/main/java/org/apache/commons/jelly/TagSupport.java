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

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.jelly.util.TagUtils;

/** <p><code>TagSupport</code> an abstract base class which is useful to
  * inherit from if developing your own tag.</p>
  */

public abstract class TagSupport implements Tag {

    /**
     * Searches up the parent hierarchy from the given tag
     * for a Tag of the given type
     *
     * @param from the tag to start searching from
     * @param tagClass the type of the tag to find
     * @return the tag of the given type or null if it could not be found
     */
    public static Tag findAncestorWithClass(Tag from, final Class tagClass) {
        // we could implement this as
        //  return findAncestorWithClass(from,Collections.singleton(tagClass));
        // but this is so simple let's save the object creation for now
        while (from != null) {
            if (tagClass.isInstance(from)) {
                return from;
            }
            from = from.getParent();
        }
        return null;
    }

    /**
     * Searches up the parent hierarchy from the given tag
     * for a Tag matching one or more of given types.
     *
     * @param from the tag to start searching from
     * @param tagClasses an array of types that might match
     * @return the tag of the given type or null if it could not be found
     * @see #findAncestorWithClass(Tag,Collection)
     */
    public static Tag findAncestorWithClass(final Tag from, final Class[] tagClasses) {
        return findAncestorWithClass(from,Arrays.asList(tagClasses));
    }

    /**
     * Searches up the parent hierarchy from the given tag
     * for a Tag matching one or more of given types.
     *
     * @param from the tag to start searching from
     * @param tagClasses a Collection of Class types that might match
     * @return the tag of the given type or null if it could not be found
     */
    public static Tag findAncestorWithClass(Tag from, final Collection tagClasses) {
        while (from != null) {
            for(final Iterator iter = tagClasses.iterator();iter.hasNext();) {
                final Class klass = (Class)iter.next();
                if (klass.isInstance(from)) {
                    return from;
                }
            }
            from = from.getParent();
        }
        return null;
    }
    /** The parent of this tag */
    protected Tag parent;
    /** The TagLibrary which defines this tag */
    protected TagLibrary tagLibrary;

    /** The body of the tag */
    protected Script body;

    /** The current context */

    protected Boolean shouldTrim;

    protected boolean hasTrimmed;

    protected JellyContext context;

    /** Whether XML text should be escaped */
    private boolean escapeText = true;

    public TagSupport() {
    }

    public TagSupport(final boolean shouldTrim) {
        setTrim( shouldTrim );
    }

    // Implementation methods
    //-------------------------------------------------------------------------
    /**
     * Searches up the parent hierarchy for a Tag of the given type.
     * @return the tag of the given type or null if it could not be found
     */
    protected Tag findAncestorWithClass(final Class parentClass) {
        return findAncestorWithClass(getParent(), parentClass);
    }

    /**
     * Searches up the parent hierarchy for a Tag of one of the given types.
     * @return the tag of the given type or null if it could not be found
     * @see #findAncestorWithClass(Collection)
     */
    protected Tag findAncestorWithClass(final Class[] parentClasses) {
        return findAncestorWithClass(getParent(), parentClasses);
    }

    /**
     * Searches up the parent hierarchy for a Tag of one of the given types.
     * @return the tag of the given type or null if it could not be found
     */
    protected Tag findAncestorWithClass(final Collection parentClasses) {
        return findAncestorWithClass(getParent(), parentClasses);
    }

    /** @return the body of the tag */
    @Override
    public Script getBody() {
        if (! hasTrimmed) {
            hasTrimmed = true;
            if (isTrim()) {
                trimBody();
            }
        }
        return body;
    }

    /**
     * Executes the body of the tag and returns the result as a String.
     *
     * @return the text evaluation of the body
     */
    protected String getBodyText() throws JellyTagException {
        return getBodyText(escapeText);
    }

	/**
     * Executes the body of the tag and returns the result as a String.
     *
     * @param shouldEscape Signal if the text should be escaped.
     * @return the text evaluation of the body
     */
    protected String getBodyText(final boolean shouldEscape) throws JellyTagException {
        final StringWriter writer = new StringWriter();
        invokeBody(XMLOutput.createXMLOutput(writer, shouldEscape));
        return writer.toString();
    }

	/** @return the context in which the tag will be run */
    @Override
    public JellyContext getContext() {
        return context;
    }

    /** @return the parent of this tag */
    @Override
    public Tag getParent() {
        return parent;
    }

    /* (non-Javadoc)
	 * @see org.apache.commons.jelly.Tag#getTagLib()
	 */
	@Override
    public TagLibrary getTagLib() {
		return tagLibrary;
	}

    /**
     * Invokes the body of this tag using the given output
     */
    @Override
    public void invokeBody(final XMLOutput output) throws JellyTagException {
        getBody().run(context, output);
    }

    /**
     * Returns whether the body of this tag will be escaped or not.
     */
    public boolean isEscapeText() {
        return escapeText;
    }

    public boolean isTrim() {
        if ( this.shouldTrim == null ) {
            final Tag parent = getParent();
            if ( parent == null ) {
                return true;
            }
            if ( parent instanceof TagSupport ) {
                final TagSupport parentSupport = (TagSupport) parent;

                this.shouldTrim = parentSupport.isTrim() ? Boolean.TRUE : Boolean.FALSE;
            }
            else {
                this.shouldTrim = Boolean.TRUE;
            }
        }

        return this.shouldTrim.booleanValue();
    }

    /** Sets the body of the tag */
    @Override
    public void setBody(final Script body) {
        this.body = body;
        this.hasTrimmed = false;
    }

    /** Sets the context in which the tag will be run */
    @Override
    public void setContext(final JellyContext context) throws JellyTagException {
        this.context = context;
    }

    /**
     * Sets whether the body of the tag should be escaped as text (so that &lt; and &gt; are
     * escaped as &amp;lt; and &amp;gt;), which is the default or leave the text as XML.
     */
    public void setEscapeText(final boolean escapeText) {
        this.escapeText = escapeText;
    }

    /** Sets the parent of this tag */
    @Override
    public void setParent(final Tag parent) {
        this.parent = parent;
    }

    /* (non-Javadoc)
	 * @see org.apache.commons.jelly.Tag#setTagLib(org.apache.commons.jelly.TagLibrary)
	 */
	@Override
    public void setTagLib(final TagLibrary tagLibrary) {
		if (this.tagLibrary != null && tagLibrary != this.tagLibrary) {
            throw new IllegalArgumentException("Cannot setTagLib once set");
        }
		this.tagLibrary = tagLibrary;
	}

    /**
     * Sets whether whitespace inside this tag should be trimmed or not.
     * Defaults to true so whitespace is trimmed
     */
    public void setTrim(final boolean shouldTrim) {
        if ( shouldTrim ) {
            this.shouldTrim = Boolean.TRUE;
        }
        else {
            this.shouldTrim = Boolean.FALSE;
        }
    }

    /**
     * Find all text nodes inside the top level of this body and
     * if they are just whitespace then remove them
     */
    protected void trimBody() {
        TagUtils.trimScript(body);
    }
}
