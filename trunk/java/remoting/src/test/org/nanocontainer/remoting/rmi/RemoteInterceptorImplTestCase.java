package org.nanocontainer.remoting.rmi;

import junit.framework.TestCase;
import org.nanocontainer.proxy.CGLIBProxyFactory;
import org.nanocontainer.proxy.ProxyFactory;
import org.nanocontainer.remoting.ByRefKey;
import org.nanocontainer.remoting.Invocation;
import org.nanocontainer.remoting.RemotingInterceptor;
import org.nanocontainer.remoting.Thang;
import org.nanocontainer.remoting.Thing;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class RemoteInterceptorImplTestCase extends TestCase {
    public void testInvocationsArePassedThrough() throws RemoteException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MutablePicoContainer pico = new DefaultPicoContainer();
        ByRefKey thangKey = new ByRefKey("thang");
        pico.registerComponentImplementation(thangKey, Thang.class);
        pico.registerComponentImplementation(ArrayList.class);
        List serverList = (List) pico.getComponentInstance(ArrayList.class);

        ProxyFactory proxyFactory = new CGLIBProxyFactory();
        RemotingInterceptor remoteInterceptor = new RemoteInterceptorImpl(RegistryHelper.getRegistry(), pico, thangKey, proxyFactory);
        Collection collection = (Collection) remoteInterceptor.invoke(new Invocation("getList", null, null));
        assertSame(serverList, collection);
    }

    public void testByRefComponentsShouldBeProxied() throws RemoteException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MutablePicoContainer pico = new DefaultPicoContainer();
        ProxyFactory proxyFactory = new CGLIBProxyFactory();
        ByRefKey thingKey = new ByRefKey("thing");
        ByRefKey thangKey = new ByRefKey("thang");
        ComponentAdapter thingAdapter = pico.registerComponentImplementation(thingKey, Thing.class);
        pico.registerComponentImplementation(thangKey, Thang.class);
        pico.registerComponentImplementation(ArrayList.class);

        RemoteInterceptorImpl remoteInterceptor = new RemoteInterceptorImpl(RegistryHelper.getRegistry(), pico, thingKey, proxyFactory);
        remoteInterceptor.bind(thingAdapter);
        Thang thang = (Thang) remoteInterceptor.invoke(new Invocation("getThang", null, null));
        assertTrue(proxyFactory.isProxyClass(thang.getClass()));
        Thang serverThang = (Thang) pico.getComponentInstance(thangKey);

        assertNotSame(serverThang, thang);
    }
}