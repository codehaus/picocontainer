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
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.defaults.AbstractComponentAdapter;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;

import javax.ejb.EJBHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@link ComponentAdapter}, that is able to lookup and instantiate an EJB as client.
 * <p>If you want to cache the EJB with a {@link org.picocontainer.defaults.CachingComponentAdapter},
 * you have to use a {@link org.nanocontainer.concurrent.ThreadLocalReference}, since you may not use an instance of the EJB
 * in different threads.</p>
 * <p>Use a EJBClientComponnatAdapterFactory for a completely transparent {@link ThreadLocal}
 * support.</p>
 *
 * @author J&ouml;rg Schaible
 */
public class EJBClientComponentAdapter extends AbstractComponentAdapter {

    private final Object m_home;
    private final Method m_create;
    private final String m_ejbName;
    private final Class m_ejbClass;

    /**
     * Construct a {@link ComponentAdapter} for an EJB.
     *
     * @param ejb           The EJB's name.
     * @param homeInterface The home interface of the EJB.
     * @param context       The {@link InitialContext} to use.
     * @throws PicoIntrospectionException Thrown if lookup of home interface fails.
     */
    public EJBClientComponentAdapter(final String ejb, final Class homeInterface, final InitialContext context) {
        super(homeInterface, homeInterface);
        try {
            if (!EJBHome.class.isAssignableFrom(homeInterface)) {
                throw new AssignabilityRegistrationException(EJBHome.class, homeInterface);
            }
            final Object ref = context.lookup(ejb);
            m_home = PortableRemoteObject.narrow(ref, homeInterface);
            final Class homeClass = m_home.getClass();
            m_create = homeClass.getMethod("create", null);
            m_ejbClass = m_create.getReturnType();
            m_ejbName = ejb;
        } catch (SecurityException e) {
            throw new PicoIntrospectionException("Security Exception occured accessing create method of home interface of " + ejb, e);
        } catch (NoSuchMethodException e) {
            throw new PicoIntrospectionException("Home interface of " + ejb + " has no create method", e);
        } catch (NamingException e) {
            throw new PicoIntrospectionException("InitialContext has no EJB named " + ejb, e);
        } catch (ClassCastException e) {
            throw new PicoIntrospectionException("Home interface for " + ejb + " has wrong type.", e);
        }
    }

    /**
     * Instantiate the EJB.
     *
     * @see org.picocontainer.ComponentAdapter#getComponentInstance(PicoContainer)
     */
    public Object getComponentInstance(PicoContainer pico) throws PicoInitializationException, PicoIntrospectionException {
        Object result = null;
        try {
            result = m_create.invoke(m_home, null);
        } catch (IllegalArgumentException e) {
            throw new PicoInitializationException(e);
        } catch (IllegalAccessException e) {
            throw new PicoInitializationException(e);
        } catch (InvocationTargetException e) {
            throw new PicoInitializationException(e);
        }
        return result;
    }

    /**
     * This implementation has nothing to verify.
     *
     * @see org.picocontainer.ComponentAdapter#verify(PicoContainer)
     */
    public void verify(PicoContainer pico) throws UnsatisfiableDependenciesException {
        // cannot do anything here
    }

    public void accept(PicoVisitor visitor) {
        visitor.visitComponentAdapter(this);
    }

    /**
     * @return the name of the EJB.
     * @see org.picocontainer.ComponentAdapter#getComponentKey()
     */
    public Object getComponentKey() {
        return m_ejbName;
    }

    /**
     * @see org.picocontainer.ComponentAdapter#getComponentImplementation()
     */
    public Class getComponentImplementation() {
        return m_ejbClass;
    }
}

