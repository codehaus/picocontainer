package org.picocontainer.behaviors;

import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.NullLifecycleStrategy;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.annotations.Single;
import org.picocontainer.injectors.SetterInjector;
import org.picocontainer.containers.EmptyPicoContainer;

import java.util.Map;
import java.util.HashMap;

import junit.framework.TestCase;
import com.thoughtworks.xstream.XStream;

public class AdaptiveBehaviorFactoryTestCase extends TestCase {

    public void testCachingBehaviorCanBeAddedByCharacteristics() {
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        ComponentCharacteristic cc = new ComponentCharacteristic();
        ComponentCharacteristics.CACHE.mergeInto(cc);
        ComponentAdapter ca = abf.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), cc, Map.class, HashMap.class);
        assertTrue(ca instanceof CachingBehavior);
        Map map = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertNotNull(map);
        Map map2 = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertSame(map, map2);
        assertFalse(cc.hasUnProcessedEntries());
    }

    public void testCachingBehaviorCanBeAddedByAnnotation() {
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        ComponentCharacteristic cc = new ComponentCharacteristic();
        ComponentAdapter ca = abf.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), cc, Map.class, MyHashMap.class);
        assertTrue(ca instanceof CachingBehavior);
        Map map = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertNotNull(map);
        Map map2 = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertSame(map, map2);
        assertFalse(cc.hasUnProcessedEntries());
    }

    @Single
    public static class MyHashMap extends HashMap {
        public MyHashMap() {
        }
    }

    public void testImplementationHidingBehaviorCanBeAddedByCharacteristics() {
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        ComponentCharacteristic cc = new ComponentCharacteristic();
        ComponentCharacteristics.HIDE.mergeInto(cc);
        ComponentAdapter ca = abf.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), cc, Map.class, HashMap.class);
        assertTrue(ca instanceof ImplementationHidingBehavior);
        Map map = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertNotNull(map);
        assertTrue(!(map instanceof HashMap));

        assertFalse(cc.hasUnProcessedEntries());


    }

    public void testSetterInjectionCanBeTriggereedMeaningAdaptiveInjectorIsUsed() {
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        ComponentCharacteristic cc = new ComponentCharacteristic();
        ComponentCharacteristics.SDI.mergeInto(cc);
        ComponentAdapter ca = abf.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), cc, Map.class, HashMap.class);
        assertTrue(ca instanceof SetterInjector);
        Map map = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertNotNull(map);
        assertFalse(cc.hasUnProcessedEntries());


    }

    public void testCachingAndImplHidingAndThreadSafetySetupCorrectly() {
        AdaptiveBehaviorFactory abf = new AdaptiveBehaviorFactory();
        ComponentCharacteristic cc = new ComponentCharacteristic();
        ComponentCharacteristics.CACHE.mergeInto(cc);
        ComponentCharacteristics.HIDE.mergeInto(cc);
        ComponentCharacteristics.THREAD_SAFE.mergeInto(cc);
        ComponentAdapter ca = abf.createComponentAdapter(new NullComponentMonitor(), new NullLifecycleStrategy(), cc, Map.class, HashMap.class);
        assertTrue(ca instanceof CachingBehavior);
        Map map = (Map)ca.getComponentInstance(new EmptyPicoContainer());
        assertNotNull(map);
        assertTrue(!(map instanceof HashMap));

        XStream xs = new XStream();
        String foo = xs.toXML(ca);

        int ih = foo.indexOf(ImplementationHidingBehavior.class.getName());
        int sb = foo.indexOf(SynchronizedBehavior.class.getName());

        // check right nesting order
        assertTrue(ih>0);
        assertTrue(sb>0);
        assertTrue(sb>ih);

        assertFalse(cc.hasUnProcessedEntries());


    }


}
