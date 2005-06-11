/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.nanocontainer.ioc.avalon;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.service.ServiceException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.defaults.*;

/**
 * An {@link org.picocontainer.ComponentAdapter} which provides support for Avalon-Framework semantics
 * within a PicoContainer environment.
 * 
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Revision$
 */
public class AvalonComponentAdapter extends AbstractComponentAdapter {
    /**
     * {@inheritDoc}
     * 
     * @param componentKey {@inheritDoc} 
     * @param componentImplementation {@inheritDoc}
     * @throws AssignabilityRegistrationException {@inheritDoc}
     * @throws NotConcreteRegistrationException {@inheritDoc}
     */ 
    public AvalonComponentAdapter(final Object componentKey, final Class componentImplementation) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation);
    }

    /**
     * {@inheritDoc}
     * 
     * @param container {@inheritDoc} 
     * @return {@inheritDoc}
     * @throws PicoInitializationException {@inheritDoc}
     * @throws PicoIntrospectionException {@inheritDoc}
     */ 
    public Object getComponentInstance(PicoContainer container) throws PicoInitializationException, PicoIntrospectionException {
        try {

            final Object instance = getComponentImplementation().newInstance();

            //AvalonUtil.tryToEnableBasicAvalonSupport(container);
            return AvalonUtil.handleAvalonLifecycle(getComponentKey(), instance, container);

        } catch (InstantiationException e) {
            throw new PicoInvocationTargetInitializationException(e);
        } catch (IllegalAccessException e) {
            throw new PicoInvocationTargetInitializationException(e);
        } catch (ContextException e) {
            throw new PicoContextException(e);
        } catch (ServiceException e) {
            throw new PicoServiceException(e);
        } catch (ConfigurationException e) {
            throw new PicoConfigurationException(e);
        } catch (Exception e) {
            throw new PicoAvalonContractException(e);
        }
    }

    /**
     * {@inheritDoc}. Please note that the implementation of this method currently does nothing as it is
     * simply not easily possible to verify an Avalon-Framework component.
     * 
     * @throws UnsatisfiableDependenciesException {@inheritDoc}
     */ 
    public void verify(PicoContainer container) throws PicoVerificationException {
    }
}
