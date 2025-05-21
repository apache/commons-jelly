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
package org.apache.commons.jelly.tags.junit;

import java.io.File;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;

/**
 * Checks that a file cant be found.
 */
public class AssertFileNotFoundTag extends AssertTagSupport
{
    /** The file to check */
    private File file;

    /**
     * Do the tag functionality: check the file can't be found.
     * @param output a place to write text output
     * @throws JellyTagException if the file exists.
     */
    @Override
    public void doTag(XMLOutput output) throws JellyTagException
    {
        String message = getBodyText();
        if (message == null || message.length() == 0)
        {
            message = "File exists.";
        }

        
        if (file == null)
        {
            throw new MissingAttributeException("file");
        }
        else
        {
            assertFalse(message, file.exists());
        }
    }
    
    /**
     * The file to be tested. If this file exists, the test will pass.
     * @param aFile the file to test.
     */
    public void setFile(File aFile)
    {
        file = aFile;
    }
}
