package picocontainer;

import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ImplementationHidingComponentFactoryTestCase extends TestCase {

    public void testBasic() throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        ImplementationHidingComponentFactory cf = new ImplementationHidingComponentFactory();
        Constructor ctor = ArrayList.class.getConstructor(new Class[0]);
        Object o = cf.createComponent(List.class, ctor, new Object[0]);
        assertTrue(o instanceof List);
        assertFalse(o instanceof ArrayList);
        ((List) o).add("hello");
    }

}
