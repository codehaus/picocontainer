/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.gems.lifecycle;

import java.io.Serializable;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.Disposable;
import org.picocontainer.Startable;

/**
 * 
 * @author Paul Hammant
 * @author Mauro Talevi
 */
public class ReflectionLifecycleStrategyTestCase extends MockObjectTestCase {

    private ReflectionLifecycleStrategy strategy;
    
    public void setUp(){
        strategy = new ReflectionLifecycleStrategy();
    }

    public void testStartable(){
        Object startable = mockComponent(true, false);
        strategy.start(startable);
        strategy.stop(startable);
    }

    public void testDisposable(){
        Object startable = mockComponent(false, true);
        strategy.dispose(startable);
    }

    public void testNotStartableNorDisposable(){
        Object serializable = mockComponent(false, false);
        try {
            strategy.start(serializable);
            fail("Expected ReflectionLifecycleException");
        } catch ( ReflectionLifecycleException e) {
            assertEquals("start", e.getMessage());
        }
        try {
            strategy.stop(serializable);
            fail("Expected ReflectionLifecycleException");
        } catch ( ReflectionLifecycleException e) {
            assertEquals("stop", e.getMessage());
        }
        try {
            strategy.dispose(serializable);
            fail("Expected ReflectionLifecycleException");
        } catch ( ReflectionLifecycleException e) {
            assertEquals("dispose", e.getMessage());
        }
    }
    
    private Object mockComponent(boolean startable, boolean disposeable) {
        Mock mock = mock(Serializable.class);
        if ( startable ) {
            mock = mock(Startable.class);
            mock.expects(atLeastOnce()).method("start");
            mock.expects(atLeastOnce()).method("stop");
        }
        if ( disposeable ) {
            mock = mock(Disposable.class);
            mock.expects(atLeastOnce()).method("dispose");
        }
        return mock.proxy();
    }
}
