package org.picocontainer.extras;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.defaults.*;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanPropertyComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {

    public static class Foo {
        public String message;

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Failing {
        public void setMessage(String message) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public void testSetProperties() {
        ComponentAdapter adapter = createAdapterCallingSetMessage(Foo.class);
        Foo foo = (Foo) adapter.getComponentInstance(picoContainer);
        assertNotNull(foo);
        assertEquals("hello", foo.message);
    }

    public void testFailingSetter() {
        ComponentAdapter adapter = createAdapterCallingSetMessage(Failing.class);
        try {
            adapter.getComponentInstance(picoContainer);
            fail();
        } catch (PicoInitializationException e) {
        }
    }

    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return new BeanPropertyComponentAdapterFactory(new DefaultComponentAdapterFactory());
    }

    public void testDelegateIsAccessible()  {
        DecoratingComponentAdapter componentAdapter =
                (DecoratingComponentAdapter) createComponentAdapterFactory().createComponentAdapter(Touchable.class, SimpleTouchable.class, null);

        assertNotNull(componentAdapter.getDelegate());
    }

    private ComponentAdapter createAdapterCallingSetMessage(Class impl) {
        BeanPropertyComponentAdapterFactory factory = (BeanPropertyComponentAdapterFactory) createComponentAdapterFactory();

        Map properties = new HashMap();
        properties.put("message", "hello");
        factory.setProperties(impl, properties);

        ComponentAdapter adapter = factory.createComponentAdapter(impl, impl, null);
        return adapter;
    }

}
