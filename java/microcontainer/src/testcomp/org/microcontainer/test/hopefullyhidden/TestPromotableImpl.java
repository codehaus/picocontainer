package org.megacontainer.test.hopefullyhidden;

import org.megacontainer.testapi.TestPromotable;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class TestPromotableImpl implements TestPromotable {
    public ClassLoader unHideImplClassLoader() {
        return this.getClass().getClassLoader();
    }
}
