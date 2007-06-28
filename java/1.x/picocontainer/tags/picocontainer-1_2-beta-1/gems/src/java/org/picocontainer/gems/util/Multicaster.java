package org.picocontainer.gems.util;

import com.thoughtworks.proxy.ProxyFactory;
import com.thoughtworks.proxy.toys.multicast.Multicasting;
import org.picocontainer.PicoContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factory for creating a multicaster object that multicasts calls to all
 * components in a PicoContainer instance. This can be used to support additional
 * lifecycle interfaces than PicoContainer's built-in support for Startable and Stoppable.
 *
 * @author Aslak Helles&oslash;y
 * @author Chris Stevenson
 * @author Paul Hammant
 * @version $Revision$
 */
public class Multicaster {
    public static Object object(PicoContainer pico, boolean callInInstantiationOrder, ProxyFactory proxyFactory) {
        List copy = new ArrayList(pico.getComponentInstances());

        if (!callInInstantiationOrder) {
            // reverse the list
            Collections.reverse(copy);
        }
        Object[] targets = copy.toArray();
        return Multicasting.object(proxyFactory, targets);
    }
}