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
package org.apache.commons.jelly.tags.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * Checks that a file exists, and if not, then the test will fail.
 *
 * @author <a href="mailto:dion@apache.org">Dion Gillard</a>
 */
public class AssertFileContainsTag extends AssertTagSupport
{
    /** the file to check */
    private File file;
    
    /** content to match */
    private String match;

    /**
     * Do the tag functionality: check the file exists.
     * @param output a place to write text output
     * @throws JellyTagException if the file doesn't exist.
     */
    @Override
    public void doTag(XMLOutput output) throws JellyTagException
    {
        if (match == null)
        {
            throw new MissingAttributeException("match");
        }
        String message = getBodyText();
        if (message == null || message.length() == 0)
        {
            message = "File does not contain '" + match + "'";
        }

        
        if (file == null)
        {
            throw new MissingAttributeException("file");
        }
        else
        {
            if (file.exists() && file.canRead())
            {
                try
                {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    boolean found = false;
                    while ((line = br.readLine()) != null)
                    {
                        if (line.indexOf(match) != -1)
                        {
                            found = true;
                            break;
                        }
                    }
                    br.close();
                    assertTrue(message, found);
                }
                catch (IOException fnfe)
                {
                    throw new JellyTagException(fnfe);
                }
            }
            else
            {
                try
                {
                    throw new JellyTagException("File '" + file.getCanonicalPath() 
                        + "' can't be read.");
                }
                catch (IOException e)
                {
                    throw new JellyTagException(e);
                }
            }
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
    
    /**
     * The content to be checked for. If this text matches some part
     * of the given file, the test will pass.
     */
    public void setMatch(String aString)
    {
        match = aString;
    }
}
