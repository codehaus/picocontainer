package org.nanocontainer.nanowar.java5.nanoweb.howto;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import junit.framework.TestCase;

import org.nanocontainer.nanowar.nanoweb.Car;

public class GetACollectionTypeTestCase extends TestCase {

    public Collection<Car> getCars() {
        return null;
    }

    public void setCars(Collection<Car> value) {
    }

    public void testUsingGetter() throws Exception {
        Class k = this.getClass();

        Method getter = k.getMethod("getCars");
        assertTrue(getter.getGenericReturnType() instanceof ParameterizedType);

        ParameterizedType type = (ParameterizedType) getter.getGenericReturnType();
        assertEquals(1, type.getActualTypeArguments().length);
        assertTrue(type.getActualTypeArguments()[0] instanceof Class);
        assertTrue(Car.class.isAssignableFrom((Class) type.getActualTypeArguments()[0]));
    }

    public void testUsingSetter() throws Exception {
        Class k = this.getClass();

        Method setter = k.getMethod("setCars", Collection.class);
        assertTrue(setter.getGenericParameterTypes()[0] instanceof ParameterizedType);

        ParameterizedType type = (ParameterizedType) setter.getGenericParameterTypes()[0];
        assertEquals(1, type.getActualTypeArguments().length);
        assertTrue(type.getActualTypeArguments()[0] instanceof Class);
        assertTrue(Car.class.isAssignableFrom((Class) type.getActualTypeArguments()[0]));
    }
    

}
