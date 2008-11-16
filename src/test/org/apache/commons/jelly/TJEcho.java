package org.apache.commons.jelly;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.xml.sax.SAXException;

public class TJEcho extends TagSupport {
	
	public static final String TAG_NAME = "echo";
	
	private String id;
	private String attr;
	private String text;

	@Override
	public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
		text = getBodyText();
		String str = "id=" + id + "; attr=" + attr + "; text=" + text + "\n";
		try {
			output.characters(str.toCharArray(), 0, str.length());
		}catch(SAXException e) {
			throw new JellyTagException();
		}
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the attr
	 */
	public String getAttr() {
		return attr;
	}

	/**
	 * @param attr the attr to set
	 */
	public void setAttr(String attr) {
		this.attr = attr;
	}

}
