package org.apache.commons.jelly;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

public class TJTest extends TagSupport {
	
	public static final String TAG_NAME = "test";
	
	@Override
	public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
		invokeBody(output);
	}
}
