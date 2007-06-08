package org.picocontainer.doc.tutorial.blocks;

import junit.framework.TestCase;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.adapters.CachingBehaviorFactory;
import org.picocontainer.adapters.ConstructorInjectionAdapter;
import org.picocontainer.adapters.CachingBehaviorAdapter;
import org.picocontainer.adapters.InstanceComponentAdapter;
import org.picocontainer.adapters.SetterInjectionAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.adapters.SetterInjectionFactory;
import org.picocontainer.adapters.SynchronizedBehaviorAdapter;
import org.picocontainer.adapters.SynchronizedBehaviorFactory;
import org.picocontainer.doc.introduction.Apple;
import org.picocontainer.doc.introduction.Juicer;
import org.picocontainer.doc.introduction.Peeler;


/**
 * Test case for the snippets used in "Component Adapters and Factories"
 * 
 * @author J&ouml;rg Schaible
 */
public class BuildingBlocksTestCase extends TestCase {
    public void testRegisterConvenient() {
        // START SNIPPET: register-convenient
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        picoContainer.addComponent(Juicer.class);
        picoContainer.addComponent("My Peeler", Peeler.class);
        picoContainer.addComponent(new Apple());
        // END SNIPPET: register-convenient
        // START SNIPPET: register-direct
        picoContainer.addAdapter(new InstanceComponentAdapter("Another Apple", new Apple(), NullLifecycleStrategy.getInstance(),
                                                                        NullComponentMonitor.getInstance()));
        // END SNIPPET: register-direct
    }

    public void testRegisterEquivalentConvenient() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        // START SNIPPET: register-equivalent-convenient
        picoContainer.addComponent(Juicer.class);
        // END SNIPPET: register-equivalent-convenient
    }

    public void testRegisterEquivalentAtLength() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        // START SNIPPET: register-equivalent-at-length
        picoContainer.addAdapter(
                new CachingBehaviorAdapter(
                        new ConstructorInjectionAdapter(Juicer.class, Juicer.class)));
        // END SNIPPET: register-equivalent-at-length
    }

    public void testRegisterDifferentComponentAdapterFactory() {

        // START SNIPPET: register-different-caf
        MutablePicoContainer picoContainer = new DefaultPicoContainer(
                new SynchronizedBehaviorFactory().forThis(new CachingBehaviorFactory().forThis(new SetterInjectionFactory())));
        // END SNIPPET: register-different-caf
    }

    public void testRegisterEquivalentAtLength2() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        // START SNIPPET: register-equivalent-at-length2
        picoContainer.addAdapter(
                new SynchronizedBehaviorAdapter(
                        new CachingBehaviorAdapter(
                                new SetterInjectionAdapter(
                                        JuicerBean.class, JuicerBean.class, (Parameter[])null))));
        // END SNIPPET: register-equivalent-at-length2
    }
}
