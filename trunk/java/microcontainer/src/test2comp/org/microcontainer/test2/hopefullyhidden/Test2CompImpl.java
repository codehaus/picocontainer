package org.megacontainer.test2.hopefullyhidden;

import org.megacontainer.test2.Test2Comp;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class Test2CompImpl implements Test2Comp {
    public ClassLoader unHideImplClassLoader() {
        return this.getClass().getClassLoader();
    }

}
