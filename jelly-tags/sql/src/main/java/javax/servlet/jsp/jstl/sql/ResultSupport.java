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

package javax.servlet.jsp.jstl.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>This class represents the conversion of a ResultSet to
 * a Result object.</p>
 *
 */
public class ResultSupport {

    /**
     * Returns an array of Row objects.
     *
     * @param resultSet the ResultSet object
     * @return the {@code Result} object of the result
     */
    public static Result toResult(final ResultSet resultSet) {
        try {
            return new ResultImpl(resultSet, -1, -1);
        } catch (final SQLException ex) {
            return null;
        }
    }

    /**
     * Returns the Result object of the cached ResultSet limited by maxRows
     *
     * @param resultSet the ResultSet object
     * @param maxRows the maximum number of rows
     * @return the {@code Result} object of the result limited by maxRows
     */
    public static Result toResult(final ResultSet resultSet, final int maxRows) {
        try {
            return new ResultImpl(resultSet, -1, maxRows);
        } catch (final SQLException ex) {
            return null;
        }
    }

}
