/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package nanocontainer;

import picocontainer.PicoContainer;
import picocontainer.Container;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoStartException;
import picocontainer.PicoContainerImpl;
import picocontainer.PicoStopException;
import picocontainer.DummyContainer;
import java.util.ArrayList;

public class StringRegistrationNanoContainerImpl implements StringRegistrationNanoContainer {

    private final PicoContainer picoContainer;
    private ArrayList classLoaders = new ArrayList();

    public StringRegistrationNanoContainerImpl(Container parentContainer, ClassLoader classLoader) {
        this.picoContainer = makePicoContainer(parentContainer);
        if (classLoader != null) {
            classLoaders.add(classLoader);
        }
    }

    protected PicoContainer makePicoContainer(Container parentContainer) {
        return new PicoContainerImpl.WithParentContainer(parentContainer);
    }

    public static class Default extends StringRegistrationNanoContainerImpl {
        public Default() {
            super(new DummyContainer(), StringRegistrationNanoContainerImpl.class.getClassLoader());
        }
    }

    public static class WithParentContainer extends StringRegistrationNanoContainerImpl {
        public WithParentContainer(Container parentContainer) {
            super(parentContainer, StringRegistrationNanoContainerImpl.class.getClassLoader());
        }
    }

    public static class WithClassLoader extends StringRegistrationNanoContainerImpl {
        public WithClassLoader(ClassLoader classLoader) {
            super(new DummyContainer(), classLoader);
        }
    }

    public void registerComponent(String compClassName) throws PicoRegistrationException, ClassNotFoundException {
        picoContainer.registerComponent(StringRegistrationNanoContainerImpl.class.getClassLoader().loadClass(compClassName));
    }

    public void registerComponent(String typeClassName, String compClassName) throws PicoRegistrationException, ClassNotFoundException {
        boolean registered = false;
        for (int i = 0; i < classLoaders.size(); i++) {
            ClassLoader classLoader = (ClassLoader) classLoaders.get(i);
            try {
                Class typeClass = classLoader.loadClass(typeClassName);
                Class compClass = classLoader.loadClass(compClassName);
                picoContainer.registerComponent(typeClass, compClass);
                registered = true;
                break;
            } catch (ClassNotFoundException e) {
                // OK for the time being.
            }
        }
        if (registered == false) {
            throw new ClassNotFoundException("One of " + typeClassName + " or " + classLoaders + " not found");
        }
    }

    public void start() throws PicoStartException {
        picoContainer.start();
    }

    public void stop() throws PicoStopException {
        picoContainer.stop();
    }

    public boolean hasComponent(Class clazz) {
        return picoContainer.hasComponent(clazz);
    }

    public Object getComponent(Class clazz) {
        return picoContainer.getComponent(clazz);
    }

    public Object[] getComponents() {
        return picoContainer.getComponents();
    }

    public void addComponentClassLoader(ClassLoader classLoader) {
        classLoaders.add(classLoader);
    }
}
