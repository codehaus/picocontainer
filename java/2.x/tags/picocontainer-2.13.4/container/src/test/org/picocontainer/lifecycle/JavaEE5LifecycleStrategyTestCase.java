/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import static org.junit.Assert.assertEquals;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.junit.Before;
import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.monitors.NullComponentMonitor;

/**
 *
 * @author Mauro Talevi
 */
public class JavaEE5LifecycleStrategyTestCase {

    MutablePicoContainer pico;

    public static class ProPostAnnotationJava5Startable {

        StringBuilder sb;

        public ProPostAnnotationJava5Startable(StringBuilder sb) {
            this.sb = sb;
        }

        @PostConstruct
        public void post() {
            sb.append("post()");
        }

        @PreDestroy
        public void pre() {
            sb.append("pre()");
        }

    }


    private LifecycleStrategy strategy;

    @Before
    public void setUp(){
        strategy = new JavaEE5LifecycleStrategy(new NullComponentMonitor());
        pico = new DefaultPicoContainer(new Caching(), strategy, new EmptyPicoContainer());
        pico.addComponent(StringBuilder.class);
        pico.addComponent(ProPostAnnotationJava5Startable.class);
    }

    @Test public void testStartable(){
        pico.start();
        assertEquals("post()", pico.getComponent(StringBuilder.class).toString());
    }

    @Test public void testStopHasNoMeaning(){
        pico.start();
        pico.stop();
        assertEquals("post()", pico.getComponent(StringBuilder.class).toString());
    }

    @Test public void testDispose(){
        pico.start();
        pico.dispose();
        assertEquals("post()pre()", pico.getComponent(StringBuilder.class).toString());
    }

    @Test public void testSerializable(){
    }

}