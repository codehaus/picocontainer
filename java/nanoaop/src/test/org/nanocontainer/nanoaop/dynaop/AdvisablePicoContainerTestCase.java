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

import junit.framework.TestCase;

import org.nanocontainer.nanoaop.AdvisablePicoContainer;
import org.nanocontainer.nanoaop.PointcutFactory;

/**
 * @author Stephen Molitor
 */
public class AdvisablePicoContainerTestCase extends TestCase {

    private AdvisablePicoContainer pico = new DynaopAdvisablePicoContainer();
    private PointcutFactory cuts = pico.getPointcutFactory();
    
    public void testInterceptor() {
        StringBuffer log = new StringBuffer();        
        pico.registerComponentImplementation(Dao.class, DaoImpl.class);
        pico.addInterceptor(cuts.instancesOf(Dao.class), cuts.allMethods(), new LoggingAdvice(log));

        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        verifyIntercepted(dao, log);
    }

    public void testComponentInterceptor() {
        StringBuffer log = new StringBuffer();        
        pico.registerComponentImplementation(Dao.class, DaoImpl.class);
        pico.addInterceptor(cuts.component(Dao.class), cuts.allMethods(), new LoggingAdvice(log));

        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        verifyIntercepted(dao, log);
    }
    
    public void testContainerSuppliedInterceptor() {
        pico.registerComponentImplementation(Dao.class, DaoImpl.class);
        pico.registerComponentImplementation("log", StringBuffer.class);
        pico.registerComponentImplementation(LoggingAdvice.class);
        pico.addInterceptor(cuts.instancesOf(Dao.class), cuts.allMethods(), cuts.component(LoggingAdvice.class));

        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        StringBuffer log = (StringBuffer) pico.getComponentInstance("log");
        verifyIntercepted(dao, log);
    }
    
    public void testContainerSuppliedComponentInterceptor() {
        pico.registerComponentImplementation(Dao.class, DaoImpl.class);
        pico.registerComponentImplementation("log", StringBuffer.class);
        pico.registerComponentImplementation(LoggingAdvice.class);
        pico.addInterceptor(cuts.component(Dao.class), cuts.allMethods(), cuts.component(LoggingAdvice.class));

        Dao dao = (Dao) pico.getComponentInstance(Dao.class);
        StringBuffer log = (StringBuffer) pico.getComponentInstance("log");
        verifyIntercepted(dao, log);
    }

    private void verifyIntercepted(Dao dao, StringBuffer log) {
        assertEquals("", log.toString());
        dao.loadData();
        assertEquals("startstop", log.toString());
    }

}