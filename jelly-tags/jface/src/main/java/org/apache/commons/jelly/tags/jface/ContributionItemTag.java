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
package org.apache.commons.jelly.tags.jface;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionManager;

/**
 * This tag creates an JFace ContributionItem
 */
public class ContributionItemTag extends UseBeanTag {

    public ContributionItemTag(final Class arg0) {
        super(arg0);
    }

    /**
      * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
      */
    @Override
    public void doTag(final XMLOutput output)
        throws MissingAttributeException, JellyTagException {

        super.doTag(output);

        final Object bean = getBean();
        if (bean != null && bean instanceof ContributionItem) {
            final IContributionManager cm = getParentContributionManager();
            if (cm != null) {
                cm.add((ContributionItem) bean);
            }
        }

    }

    /**
     * @return IContributionManager
     */
    protected IContributionManager getParentContributionManager() {
        final MenuManagerTag tag =
            (MenuManagerTag) findAncestorWithClass(MenuManagerTag.class);
        if (tag != null) {
            return tag.getMenuManager();
        }
        return null;
    }

}
