package org.picocontainer.tck;

import junit.framework.TestCase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public abstract class AbstractNullConstructionTestCase extends TestCase {

    protected abstract Class getContainerClass();

    protected abstract Object[] getContainersInstantiationParameters();

    public void testContainerInstansiable() throws InvocationTargetException,
            IllegalAccessException, InstantiationException {
        Class container = getContainerClass();
        Constructor ctor = container.getConstructors()[0];
        ctor.newInstance(getContainersInstantiationParameters());
    }

    public void testContainerNotInstansiableWillNullParams() throws InvocationTargetException,
            IllegalAccessException, InstantiationException {
        Class container = getContainerClass();
        Constructor ctor = container.getConstructors()[0];
        Object[] params = getContainersInstantiationParameters();
        for (int i = 0; i < params.length; i++) {
            Object[] testParams = (Object[]) params.clone();
            testParams[i] = null;
            try {
                ctor.newInstance(testParams);
                fail("Should have barfed with NullPointerException");
            } catch (InvocationTargetException ite) {
                if (ite.getTargetException() instanceof NullPointerException) {
                    // expected
                } else {
                    throw ite;
                }
            }
        }
    }


}
