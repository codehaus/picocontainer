package org.nanocontainer.multicast;

import junit.framework.TestCase;
import org.nanocontainer.proxy.CGLIBProxyFactory;
import org.nanocontainer.proxy.ProxyFactory;
import org.nanocontainer.proxy.StandardProxyFactory;

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MulticasterFactoryTestCase extends TestCase {
    public static class Thing implements Serializable {
    }

    private void testMulticastingToConcreteClassesShouldWorkWithAnyProxyFactory(ProxyFactory proxyFactory, boolean shouldBeAbleToProxyThing) {
        Map one = new HashMap();
        Map two = new HashMap();
        Thing thingOne = new Thing();
        Thing thingTwo = new Thing();
        one.put("thing", thingOne);
        two.put("thing", thingTwo);

        Map multicastingMap = (Map) proxyFactory.createProxy(HashMap.class, null, new AggregatingInvocationInterceptor(new MulticasterFactory(proxyFactory),
                new Object[]{one, two},
                new MulticastInvoker(),
                proxyFactory));

        Object resultFromPut = multicastingMap.put("thing", "blah");
        if (shouldBeAbleToProxyThing) {
            assertTrue(resultFromPut instanceof Thing);
            assertNotSame(resultFromPut, thingOne);
            assertNotSame(resultFromPut, thingTwo);
        } else {
            assertFalse(resultFromPut instanceof Thing);
//            assertTrue(resultFromPut instanceof Serializable);
        }
        assertEquals("blah", one.get("thing"));
        assertEquals("blah", two.get("thing"));
    }

    public void testMulticastingToConcreteClassesShouldWorkWithStandardProxyFactory() {
        testMulticastingToConcreteClassesShouldWorkWithAnyProxyFactory(new StandardProxyFactory(), false);
    }

    public void testMulticastingToConcreteClassesShouldWorkWithCGLIBProxyFactory() {
        testMulticastingToConcreteClassesShouldWorkWithAnyProxyFactory(new CGLIBProxyFactory(), true);
    }
}