/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.dynaop;

import org.nanocontainer.nanoaop.AbstractNanoaopTestCase;
import org.nanocontainer.nanoaop.AspectablePicoContainer;
import org.nanocontainer.nanoaop.AspectablePicoContainerFactory;
import org.nanocontainer.nanoaop.Dao;
import org.nanocontainer.nanoaop.DaoImpl;
import org.nanocontainer.nanoaop.LoggingInterceptor;
import org.nanocontainer.nanoaop.PointcutsFactory;

/**
 * @author Stephen Molitor
 */
public class DynaopAspectablePicoContainerFactoryTestCase extends AbstractNanoaopTestCase {

    public void testCreateInterceptor() {
        StringBuffer log = new StringBuffer();

        AspectablePicoContainerFactory factory = new DynaopAspectablePicoContainerFactory();
        AspectablePicoContainer pico = factory.createContainer();
        PointcutsFactory cuts = pico.getPointcutsFactory();

        pico.registerComponentImplementation(Dao.class, DaoImpl.class);
        pico.registerInterceptor(cuts.instancesOf(Dao.class), cuts.allMethods(), new LoggingInterceptor(log));

        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        verifyIntercepted(dao, log);
    }

}