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

package org.apache.commons.jelly.tags.core;

import java.io.File;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * A tag which conditionally evaluates its body based on some condition
 */
public class IncludeTag extends TagSupport {

    private String uri;
    private File file;
    private boolean shouldExport;
    private boolean shouldInherit;

    public IncludeTag() {
        this.shouldExport = false;
        this.shouldInherit = true;
    }

    @Override
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (uri == null && file == null) {
            throw new MissingAttributeException("uri");
        }
        // we need to create a new JellyContext of the URI
        // take off the script name from the URL
        String text = null;
        try {
            if (uri != null) {
                text = uri;
                context.runScript(uri, output, isExport(), isInherit());
            } else {
                text = file.toString();
                context.runScript(file, output, isExport(), isInherit());
            }
        } catch (final JellyException e) {
            throw new JellyTagException("could not include jelly script: " + text + ". Reason: " + e, e);
        }
    }

    /**
     * Gets the file.
     *
     * @return the file.
     */
    public File getFile() {
        return file;
    }

    public boolean isExport() {
        return this.shouldExport;
    }

    public boolean isInherit() {
        return this.shouldInherit;
    }

    public void setExport(final String export) {
        if ("true".equals(export)) {
            this.shouldExport = true;
        } else {
            this.shouldExport = false;
        }
    }

    /**
     * Sets the file to be included which is either an absolute file or a file relative to the current directory
     *
     * @param file A file..
     */
    public void setFile(final File file) {
        this.file = file;
    }

    public void setInherit(final String inherit) {
        if ("true".equals(inherit)) {
            this.shouldInherit = true;
        } else {
            this.shouldInherit = false;
        }
    }

    /** Sets the URI (relative URI or absolute URL) for the script to evaluate. */
    public void setUri(final String uri) {
        this.uri = uri;
    }
}
