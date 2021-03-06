/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.gems.behaviors;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.picocontainer.Characteristics;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.adapters.InstanceAdapter;
import org.picocontainer.behaviors.AbstractBehavior;
import org.picocontainer.gems.adapters.Elephant;
import org.picocontainer.gems.adapters.ElephantImpl;
import org.picocontainer.gems.adapters.ElephantProxy;
import org.picocontainer.injectors.AdaptingInjection;
import org.picocontainer.injectors.ConstructorInjection;
import org.picocontainer.injectors.ConstructorInjector;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.tck.AbstractComponentFactoryTest;


public final class AsmImplementationHidingTestCase extends AbstractComponentFactoryTest {

    private final ComponentFactory implementationHidingComponentFactory = new AsmImplementationHiding().wrap(new AdaptingInjection());

    public void testAddComponentUsesImplementationHidingBehavior() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new AsmImplementationHiding().wrap(new ConstructorInjection()));
        pico.addComponent("foo", String.class);
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(HiddenImplementation.class, foo.getClass());
        assertEquals(ConstructorInjector.class, ((AbstractBehavior) foo).getDelegate().getClass());
    }

    public void testAddComponentUsesImplementationHidingBehaviorWithRedundantHideImplProperty() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new AsmImplementationHiding().wrap(new ConstructorInjection()));
        pico.change(Characteristics.HIDE_IMPL).addComponent("foo", String.class);
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(HiddenImplementation.class, foo.getClass());
        assertEquals(ConstructorInjector.class, ((AbstractBehavior) foo).getDelegate().getClass());
    }

    public void testAddComponentNoesNotUseImplementationHidingBehaviorWhenNoCachePropertyIsSpecified() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new AsmImplementationHiding().wrap(new ConstructorInjection()));
        pico.change(Characteristics.NO_HIDE_IMPL).addComponent("foo", String.class);
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(ConstructorInjector.class, foo.getClass());
    }

    public void testAddAdapterUsesImplementationHidingBehavior() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new AsmImplementationHiding().wrap(new ConstructorInjection()));
        pico.addAdapter(new InstanceAdapter("foo", "bar", new NullLifecycleStrategy(), new NullComponentMonitor()));
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(HiddenImplementation.class, foo.getClass());
        assertEquals(InstanceAdapter.class, ((AbstractBehavior) foo).getDelegate().getClass());

    }

    public void testAddAdapterUsesImplementationHidingBehaviorWithRedundantHideImplProperty() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new AsmImplementationHiding().wrap(new ConstructorInjection()));
        pico.change(Characteristics.HIDE_IMPL).addAdapter(new InstanceAdapter("foo", "bar", new NullLifecycleStrategy(), new NullComponentMonitor()));
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(HiddenImplementation.class, foo.getClass());
        assertEquals(InstanceAdapter.class, ((AbstractBehavior) foo).getDelegate().getClass());
    }

    public void testAddAdapterNoesNotUseImplementationHidingBehaviorWhenNoCachePropertyIsSpecified() {
        DefaultPicoContainer pico =
            new DefaultPicoContainer(new AsmImplementationHiding().wrap(new ConstructorInjection()));
        pico.change(Characteristics.NO_HIDE_IMPL).addAdapter(new InstanceAdapter("foo", "bar", new NullLifecycleStrategy(), new NullComponentMonitor()));
        ComponentAdapter foo = pico.getComponentAdapter("foo");
        assertEquals(InstanceAdapter.class, foo.getClass());
    }

    protected ComponentFactory createComponentFactory() {
        return implementationHidingComponentFactory;
    }

    public void testElephantWithoutAsmProxy() throws IOException {
        assertions(new ElephantProxy(new ElephantImpl()));
    }

    public void testElephantWithAsmProxy() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.addAdapter(new HiddenImplementation(new ConstructorInjector("l", ArrayList.class, null, new NullComponentMonitor(), new NullLifecycleStrategy(), false)));
        Elephant elephant = pico.addComponent(Elephant.class, ElephantImpl.class).getComponent(Elephant.class);

        assertions(elephant);

    }

    private void assertions(Elephant foo) throws IOException {
        assertEquals("onetwo", foo.objects("one", "two"));
        assertEquals("onetwo", foo.objectsArray(new String[]{"one"}, new String[]{"two"})[0]);
        assertEquals(3, foo.iint(1, 2));
        assertEquals(3, foo.llong(1, 2));
        assertEquals(6, foo.bbyte((byte) 1, (byte) 2, (byte) 3));
        assertEquals((float) 10, foo.ffloat(1, 2, 3, 4));
        assertEquals((double) 3, foo.ddouble(1, 2));
        assertEquals('c', foo.cchar('a', 'b'));
        assertEquals(3, foo.sshort((short) 1, (short) 2));
        assertEquals(true, foo.bboolean(true, true));
        assertEquals(true, foo.bbooleanArray(new boolean[]{true}, new boolean[]{true})[0]);
    }

}