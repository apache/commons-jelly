package org.apache.commons.jelly.test.impl;

import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test taglibrary
 * 
 * @author <a href="mailto:vinayc@apache.org">Vinay Chandran</a>
 */
public class DummyTagLibrary extends TagLibrary {
    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(DummyTagLibrary.class);

    public DummyTagLibrary() {
        registerTag("dummy", DummyTag.class);
    }

}
