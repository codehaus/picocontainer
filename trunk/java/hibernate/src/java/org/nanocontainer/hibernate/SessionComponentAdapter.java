/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.hibernate;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;

import java.util.HashSet;

/**
 * Provides instance of hibernate session. this component will open new session of every request,
 * and thus all management shall be done by component.
 * it's also unvise to cache this session by CachingComponentAdapter, because session may
 * become invalid due to some exception.
 *
 * @author Konstantin Pribluda
 * @version $Revision$
 * @deprecated this component adapter requires ugly scripts to set up in nanocontainer, and is deprecated
 * because of constructable session
 */
public class SessionComponentAdapter implements ComponentAdapter {

    private Parameter sessionFactoryParameter = null;
    private final Object componentKey;

    /**
     * construct adapter with net.sf.hibernate.Session.class as key
     * and dependecy to net.sf.hibernate.SessionFactory.class
     */
    public SessionComponentAdapter() {
        this(Session.class, null);
    }

    /**
     * construct component adapter with specified key and dependecy to
     * net.sf.hibernate.SessionFactory.class
     */
    public SessionComponentAdapter(Object componentKey) {
        this(componentKey, null);
    }

    /**
     * register adapter with given key and parameter
     */
    public SessionComponentAdapter(Object componentKey, Parameter parameter) {
        this.componentKey = componentKey;
        this.sessionFactoryParameter = parameter == null ? new ComponentParameter() : parameter;
    }

    public Object getComponentKey() {
        return componentKey;
    }

    public Class getComponentImplementation() {
        return Session.class;
    }

    public Object getComponentInstance(PicoContainer picoContainer) throws PicoInitializationException, PicoIntrospectionException {
        try {
            return ((SessionFactory)sessionFactoryParameter.resolveInstance(picoContainer, this, SessionFactory.class)).openSession();
        } catch (HibernateException he) {
            throw new PicoInitializationException(he);
        }

    }

    public void verify(final PicoContainer picoContainer) throws PicoIntrospectionException {
        HashSet unsatisfiableDependencies = new HashSet();
        unsatisfiableDependencies.add(SessionFactory.class);

        if (!sessionFactoryParameter.isResolvable(picoContainer, SessionComponentAdapter.this, SessionFactory.class)) {
            throw new UnsatisfiableDependenciesException(SessionComponentAdapter.this, unsatisfiableDependencies);
        }
    }

    public void accept(PicoVisitor visitor) {
        visitor.visitComponentAdapter(this);
    }
}
