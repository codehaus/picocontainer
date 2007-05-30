package org.nanocontainer.script;

import org.nanocontainer.NanoContainer;
import org.nanocontainer.DefaultNanoContainer;
import org.picocontainer.defaults.ComponentFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.containers.EmptyPicoContainer;
import org.picocontainer.adapters.CachingBehaviorFactory;
import org.picocontainer.adapters.AnyInjectionFactory;

import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContainerElementHelper {
    public static NanoContainer makeNanoContainer(ComponentFactory caf, PicoContainer parent, ClassLoader classLoader) {
        if (parent == null) {
            parent = new EmptyPicoContainer();
        }
        if (caf == null) {
            caf = new CachingBehaviorFactory().forThis(new AnyInjectionFactory());
        }
        return new DefaultNanoContainer(classLoader, new DefaultPicoContainer(caf, parent));

    }

    public static void debug(List arg0, Map arg1) {
        System.out.println("-->debug " + arg0.size() + " " + arg1.size());
        for (int i = 0; i < arg0.size(); i++) {
            Object o = arg0.get(i);
            System.out.println("--> arg0[" + i + "] " + o);

        }
        Set keys = arg1.keySet();
        int i = 0;
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            System.out.println("--> arg1[" + i++ + "] " + o + ", " + arg1.get(o));

        }
    }

}
