package org.picocontainer.doc.tutorial.blocks;

import junit.framework.TestCase;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.adapters.CachingComponentAdapterFactory;
import org.picocontainer.adapters.ConstructorInjectionComponentAdapter;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.adapters.CachingComponentAdapter;
import org.picocontainer.adapters.InstanceComponentAdapter;
import org.picocontainer.adapters.SetterInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.adapters.SetterInjectionComponentAdapterFactory;
import org.picocontainer.adapters.SynchronizedComponentAdapter;
import org.picocontainer.adapters.SynchronizedComponentAdapterFactory;
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
        picoContainer.component(Juicer.class);
        picoContainer.component("My Peeler", Peeler.class);
        picoContainer.component(new Apple());
        // END SNIPPET: register-convenient
        // START SNIPPET: register-direct
        picoContainer.adapter(new InstanceComponentAdapter("Another Apple", new Apple()));
        // END SNIPPET: register-direct
    }

    public void testRegisterEquivalentConvenient() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        // START SNIPPET: register-equivalent-convenient
        picoContainer.component(Juicer.class);
        // END SNIPPET: register-equivalent-convenient
    }

    public void testRegisterEquivalentAtLength() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        // START SNIPPET: register-equivalent-at-length
        picoContainer.adapter(
                new CachingComponentAdapter(
                        new ConstructorInjectionComponentAdapter(Juicer.class, Juicer.class)));
        // END SNIPPET: register-equivalent-at-length
    }

    public void testRegisterDifferentComponentAdapterFactory() {

        // START SNIPPET: register-different-caf
        MutablePicoContainer picoContainer = new DefaultPicoContainer(
                new SynchronizedComponentAdapterFactory(
                        new CachingComponentAdapterFactory(
                                new SetterInjectionComponentAdapterFactory(NullLifecycleStrategy.getInstance()))));
        // END SNIPPET: register-different-caf
    }

    public void testRegisterEquivalentAtLength2() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        // START SNIPPET: register-equivalent-at-length2
        picoContainer.adapter(
                new SynchronizedComponentAdapter(
                        new CachingComponentAdapter(
                                new SetterInjectionComponentAdapter(
                                        JuicerBean.class, JuicerBean.class, (Parameter[])null))));
        // END SNIPPET: register-equivalent-at-length2
    }
}
