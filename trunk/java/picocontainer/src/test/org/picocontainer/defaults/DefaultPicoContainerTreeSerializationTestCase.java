/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.tck.AbstractPicoContainerTestCase;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

/**
 * @author Thomas Heller
 * @author Paul Hammant
 */
public class DefaultPicoContainerTreeSerializationTestCase extends AbstractPicoContainerTestCase {
    protected MutablePicoContainer createPicoContainer() {
        DefaultPicoContainer container = new DefaultPicoContainer();
        DefaultPicoContainer child = new DefaultPicoContainer();

        child.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        child.registerComponentImplementation(DependsOnTouchable.class);
        container.addChild(child);
        return container;
    }

    public void testContainerIsDeserializableWithChildren() throws PicoException, PicoInitializationException,
            IOException, ClassNotFoundException {

        PicoContainer pico = createPicoContainerWithTouchableAndDependency();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(pico);

        // yeah yeah, is not needed.
        pico = null;

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

        pico = (PicoContainer) ois.readObject();

        Collection children = pico.getChildContainers();
        assertNotNull(children);
        assertEquals(1, children.size());
    }
}
