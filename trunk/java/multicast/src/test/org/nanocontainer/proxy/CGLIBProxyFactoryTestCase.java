package org.nanocontainer.proxy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class CGLIBProxyFactoryTestCase extends AbstractProxyFactoryTestCase {

    public static class InitialCapacityArrayList extends ArrayList {
        public InitialCapacityArrayList() {
        }

        public InitialCapacityArrayList(int initialCapacity) {
            super(initialCapacity);
        }
    }

    protected ProxyFactory createProxyFactory() {
        return new CGLIBProxyFactory();
    }

    public void testConcreteClassCanBeProxied() {
        ProxyFactory proxyFactory = createProxyFactory();
        List target = new InitialCapacityArrayList(3);
        InvocationInterceptor forwardInterceptor = new ForwardingInterceptor(target);
        Object listProxy = proxyFactory.createProxy(InitialCapacityArrayList.class, null, forwardInterceptor);
        List proxyList = (List) listProxy;
        proxyList.add("hello");
        assertEquals("hello", target.get(0));
    }

}