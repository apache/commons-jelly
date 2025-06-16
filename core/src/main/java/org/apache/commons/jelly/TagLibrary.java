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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils2.ConvertUtils;
import org.apache.commons.beanutils2.Converter;
import org.apache.commons.jelly.expression.CompositeExpression;
import org.apache.commons.jelly.expression.ConstantExpression;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.expression.ExpressionFactory;
import org.apache.commons.jelly.impl.TagFactory;
import org.apache.commons.jelly.impl.TagScript;
import org.xml.sax.Attributes;

/** <p><code>Taglib</code> represents the metadata for a Jelly custom tag library.</p>
  */

public abstract class TagLibrary {

	static {

        // register standard converters

      ConvertUtils.register(
              new Converter() {
                  @Override
                  public Object convert(Class type, Object value) {
                      if ( value instanceof File ) {
                          return (File) value;
                      }
                      else if ( value != null ) {
                          String text = value.toString();
                          return new File( text );
                      }
                      return null;
                  }
              },
              File.class
          );
    }
    private boolean allowUnknownTags = true;

    private final Map tags = new HashMap();

    /**
	 * Default Constructor
	 */
    public TagLibrary() {
    }

    /**
     * Constructor
	 * @param allowUnknownTags whether unknown tags are allowed or an exception is raised
	 */
	public TagLibrary(final boolean allowUnknownTags) {
		this.allowUnknownTags = allowUnknownTags;
	}

	/** Allows taglibs to use their own expression evaluation mechanism */
    public Expression createExpression(
        final ExpressionFactory factory,
        final TagScript tagScript,
        final String attributeName,
        final String attributeValue)
        throws JellyException {

        ExpressionFactory myFactory = getExpressionFactory();
        if (myFactory == null) {
            myFactory = factory;
        }
        if (myFactory != null) {
            return CompositeExpression.parse(attributeValue, myFactory);
        }

        // will use a constant expression instead
        return new ConstantExpression(attributeValue);
    }

    /** Creates a new Tag for the given tag name and attributes */
    public Tag createTag(final String name, final Attributes attributes)
        throws JellyException {

        final Object value = tags.get(name);
        if (value instanceof Class) {
            final Class type = (Class) value;
            try {
                return (Tag) type.getConstructor().newInstance();
            } catch (final ReflectiveOperationException e) {
                throw new JellyException(e.toString());
            }
        }
        if (value instanceof TagFactory) {
            final TagFactory factory = (TagFactory) value;
            return factory.createTag(name, attributes);
        }
    	return null;
    }

    /** Creates a new script to execute the given tag name and attributes */
    public TagScript createTagScript(final String name, final Attributes attributes)
        throws JellyException {

        final Object value = tags.get(name);
        if (value instanceof Class) {
            final Class type = (Class) value;
            return TagScript.newInstance(type);
        }
        if (value instanceof TagFactory) {
            return new TagScript( (TagFactory) value );
        }
        return null;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /** Allows derived tag libraries to use their own factory */
    public ExpressionFactory getExpressionFactory() {
        return null;
    }

    protected Map getTagClasses() {
        return tags;
    }

    /**
	 * @return the allowUnknownTags
	 */
	public boolean isAllowUnknownTags() {
		return allowUnknownTags;
	}

    /**
     * Registers a tag implementation Class for a given tag name
     */
    protected void registerTag(final String name, final Class type) {
        tags.put(name, type);
    }

	/**
     * Registers a tag factory for a given tag name
     */
    protected void registerTagFactory(final String name, final TagFactory tagFactory) {
        tags.put(name, tagFactory);
    }

	/**
	 * @param allowUnknownTags the allowUnknownTags to set
	 */
	public void setAllowUnknownTags(final boolean allowUnknownTags) {
		this.allowUnknownTags = allowUnknownTags;
	}

}
