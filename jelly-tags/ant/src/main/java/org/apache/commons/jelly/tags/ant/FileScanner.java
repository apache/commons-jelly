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
package org.apache.commons.jelly.tags.ant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

/**
 * <p><code>FileScanner</code> is a bean which allows the iteration
 * over a number of files from a collection of FileSet instances.
 */
public class FileScanner {

    /** FileSets */
    private final List filesets = new ArrayList();

    /** The Ant project */
    private Project project;

    /**
     * Adds a set of files (nested fileset attribute).
     */
    public void addFileset(final FileSet set) {
        filesets.add(set);
    }

    /**
     * Clears any file sets that have been added to this scanner
     */
    public void clear() {
        filesets.clear();
    }

    public Iterator directories() {
        return new FileIterator(project, filesets.iterator(), true);
    }

    public boolean hasFiles() {
        return filesets.size() > 0;
    }

    public Iterator iterator() {
        return new FileIterator(project, filesets.iterator());
    }

    // Properties
    //-------------------------------------------------------------------------

    public void setProject(final Project project)
    {
        this.project = project;
    }

}

