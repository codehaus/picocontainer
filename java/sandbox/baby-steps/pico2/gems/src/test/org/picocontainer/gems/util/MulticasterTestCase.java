package org.picocontainer.gems.util;

import junit.framework.TestCase;

import org.picocontainer.Disposable;
import org.picocontainer.Startable;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.testmodel.RecordingLifecycle;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.factory.StandardProxyFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MulticasterTestCase extends TestCase {
    public void testOrderOfInstantiationShouldBeDependencyOrder() throws Exception {

        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.component("recording", StringBuffer.class);
        pico.component(RecordingLifecycle.Four.class);
        pico.component(RecordingLifecycle.Two.class);
        pico.component(RecordingLifecycle.One.class);
        pico.component(RecordingLifecycle.Three.class);

        ProxyFactory proxyFactory = new StandardProxyFactory();
        Startable startable = (Startable) Multicaster.object(pico, true, proxyFactory);
        Startable stoppable = (Startable) Multicaster.object(pico, false, proxyFactory);
        Disposable disposable = (Disposable) Multicaster.object(pico, false, proxyFactory);

        startable.start();
        stoppable.stop();
        disposable.dispose();

        assertEquals("<One<Two<Three<FourFour>Three>Two>One>!Four!Three!Two!One", pico.getComponent("recording").toString());
    }

}