package org.picocontainer.extras;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.defaults.*;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;

import java.lang.reflect.Method;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

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

    public void testSetProperties() throws PicoInitializationException, NoSuchMethodException, IntrospectionException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter adapter = createAdapterCallingSetMessage(Foo.class);
        Foo foo = (Foo) adapter.getComponentInstance(picoContainer);
        assertNotNull(foo);
        assertEquals("hello", foo.message);
    }

    public void testFailingSetter() throws NoSuchMethodException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
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

    public void testDelegateIsAccessible() throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        DecoratingComponentAdapter componentAdapter =
                (DecoratingComponentAdapter) createComponentAdapterFactory().createComponentAdapter(Touchable.class, SimpleTouchable.class, null);

        assertNotNull(componentAdapter.getDelegate());
    }

    private ComponentAdapter createAdapterCallingSetMessage(Class impl) throws PicoIntrospectionException, NoSuchMethodException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        BeanPropertyComponentAdapterFactory.Adapter adapter =
                (BeanPropertyComponentAdapterFactory.Adapter) createComponentAdapterFactory().createComponentAdapter("whatever", impl, null);

        final Method setMessage = Foo.class.getMethod("setMessage", new Class[]{String.class});
        PropertyDescriptor[] pd = adapter.getPropertyDescriptors();
        for (int i = 0; i < pd.length; i++) {
            if(setMessage.equals(pd[i].getWriteMethod())) {
                adapter.setPropertyValue(pd[i], "hello");
                assertEquals("hello", adapter.getPropertValue(pd[i]));
            }
        }
        return adapter;
    }

}
