package org.microcontainer.test.hopefullyhidden;

import org.microcontainer.testapi.TestPromotable;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class TestPromotableImpl implements TestPromotable {
    public ClassLoader unHideImplClassLoader() {
        return this.getClass().getClassLoader();
    }
}
