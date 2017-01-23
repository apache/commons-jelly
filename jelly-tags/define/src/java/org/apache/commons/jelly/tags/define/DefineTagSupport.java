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

package org.apache.commons.jelly.tags.define;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.impl.DynamicTagLibrary;

/**
 * An abstract base class useful for implementation inheritance.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision$
 */
public abstract class DefineTagSupport extends TagSupport {

    // Implementstion methods
    //-------------------------------------------------------------------------

    /**
     * @return the current dynamic tag library instance or throws a JellyException
     * if one is not available
     */
    public DynamicTagLibrary getTagLibrary() {
        TaglibTag tag
            = (TaglibTag) findAncestorWithClass(TaglibTag.class);
        if ( tag == null ) {
            throw new IllegalArgumentException( "<define:tag> must be inside <define:taglib>" );
        }
        return tag.getTagLibrary();
    }

}
