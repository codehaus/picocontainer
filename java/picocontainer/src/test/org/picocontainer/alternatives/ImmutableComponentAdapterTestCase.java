package org.picocontainer.alternatives;

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.InstanceComponentAdapter;
import org.picocontainer.alternatives.ImmutableComponentAdapter;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class ImmutableComponentAdapterTestCase extends TestCase {

    public void testGetComponentKey() {
        ComponentAdapter ca = new InstanceComponentAdapter(Map.class, new HashMap());
        ImmutableComponentAdapter ica = new ImmutableComponentAdapter(ca);
        Object key = ica.getComponentKey();
        assertNotNull(key);
        assertTrue(key instanceof Class);
        Class clazz = (Class) key;
        assertSame(Map.class, clazz);
    }

    public void testGetComponentInstance() {
        ComponentAdapter ca = new InstanceComponentAdapter(Map.class, new HashMap());
        ImmutableComponentAdapter ica = new ImmutableComponentAdapter(ca);
        Object comp = ica.getComponentInstance();
        assertNotNull(comp);
        assertTrue(comp instanceof HashMap);
    }

    public void testGetComponentImplementation() {
        ComponentAdapter ca = new InstanceComponentAdapter(Map.class, new HashMap());
        ImmutableComponentAdapter ica = new ImmutableComponentAdapter(ca);
        Object impl = ica.getComponentImplementation();
        assertNotNull(impl);
    }

    public void testDelegationOfVerify() {
        ImmutableComponentAdapter ica = new ImmutableComponentAdapter(new ComponentAdapter() {
            public Object getComponentKey() {
                return null;
            }

            public Class getComponentImplementation() {
                return null;
            }

            public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException {
                return null;
            }

            public PicoContainer getContainer() {
                return null;
            }

            public void setContainer(PicoContainer picoContainer) {

            }

            public void verify() throws PicoVerificationException {
                throw new UnsupportedOperationException("Forced barf!");
            }
        });

        try {
            ica.verify();
            fail("wrong!");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public void testSetContainer() {
        ComponentAdapter ca = new InstanceComponentAdapter(Map.class, new HashMap());
        ImmutableComponentAdapter ica = new ImmutableComponentAdapter(ca);
        try {
            ica.setContainer(null);
            fail("should have barfed");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

}
