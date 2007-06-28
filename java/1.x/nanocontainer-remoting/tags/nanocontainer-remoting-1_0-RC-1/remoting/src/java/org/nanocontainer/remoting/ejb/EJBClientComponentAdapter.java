/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.nanocontainer.remoting.ejb;

import com.thoughtworks.proxy.Invoker;
import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AbstractComponentAdapter;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.PicoInvocationTargetInitializationException;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;

import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.rmi.ConnectException;
import java.rmi.NoSuchObjectException;
import java.util.Hashtable;


/**
 * {@link ComponentAdapter}, that is able to lookup and instantiate an EJB as client.
 * <p>
 * The default mode for this adapter is late binding i.e. the adapter returns a proxy object for
 * the requested type as instance. The lookup for the EJB is started with the first call of the
 * proxy and the stub is created. Any further call will do the same, if the last call was
 * aborted by a remote exception. With early binding the stub wiill be created in the
 * constructor and the initialization of the adapter may fail.
 * </p>
 * <p>
 * The adapter is using internally an own proxy for the stub object. This enables a failover in
 * case of a temporary unavailability of the application server providing the EJB. With every
 * call to a methof of the EJB, the adapter is able to reestablish the connection, if the last
 * call had been failed. For standard cases the adapter throws a
 * {@link ServiceUnavailableException}when a call fails and such a temporary unavailability can
 * be assumed (e.g. because the application is redeployed). Although the adapter tries his best
 * to detect such cases, you might still get some other {@link RuntimeException}when a call is
 * made and the application server is about to stop. In this case you might try a call at some
 * minutes later again, but you should give up after some trials. Same applies to a thrown
 * ServiceUnavailableException although you might have a good chance, that the application
 * server will be available some time later again.
 * </p>
 * <p>
 * If you want to cache the EJB with a
 * {@link org.picocontainer.defaults.CachingComponentAdapter}, you have to use a
 * {@link org.picocontainer.gems.ThreadLocalReference}, since you may not use an instance
 * of the EJB in different threads. Use an {@link EJBClientComponentAdapterFactory}for such a
 * completely transparent {@link ThreadLocal}support.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 */
public class EJBClientComponentAdapter extends AbstractComponentAdapter {

    private final Object m_proxy;

    /**
     * Construct a {@link ComponentAdapter}for an EJB. This constructor implies the home
     * interface follows normal naming conventions. The adapter will use late binding and JDK
     * {@link java.lang.reflect.Proxy}instances.
     * 
     * @param name The EJB's JNDI name.
     * @param type The implemented interface of the EJB.
     * @throws ClassNotFoundException Thrown if the home interface could not be found
     */
    public EJBClientComponentAdapter(final String name, final Class type) throws ClassNotFoundException {
        this(name, type, null, false);
    }

    /**
     * Construct a {@link ComponentAdapter}for an EJB. This constructor implies the home
     * interface follows normal naming conventions. The adapter will use late bining.
     * 
     * @param name The EJB's JNDI name.
     * @param type The implemented interface of the EJB.
     * @param factory The {@link ProxyFactory}to use.
     * @throws ClassNotFoundException Thrown if the home interface could not be found
     */
    public EJBClientComponentAdapter(final String name, final Class type, final ProxyFactory factory) throws ClassNotFoundException {
        this(name, type, null, false, factory);
    }

    /**
     * Construct a {@link ComponentAdapter}for an EJB. This constructor implies the home
     * interface follows normal naming conventions. The adapter will use late binding and JDK
     * {@link java.lang.reflect.Proxy}instances.
     * 
     * @param name The EJB's JNDI name.
     * @param type The implemented interface of the EJB.
     * @param environment The environment {@link InitialContext}to use.
     * @param earlyBinding <code>true</code> if the EJB should be instantiated in the
     *                   constructor.
     * @throws ClassNotFoundException Thrown if the home interface could not be found
     */
    public EJBClientComponentAdapter(final String name, final Class type, final Hashtable environment, final boolean earlyBinding)
            throws ClassNotFoundException {
        this(name, type, type.getClassLoader().loadClass(type.getName() + "Home"), environment, earlyBinding);
    }

    /**
     * Construct a {@link ComponentAdapter}for an EJB. This constructor implies the home
     * interface follows normal naming conventions. The adapter will use late binding.
     * 
     * @param name The EJB's JNDI name.
     * @param type The implemented interface of the EJB.
     * @param environment The environment {@link InitialContext}to use.
     * @param earlyBinding <code>true</code> if the EJB should be instantiated in the
     *                   constructor.
     * @param factory The {@link ProxyFactory}to use.
     * @throws ClassNotFoundException Thrown if the home interface could not be found
     */
    public EJBClientComponentAdapter(
            final String name, final Class type, final Hashtable environment, final boolean earlyBinding, final ProxyFactory factory)
            throws ClassNotFoundException {
        this(name, type, type.getClassLoader().loadClass(type.getName() + "Home"), environment, earlyBinding, factory);
    }

    /**
     * Construct a {@link ComponentAdapter}for an EJB using JDK {@link java.lang.reflect.Proxy}
     * instances.
     * 
     * @param name The EJB's JNDI name.
     * @param type The implemented interface of the EJB.
     * @param homeInterface The home interface of the EJB.
     * @param environment The environment {@link InitialContext}to use.
     * @param earlyBinding <code>true</code> if the EJB should be instantiated in the
     *                   constructor.
     * @throws PicoIntrospectionException Thrown if lookup of home interface fails.
     */
    public EJBClientComponentAdapter(
            final String name, final Class type, final Class homeInterface, final Hashtable environment, final boolean earlyBinding) {
        this(name, type, homeInterface, environment, earlyBinding, new StandardProxyFactory());
    }

