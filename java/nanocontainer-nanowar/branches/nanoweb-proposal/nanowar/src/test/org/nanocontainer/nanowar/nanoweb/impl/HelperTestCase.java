package org.nanocontainer.nanowar.nanoweb.impl;

import junit.framework.TestCase;

import org.nanocontainer.nanowar.nanoweb.Car;
import org.nanocontainer.nanowar.nanoweb.CarConverter;
import org.nanocontainer.nanowar.nanoweb.Converter;
import org.nanocontainer.nanowar.nanoweb.ConverterComponentAdapter;
import org.nanocontainer.nanowar.nanoweb.MyAction;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;

public class HelperTestCase extends TestCase {

    public void testGetConverterForWhenItsInTheLastNode() throws Exception {
        MutablePicoContainer pico1 = new DefaultPicoContainer();
        MutablePicoContainer pico2 = new DefaultPicoContainer(pico1);

        pico1.registerComponentImplementation(MyAction.class);
        pico2.registerComponentImplementation(Car.class);

        // register
        pico2.registerComponent(new ConverterComponentAdapter(Car.class, new CachingComponentAdapter(new ConstructorInjectionComponentAdapter(CarConverter.class, CarConverter.class))));

        Converter converter = Helper.getConverterFor(Car.class, pico2);
        assertNotNull(converter);
        assertTrue(converter instanceof CarConverter);
    }

    public void testGetConverterForWhenItsInTheParentContaner() throws Exception {
        MutablePicoContainer pico1 = new DefaultPicoContainer();
        MutablePicoContainer pico2 = new DefaultPicoContainer(pico1);

        pico1.registerComponentImplementation(MyAction.class);
        pico2.registerComponentImplementation(Car.class);
        
        // register
        pico1.registerComponent(new ConverterComponentAdapter(Car.class, new CachingComponentAdapter(new ConstructorInjectionComponentAdapter(CarConverter.class, CarConverter.class))));

        Converter converter = Helper.getConverterFor(Car.class, pico2);
        assertNotNull(converter);
        assertTrue(converter instanceof CarConverter);
    }


}
