/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.nanocontainer.ejb;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.ThreadLocalComponentAdapter;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * {@link ComponentAdapterFactory} for EJB components.
 * The instantiated components are cached for each {@link Thread}.
 * @author J&ouml;rg Schaible
 */
public class EJBClientComponentAdapterFactory
        implements ComponentAdapterFactory {

    private final InitialContext m_initialContext;

    /**
     * Construct an EJBClientComponentAdapterFactory using the default {@link InitialContext}.
     * @throws NamingException
     */
    public EJBClientComponentAdapterFactory() throws NamingException {
        this(new InitialContext());
    }

    /**
     * Construct an EJBClientComponentAdapterFactory using a special {@link InitialContext}.
     * @param initialContext The InitialContext.
     */
    public EJBClientComponentAdapterFactory(final InitialContext initialContext) {
        super();
        m_initialContext = initialContext;
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.defaults.ComponentAdapterFactory#createComponentAdapter(java.lang.Object, java.lang.Class, org.picocontainer.Parameter[])
     */
    public ComponentAdapter createComponentAdapter(
            Object componentKey, Class componentImplementation, Parameter[] parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException,
            NotConcreteRegistrationException {
        return createComponentAdapter(componentKey.toString(), componentImplementation);
    }

    /**
     * Creates a {@link ComponentAdapter} for EJB objects.
     * @param componentKey The key used to lookup the {@link InitialContext}.
     * @param componentImplementation The home interface.
     * @see org.picocontainer.defaults.ComponentAdapterFactory#createComponentAdapter(java.lang.Object, java.lang.Class, org.picocontainer.Parameter[])
     * @return Returns the created {@link ComponentAdapter}
     * @throws PicoIntrospectionException Thrown if the home interface of the EJB could not instanciated.
     * @throws AssignabilityRegistrationException Thrown if the <code>componentImplementation</code> does not extend {@link javax.ejb.EJBHome}.
     * @throws NotConcreteRegistrationException Should not occur.
     */
    public ComponentAdapter createComponentAdapter(
            String componentKey, Class componentImplementation)
            throws PicoIntrospectionException, AssignabilityRegistrationException,
            NotConcreteRegistrationException {
        return new ThreadLocalComponentAdapter(
                new EJBClientComponentAdapter(componentKey, componentImplementation, m_initialContext));
    }

}