    /**
     * Construct a {@link ComponentAdapter}for an EJB.
     * 
     * @param name The EJB's JNDI name.
     * @param type The implemented interface of the EJB.
     * @param homeInterface The home interface of the EJB.
     * @param environment The environment {@link InitialContext}to use.
     * @param earlyBinding <code>true</code> if the EJB should be instantiated in the
     *                   constructor.
     * @param factory The {@link ProxyFactory}to use.
     * @throws PicoIntrospectionException Thrown if lookup of home interface fails.
     */
    public EJBClientComponentAdapter(
            final String name, final Class type, final Class homeInterface, final Hashtable environment,
            final boolean earlyBinding, final ProxyFactory factory) {
        super(name, type);
        if (!EJBHome.class.isAssignableFrom(homeInterface)) {
            throw new AssignabilityRegistrationException(EJBHome.class, homeInterface);
        }
        if (!EJBObject.class.isAssignableFrom(type)) {
            throw new AssignabilityRegistrationException(EJBObject.class, type);
        }
        if (!type.isInterface()) {
            throw new PicoIntrospectionException(type.getName() + " must be an interface");
        }
        final Invoker invoker = new EJBClientInvoker(name, type, homeInterface, environment);
        m_proxy = factory.createProxy(new Class[]{type}, invoker);
        if (earlyBinding) {
            m_proxy.hashCode();
        }
    }

    /**
     * Return the proxy for the EJB instance.
     * 
     * @see org.picocontainer.ComponentAdapter#getComponentInstance(PicoContainer)
     */
    public Object getComponentInstance(final PicoContainer pico) {
        return m_proxy;
    }

    /**
     * This implementation has nothing to verify.
     * 
     * @see org.picocontainer.ComponentAdapter#verify(PicoContainer)
     */
    public void verify(final PicoContainer pico) throws UnsatisfiableDependenciesException {
        // cannot do anything here
    }

    private final static class EJBClientInvoker implements Invoker {
        private final String m_name;
        private final Class m_type;
        private final Class m_home;
        private final Hashtable m_environment;
        private transient Object m_stub;

        private EJBClientInvoker(final String name, final Class type, final Class home, final Hashtable environment) {
            m_name = name;
            m_type = type;
            m_home = home;
            m_environment = environment;
        }

        /**
         * @see com.thoughtworks.proxy.Invoker#invoke(java.lang.Object,
         *           java.lang.reflect.Method, java.lang.Object[])
         */
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            try {
                if (m_stub == null) {
                    m_stub = bind();
                }
                return method.invoke(m_stub, args);
            } catch (final InvocationTargetException e) {
                m_stub = null;
                final Throwable target = e.getTargetException();
                if (target instanceof ConnectException || target instanceof NoSuchObjectException) {
                    // Server meanwhile down or restarted or application has been redeployed
                    throw new ServiceUnavailableException("EJB instance named " + m_name + " no longer available", target);
                } else {
                    throw target;
                }
            } catch (Throwable t) {
                m_stub = null;
                throw t;
            }
        }

        private Object bind() {
            try {
                final InitialContext context = new InitialContext(m_environment);
                final Object ref = context.lookup(m_name);
                final Object proxy = PortableRemoteObject.narrow(ref, m_home);
                final Class homeClass = proxy.getClass();
                final Method create = homeClass.getMethod("create", null);
                if (m_type.isAssignableFrom(create.getReturnType())) {
                    return create.invoke(proxy, null);
                }
                throw new PicoIntrospectionException("Wrong return type of EJBHome implementation", new ClassCastException(create
                        .getReturnType().getName()));
            } catch (final SecurityException e) {
                throw new PicoIntrospectionException("Security Exception occured accessing create method of home interface of "
                        + m_name, e);
            } catch (final NoSuchMethodException e) {
                throw new PicoIntrospectionException("Home interface of " + m_name + " has no create method", e);
            } catch (final NameNotFoundException e) {
                // Server startup, application not bound yet
                throw new ServiceUnavailableException("EJB named " + m_name + " not found", e);
            } catch (final NamingException e) {
                final Throwable rootCause = e.getRootCause();
                if (rootCause != null
                        && (rootCause instanceof SocketTimeoutException || rootCause instanceof NoSuchObjectException)) {
                    // Server down, did not have a connection or JNDI not stuffed yet
                    throw new ServiceUnavailableException("Timeout occured creating EJB named " + m_name, e);
                } else {
                    throw new PicoInitializationException("InitialContext has no EJB named " + m_name, e);
                }
            } catch (final IllegalAccessException e) {
                throw new PicoInitializationException("Cannot access default constructor for " + m_name, e);
            } catch (final InvocationTargetException e) {
                if (e.getTargetException() instanceof RuntimeException) {
                    throw (RuntimeException) e.getTargetException();
                } else if (e.getTargetException() instanceof Error) {
                    throw (Error) e.getTargetException();
                }
                throw new PicoInvocationTargetInitializationException(e.getTargetException());
            }

        }
    }
}
