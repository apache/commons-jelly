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

package org.apache.commons.jelly.tags.soap;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UncheckedIOException;

/**
 * Wraps a String as an InputStream. Note that data will be lost for
 * characters not in ISO Latin 1, as a simple char to byte mapping is assumed.
 *
 * @author <a href="mailto:umagesh@apache.org">Magesh Umasankar</a>
 */
public class StringInputStream
    extends InputStream
{
    /** Source string, stored as a StringReader */
    private StringReader in;

    /**
     * Composes a stream from a String
     *
     * @param source The string to read from. Must not be {@code null}.
     */
    public StringInputStream( String source )
    {
        in = new StringReader( source );
    }

    /**
     * Reads from the Stringreader, returning the same value. Note that
     * data will be lost for characters not in ISO Latin 1. Clients
     * assuming a return value in the range -1 to 255 may even fail on
     * such input.
     *
     * @return the value of the next character in the StringReader
     *
     * @throws IOException if the original StringReader fails to be read
     */
    @Override
    public int read() throws IOException
    {
        return in.read();
    }

    /**
     * Closes the Stringreader.
     *
     * @throws IOException if the original StringReader fails to be closed
     */
    @Override
    public void close() throws IOException
    {
        in.close();
    }

    /**
     * Marks the read limit of the StringReader.
     *
     * @param limit the maximum limit of bytes that can be read before the
     *              mark position becomes invalid
     */
    @Override
    public synchronized void mark( final int limit )
    {
        try
        {
            in.mark( limit );
        }
        catch ( IOException ioe )
        {
            throw new UncheckedIOException( ioe.getMessage(), ioe );
        }
    }

    /**
     * Resets the StringReader.
     *
     * @throws IOException if the StringReader fails to be reset
     */
    @Override
    public synchronized void reset() throws IOException
    {
        in.reset();
    }

    /**
     * @see InputStream#markSupported
     */
    @Override
    public boolean markSupported()
    {
        return in.markSupported();
    }
}

