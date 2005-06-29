package org.nanocontainer.nanowar.java5.nanoweb;

import java.util.Arrays;

import junit.framework.TestCase;

import org.nanocontainer.nanowar.nanoweb.Car;
import org.nanocontainer.nanowar.nanoweb.CarConverter;
import org.nanocontainer.nanowar.nanoweb.ConverterComponentAdapter;
import org.nanocontainer.nanowar.nanoweb.defaults.OgnlExpressionEvaluator;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;

public class GenericCollectionTypeResolverTestCase extends TestCase {

    protected DefaultPicoContainer pico;

    protected OgnlExpressionEvaluator eval = new OgnlExpressionEvaluator(new GenericCollectionTypeResolver());

    protected void setUp() throws Exception {
        super.setUp();
        pico = new DefaultPicoContainer();
        pico.registerComponent(new ConverterComponentAdapter(Car.class, new ConstructorInjectionComponentAdapter(CarConverter.class, CarConverter.class)));
    }

    public void testCollectionSet() throws Exception {
        MyJava5Action a = new MyJava5Action();
        eval.set(pico, a, "cars", new String[] { "car1", "car2" });

        assertEquals(Arrays.asList(new Car[] { new Car("car1"), new Car("car2") }), a.getCars());
    }

    public void testCollectionAdd() throws Exception {
        MyJava5Action a = new MyJava5Action();
        a.getCars().add(new Car("car1"));
        a.getCars().add(new Car("car2"));
        eval.set(pico, a, "cars+", new String[] { "car3", "car4" });

        assertEquals(Arrays.asList(new Car[] { new Car("car1"), new Car("car2"), new Car("car3"), new Car("car4") }), a.getCars());
    }

    public void testCollectionRemove() throws Exception {
        MyJava5Action a = new MyJava5Action();
        a.getCars().add(new Car("car1"));
        a.getCars().add(new Car("car2"));
        a.getCars().add(new Car("car3"));
        a.getCars().add(new Car("car4"));
        eval.set(pico, a, "cars-", new String[] { "car2", "car4" });

        assertEquals(Arrays.asList(new Car[] { new Car("car1"), new Car("car3") }), a.getCars());
    }

}
