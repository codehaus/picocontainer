package org.nanocontainer.proxy;

import org.nanocontainer.proxy.StandardProxyFactory;
import org.nanocontainer.proxy.ProxyFactory;
import org.nanocontainer.proxy.AbstractProxyFactoryTestCase;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class StandardProxyFactoryTestCase extends AbstractProxyFactoryTestCase {
    protected ProxyFactory createProxyFactory() {
        return new StandardProxyFactory();
    }
}