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

package org.apache.commons.digester.rss;

import java.io.PrintWriter;
import java.io.Serializable;

/**
 * <p>Implementation object representing an <strong>item</strong> in the
 * <em>Rich Site Summary</em> DTD, version 0.91.  This class may be subclassed
 * to further specialize its behavior.</p>
 */

public class Item implements Serializable {

    /**
     * The item description (1-500 characters).
     */
    protected String description = null;

    /**
     * The item link (1-500 characters).
     */
    protected String link = null;

    /**
     * The item title (1-100 characters).
     */
    protected String title = null;

    public String getDescription() {
        return this.description;
    }

    public String getLink() {
        return this.link;
    }

    public String getTitle() {
        return this.title;
    }

    /**
     * Render this channel as XML conforming to the RSS 0.91 specification,
     * to the specified writer.
     *
     * @param writer The writer to render output to
     */
    void render(final PrintWriter writer) {

        writer.println("    <item>");

        writer.print("      <title>");
        writer.print(title);
        writer.println("</title>");

        writer.print("      <link>");
        writer.print(link);
        writer.println("</link>");

        if (description != null) {
            writer.print("      <description>");
            writer.print(description);
            writer.println("</description>");
        }

        writer.println("    </item>");

    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

}
