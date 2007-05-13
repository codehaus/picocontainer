package org.picocontainer.gems.adapters;

import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.hotswap.Swappable;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.adapters.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.adapters.CachingComponentAdapter;
import org.picocontainer.adapters.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.adapters.ConstructorInjectionComponentAdapter;
import org.picocontainer.adapters.AnyInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ImplementationHidingComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {

    private ImplementationHidingComponentAdapterFactory implementationHidingComponentAdapterFactory = new ImplementationHidingComponentAdapterFactory(
            new AnyInjectionComponentAdapterFactory());
    private CachingComponentAdapterFactory cachingComponentAdapterFactory = new CachingComponentAdapterFactory(
            implementationHidingComponentAdapterFactory);

    public void testComponentRegisteredWithInterfaceKeyOnlyImplementsThatInterfaceUsingStandardProxyfactory() {

        DefaultPicoContainer pico = new DefaultPicoContainer(new ImplementationHidingComponentAdapterFactory(
                new ConstructorInjectionComponentAdapterFactory()));
        pico.addComponent(Collection.class, ArrayList.class);
        Object collection = pico.getComponent(Collection.class);
        assertTrue(collection instanceof Collection);
        assertTrue(collection instanceof List);
        assertFalse(collection instanceof ArrayList);
    }

    public void testComponentRegisteredWithOtherKeyImplementsAllInterfacesUsingStandardProxyFactory() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new ImplementationHidingComponentAdapterFactory(
                new ConstructorInjectionComponentAdapterFactory()));
        pico.addComponent("list", ArrayList.class);
        Object collection = pico.getComponent("list");
        assertTrue(collection instanceof List);
        assertFalse(collection instanceof ArrayList);
    }

    public void testComponentRegisteredWithOtherKeyImplementsAllInterfacesUsingCGLIBProxyFactory() {
        DefaultPicoContainer pico = new DefaultPicoContainer(new ImplementationHidingComponentAdapterFactory(
                new ConstructorInjectionComponentAdapterFactory()));
        pico.addComponent("list", ArrayList.class);
        Object collection = pico.getComponent("list");
        assertTrue(collection instanceof Collection);
        assertTrue(collection instanceof List);
        assertFalse(collection instanceof ArrayList);
        assertTrue(collection.getClass().getSuperclass().equals(Object.class));
    }

    public void testIHCAFwithCTORandNoCaching() {
        // http://lists.codehaus.org/pipermail/picocontainer-dev/2004-January/001985.html
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new ImplementationHidingComponentAdapter(new ConstructorInjectionComponentAdapter("l", ArrayList.class)));

        List list1 = (List)pico.getComponent("l");
        List list2 = (List)pico.getComponent("l");

        assertNotSame(list1, list2);
        assertFalse(list1 instanceof ArrayList);

        list1.add("Hello");
        assertTrue(list1.contains("Hello"));
        assertFalse(list2.contains("Hello"));
    }

    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return cachingComponentAdapterFactory;
    }

}