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
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;

import java.util.HashSet;

/**
 * component adapter providing  hibernate session factory.
 *
 * @author konstantin pribluda
 * @version $Revision$
 */
public class SessionFactoryComponentAdapter implements ComponentAdapter {

    private final Object componentKey;
    private Parameter configurationParameter = null;

    /**
     * construct adapter with net.sf.hibernate.SessionFactory.class as key
     * and dependecy to net.sf.hibernate.cfg.Configuration.class
     */
    public SessionFactoryComponentAdapter() {
        this(SessionFactory.class, null);
    }

    /**
     * construct component adapter with specified key and dependecy to
     * net.sf.hibernate.cfg.Configuration.class
     */
    public SessionFactoryComponentAdapter(Object componentKey) {
        this(componentKey, null);
    }

    /**
     * construct component adapter with given key and specified parameter.
     * in case null parameter is supplied use dependency to
     * net.sf.hibernate.cfg.Configuration.class
     */
    public SessionFactoryComponentAdapter(Object componentKey, Parameter parameter) {
        this.componentKey = componentKey;
        this.configurationParameter = parameter == null ? new ComponentParameter() : parameter;
    }


    /**
     * obtain session factory instance if possible
     */
    public Object getComponentInstance(PicoContainer picoContainer) throws PicoInitializationException, PicoIntrospectionException {
        try {
            return ((Configuration)configurationParameter.resolveInstance(picoContainer, this, Configuration.class)).buildSessionFactory();
        } catch (HibernateException he) {
            throw new PicoInitializationException(he);
        }
    }

    public void verify(final PicoContainer picoContainer) throws PicoVerificationException {
        HashSet unsatisfiableDependencies = new HashSet();
        unsatisfiableDependencies.add(Configuration.class);

        if (!configurationParameter.isResolvable(picoContainer, SessionFactoryComponentAdapter.this, Configuration.class)) {
            throw new UnsatisfiableDependenciesException(SessionFactoryComponentAdapter.this, unsatisfiableDependencies);
        }
    }

    public void accept(PicoVisitor visitor) {
        visitor.visitComponentAdapter(this);
    }

    public Object getComponentKey() {
        return componentKey;
    }

    public Class getComponentImplementation() {
        return SessionFactory.class;
    }
}
