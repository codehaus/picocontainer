/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
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


public class StringRegistrationNanoContainerImpl implements StringRegistrationNanoContainer {

    private final PicoContainer picoContainer;

    public StringRegistrationNanoContainerImpl(Container parentContainer) {
        this.picoContainer = makePicoContainer(parentContainer);
    }

    protected PicoContainer makePicoContainer(Container parentContainer) {
        return new PicoContainerImpl.WithParentContainer(parentContainer);
    }

    public static class Default extends StringRegistrationNanoContainerImpl {
        public Default() {
            super(new DummyContainer());
        }
    }

    public void registerComponent(String compClassName) throws NanoRegistrationException, ClassNotFoundException {
        try {
            picoContainer.registerComponent(StringRegistrationNanoContainerImpl.class.getClassLoader().loadClass(compClassName));
        } catch (PicoRegistrationException e) {
            throw new NanoPicoRegistrationException(e);
        }
    }

    public void registerComponent(String typeClassName, String compClassName) throws NanoRegistrationException, ClassNotFoundException {
        try {
            ClassLoader classLoader = StringRegistrationNanoContainerImpl.class.getClassLoader();
            picoContainer.registerComponent(
                    classLoader.loadClass(typeClassName),
                    classLoader.loadClass(compClassName)
            );
        } catch (PicoRegistrationException e) {
            throw new NanoPicoRegistrationException(e);
        }
    }

    public void start() throws PicoStartException {
            picoContainer.start();
    }

    public void stop() throws PicoStopException {
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


}
