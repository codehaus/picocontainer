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

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;

import java.util.Hashtable;

import javax.naming.InitialContext;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.gems.ThreadLocalComponentAdapter;


/**
 * {@link ComponentAdapterFactory}for EJB components. The instantiated components are cached
 * for each {@link Thread}.
 * 
 * @author J&ouml;rg Schaible
 */
public class EJBClientComponentAdapterFactory implements ComponentAdapterFactory {

    private final Hashtable m_environment;
    private final boolean m_earlyBinding;
    private final ProxyFactory m_proxyFactory;

    /**
     * Construct an EJBClientComponentAdapterFactory using the default {@link InitialContext}
     * and late binding and JDK {@link java.lang.reflect.Proxy}instances.
     */
    public EJBClientComponentAdapterFactory() {
        this(new StandardProxyFactory());
    }

    /**
     * Construct an EJBClientComponentAdapterFactory using the default {@link InitialContext}
     * and late binding.
     * 
     * @param proxyFactory The {@link ProxyFactory}to use.
     */
    public EJBClientComponentAdapterFactory(final ProxyFactory proxyFactory) {
        this(null, proxyFactory);
    }

    /**
     * Construct an EJBClientComponentAdapterFactory using an {@link InitialContext}with a
     * special environment and JDK {@link java.lang.reflect.Proxy}instances.
     * 
     * @param environment the environment and late binding.
     */
    public EJBClientComponentAdapterFactory(final Hashtable environment) {
        this(environment, false);
    }

    /**
     * Construct an EJBClientComponentAdapterFactory using an {@link InitialContext}with a
     * special environment.
     * 
     * @param environment the environment and late binding.
     * @param proxyFactory The {@link ProxyFactory}to use.
     */
    public EJBClientComponentAdapterFactory(final Hashtable environment, final ProxyFactory proxyFactory) {
        this(environment, false, proxyFactory);
    }

    /**
     * Construct an EJBClientComponentAdapterFactory using an {@link InitialContext}with a
     * special environment and binding type and JDK {@link java.lang.reflect.Proxy}instances.
     * 
     * @param environment the environment.
     * @param earlyBinding <code>true</code> for early binding of the
     *                   {@link EJBClientComponentAdapter}.
     */
    public EJBClientComponentAdapterFactory(final Hashtable environment, final boolean earlyBinding) {
        this(environment, earlyBinding, new StandardProxyFactory());
    }

    /**
     * Construct an EJBClientComponentAdapterFactory using an {@link InitialContext}with a
     * special environment and binding type.
     * 
     * @param environment the environment.
     * @param earlyBinding <code>true</code> for early binding of the
     *                   {@link EJBClientComponentAdapter}.
     * @param proxyFactory The {@link ProxyFactory}to use.
     */
    public EJBClientComponentAdapterFactory(final Hashtable environment, final boolean earlyBinding, final ProxyFactory proxyFactory) {
        super();
        m_environment = environment;
        m_earlyBinding = earlyBinding;
        m_proxyFactory = proxyFactory;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.picocontainer.defaults.ComponentAdapterFactory#createComponentAdapter(java.lang.Object,
     *           java.lang.Class, org.picocontainer.Parameter[])
     */
    public ComponentAdapter createComponentAdapter(
            final Object componentKey, final Class componentImplementation, final Parameter[] parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException {
        return createComponentAdapter(componentKey.toString(), componentImplementation);
    }

    /**
     * Creates a {@link ComponentAdapter}for EJB objects.
     * 
     * @param componentKey The key used to lookup the {@link InitialContext}.
     * @param componentImplementation The home interface.
     * @see org.picocontainer.defaults.ComponentAdapterFactory#createComponentAdapter(java.lang.Object,
     *           java.lang.Class, org.picocontainer.Parameter[])
     * @return Returns the created {@link ComponentAdapter}
     * @throws PicoIntrospectionException if the home interface of the EJB could not
     *                     instanciated.
     * @throws AssignabilityRegistrationException if the <code>componentImplementation</code>
     *                     does not extend {@link javax.ejb.EJBHome}.
     */
    public ComponentAdapter createComponentAdapter(final String componentKey, final Class componentImplementation)
            throws PicoIntrospectionException, AssignabilityRegistrationException {
        try {
            return new ThreadLocalComponentAdapter(
                    new EJBClientComponentAdapter(
                            componentKey.toString(), componentImplementation, m_environment, m_earlyBinding, m_proxyFactory),
                    m_proxyFactory);
        } catch (final ClassNotFoundException e) {
            throw new PicoIntrospectionException("Home interface not found", e);
        }
    }

}
