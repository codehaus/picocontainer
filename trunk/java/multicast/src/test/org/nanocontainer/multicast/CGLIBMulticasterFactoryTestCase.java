package org.nanocontainer.multicast;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.ArrayList;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class CGLIBMulticasterFactoryTestCase extends TestCase {
    public void testMulticastingToConcreteClasses() {
        ArrayList one = new ArrayList();
        ArrayList two = new ArrayList();

        MulticasterFactory multicasterFactory = new CGLIBMulticasterFactory();
        ArrayList multicaster = (ArrayList) multicasterFactory.createComponentMulticaster(
                getClass().getClassLoader(),
                Arrays.asList(new ArrayList[]{one, two}),
                false,
                new NullInvocationInterceptor(),
                new MulticastInvoker()
        );
        multicaster.add("hello");
        assertTrue(one.contains("hello"));
        assertTrue(two.contains("hello"));
    }
}