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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Hashtable;

import javax.naming.InitialContext;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.gems.adapters.ThreadLocalComponentAdapter;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;


/**
 * {@link ComponentAdapterFactory}for EJB components. The instantiated components are cached for each {@link Thread}.
 * @author J&ouml;rg Schaible
 */
public class EJBClientComponentAdapterFactory implements ComponentAdapterFactory {

    private final Hashtable environment;
    private final boolean earlyBinding;
    private transient ProxyFactory proxyFactory;

    /**
     * Construct an EJBClientComponentAdapterFactory using the default {@link InitialContext} and late binding.
     */
    public EJBClientComponentAdapterFactory() {
        this(null);
    }

    /**
     * Construct an EJBClientComponentAdapterFactory using an {@link InitialContext} with a special environment.
     * @param environment the environment and late binding
     */
    public EJBClientComponentAdapterFactory(final Hashtable environment) {
        this(environment, false);
    }

    /**
     * Construct an EJBClientComponentAdapterFactory using an {@link InitialContext} with a special environment and
     * binding.
     * @param environment the environment.
     * @param earlyBinding <code>true</code> for early binding of the {@link EJBClientComponentAdapter}.
     */
    public EJBClientComponentAdapterFactory(final Hashtable environment, final boolean earlyBinding) {
        super();
        this.environment = environment;
        this.earlyBinding = earlyBinding;
        this.proxyFactory = new StandardProxyFactory();
    }

    public ComponentAdapter createComponentAdapter(
            final Object componentKey, final Class componentImplementation, final Parameter[] parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException {
        return createComponentAdapter(componentKey.toString(), componentImplementation);
    }

    /**
     * Creates a {@link ComponentAdapter} for EJB objects.
     * @param componentKey the key used to lookup the {@link InitialContext}.
     * @param componentImplementation the home interface.
     * @see org.picocontainer.defaults.ComponentAdapterFactory#createComponentAdapter(java.lang.Object, java.lang.Class,
     *      org.picocontainer.Parameter[])
     * @return Returns the created {@link ComponentAdapter}
     * @throws PicoIntrospectionException if the home interface of the EJB could not instanciated
     * @throws AssignabilityRegistrationException if the <code>componentImplementation</code> does not extend
     *             {@link javax.ejb.EJBHome}
     */
    public ComponentAdapter createComponentAdapter(final String componentKey, final Class componentImplementation)
            throws PicoIntrospectionException, AssignabilityRegistrationException {
        try {
            return new ThreadLocalComponentAdapter(new EJBClientComponentAdapter(
                    componentKey.toString(), componentImplementation, environment, earlyBinding), proxyFactory);
        } catch (final ClassNotFoundException e) {
            throw new PicoIntrospectionException("Home interface not found", e);
        }
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.proxyFactory = new StandardProxyFactory();
    }
}
