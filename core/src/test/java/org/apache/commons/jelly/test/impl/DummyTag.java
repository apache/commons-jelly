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
package org.apache.commons.jelly.test.impl;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.util.ClassLoaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple Test Tag
 */
public class DummyTag extends TagSupport {
    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(DummyTag.class);
    /** A test class to be loaded by the Tag*/
    private String m_classToBeLoaded = null;

    /**
     *
     * @see org.apache.commons.jelly.Tag#doTag(XMLOutput)
     * @see org.apache.commons.jelly.tags.core.JellyTag
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException {
        if (log.isDebugEnabled()) {
            log.debug("********Executing DummyTag Body*********");
        }
        if (m_classToBeLoaded != null) {
            try {
                ClassLoaderUtils.loadClass(m_classToBeLoaded, getClass());
                if (log.isDebugEnabled()) {
                    log.debug("Class[" + m_classToBeLoaded + "] FOUND");
                }
            }
            catch (final ClassNotFoundException cnfe) {
                if (log.isWarnEnabled()) {
                    log.warn("Class[" + m_classToBeLoaded + "] NOT FOUND");
                }
            }

        }
        invokeBody(output);
    }

    /**
     * A Test Variable(Used for testing the TagLibraryClassloader)
     */
    public void setLoadClass(final String extraClass) {
        m_classToBeLoaded = extraClass;
    }

}
