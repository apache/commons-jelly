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
package org.apache.commons.jelly.tags.swing.converters;

import java.awt.Point;
import java.util.StringTokenizer;

import org.apache.commons.beanutils2.Converter;

/**
 * A Converter that turns Strings in the form "x, y" into Point objects
 */
public class PointConverter implements Converter {

    @Override
    public Object convert(final Class type, final Object value) {
        if ( value != null ) {
            final String text = value.toString();
            final StringTokenizer pointEnum = new StringTokenizer( text, "," );
            int x = 0;
            int y = 0;
            if ( pointEnum.hasMoreTokens() ) {
                x = parseNumber( pointEnum.nextToken() );
            }
            if ( pointEnum.hasMoreTokens() ) {
                y = parseNumber( pointEnum.nextToken() );
            }

            // now lets parse the Point...
            return new Point( x, y );
        }
        return null;
    }

    protected int parseNumber(String text) {
        text = text.trim();
        return Integer.parseInt(text);
    }

}