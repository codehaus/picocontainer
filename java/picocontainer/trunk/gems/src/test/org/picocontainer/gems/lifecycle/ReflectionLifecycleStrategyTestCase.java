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
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
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
        strategy.dispose(startable);
    }

    public void testDisposable(){
        Object disposable = mockComponent(false, true);
        strategy.start(disposable);
        strategy.stop(disposable);
        strategy.dispose(disposable);
    }

    public void testNotStartableNorDisposable(){
        Object serializable = mock(Serializable.class);
        assertFalse(strategy.hasLifecycle(serializable.getClass()));
        strategy.start(serializable);
        strategy.stop(serializable);
        strategy.dispose(serializable);
    }
    
    private Object mockComponent(boolean startable, boolean disposable) {
        Mock mock = mock(Serializable.class);
        if ( startable ) {
            mock = mock(Startable.class);
            mock.expects(atLeastOnce()).method("start");
            mock.expects(atLeastOnce()).method("stop");
        }
        if ( disposable ) {
            mock = mock(Disposable.class);
            mock.expects(atLeastOnce()).method("dispose");
        }
        return mock.proxy();
    }
}
