package org.apache.commons.jelly;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.expression.ConstantExpression;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.expression.ExpressionFactory;

public class TJTagLibrary extends TagLibrary {

	public static final String NS = "jelly:test";

	private static final ExpressionFactory TEST_FACTORY = new ExpressionFactory() {

		/* (non-Javadoc)
		 * @see org.apache.commons.jelly.expression.ExpressionFactory#createExpression(java.lang.String)
		 */
		@Override
		public Expression createExpression(String text) throws JellyException {
			return new ConstantExpression("${TEST FACTORY: " + text + "}");
		}
	};
	
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
