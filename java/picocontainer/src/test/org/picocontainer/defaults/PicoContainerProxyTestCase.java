package org.picocontainer.defaults;

import junit.framework.TestCase;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class PicoContainerProxyTestCase extends TestCase {

    public void testFoo() {
        DefaultPicoContainer dpc = new DefaultPicoContainer();
        dpc.registerComponentImplementation(Map.class, HashMap.class);
        
    }


}
