package org.nanocontainer.remoting;

import org.nanocontainer.remoting.rmi.RemoteInterceptorImpl;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;

import com.thoughtworks.proxy.ProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class NanoNamingImpl extends UnicastRemoteObject implements NanoNaming {
    private final Registry registry;
    private final PicoContainer pico;
    private final ProxyFactory proxyFactory;

    public NanoNamingImpl(Registry registry, PicoContainer pico, ProxyFactory proxyFactory) throws RemoteException {
        this.registry = registry;
        this.pico = pico;
        this.proxyFactory = proxyFactory;
    }

    public Object lookup(ByRefKey key) throws RemoteException {
        RemoteInterceptorImpl remoteInterceptor = new RemoteInterceptorImpl(registry, pico, key, proxyFactory);
        ClientInvoker clientInvocationHandler = new ClientInvoker(key, remoteInterceptor);
        ComponentAdapter componentAdapter = pico.getComponentAdapter(key);
        remoteInterceptor.bind(componentAdapter);
        return proxyFactory.createProxy(
                new Class[]{
                    componentAdapter.getComponentImplementation(),
                    Serializable.class,
                    KeyHolder.class
                },
                clientInvocationHandler);
    }

    public void bind(String name) throws RemoteException {
        registry.rebind(name, this);
    }
}