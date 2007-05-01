package org.picocontainer.doc.tutorial.blocks;

import junit.framework.TestCase;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.componentadapters.CachingComponentAdapter;
import org.picocontainer.componentadapters.CachingComponentAdapterFactory;
import org.picocontainer.componentadapters.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.componentadapters.InstanceComponentAdapter;
import org.picocontainer.componentadapters.SetterInjectionComponentAdapterFactory;
import org.picocontainer.componentadapters.SetterInjectionComponentAdapter;
import org.picocontainer.componentadapters.SynchronizedComponentAdapter;
import org.picocontainer.componentadapters.SynchronizedComponentAdapterFactory;
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
                                new SetterInjectionComponentAdapterFactory())));
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
