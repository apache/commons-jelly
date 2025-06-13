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

package org.apache.commons.jelly;

import org.apache.commons.jelly.expression.ConstantExpression;
import org.apache.commons.jelly.expression.ExpressionFactory;

public class TJTagLibrary extends TagLibrary {

	public static final String NS = "jelly:test";

	private static final ExpressionFactory TEST_FACTORY = text -> new ConstantExpression("${TEST FACTORY: " + text + "}");

	public TJTagLibrary() {
		super(false);
		registerTag(TJTest.TAG_NAME, TJTest.class);
		registerTag(TJEcho.TAG_NAME, TJEcho.class);
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.jelly.TagLibrary#getExpressionFactory()
	 */
	@Override
    public ExpressionFactory getExpressionFactory() {
		return TEST_FACTORY;
	}

}
