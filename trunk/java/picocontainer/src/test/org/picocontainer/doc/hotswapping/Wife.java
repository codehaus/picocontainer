package org.picocontainer.doc.hotswapping;

import org.picocontainer.defaults.ImplementationHidingComponentAdapterFactoryTestCase;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
// START SNIPPET: class
public class Wife implements Woman {
    public final Man partner;

    public Wife(Man partner) {
        this.partner = partner;
    }

    public Man getMan() {
        return partner;
    }
}
// END SNIPPET: class
