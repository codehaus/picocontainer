package org.nanocontainer.proxy;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractProxyFactoryTestCase extends TestCase {
    public void testConcreteClassCanBeProxied() {
        ProxyFactory proxyFactory = createProxyFactory();
        final ArrayList target = new ArrayList();
        InvocationInterceptor forwardInterceptor = new ForwardingInterceptor(target);
        Object arrayListProxy = proxyFactory.createProxy(ArrayList.class, null, forwardInterceptor);
        List proxyList = (List) arrayListProxy;
        proxyList.add("hello");
        assertEquals("hello", target.get(0));
    }

    protected abstract ProxyFactory createProxyFactory();

}