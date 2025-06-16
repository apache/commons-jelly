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

import org.xml.sax.SAXException;

public class TJEcho extends TagSupport {

	public static final String TAG_NAME = "echo";

	private String id;
	private String attr;
	private String text;

	@Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
		text = getBodyText();
		final String str = "id=" + id + "; attr=" + attr + "; text=" + text + "\n";
		try {
			output.characters(str.toCharArray(), 0, str.length());
		}catch (final SAXException e) {
			throw new JellyTagException();
		}
	}

	/**
	 * @return the attr
	 */
	public String getAttr() {
		return attr;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param attr the attr to set
	 */
	public void setAttr(final String attr) {
		this.attr = attr;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(final String id) {
		this.id = id;
	}

}
