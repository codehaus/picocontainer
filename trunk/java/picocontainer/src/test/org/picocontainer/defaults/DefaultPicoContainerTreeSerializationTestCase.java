/*****************************************************************************
 * Copyright (ComponentC) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.*;
import org.picocontainer.tck.AbstractPicoContainerTestCase;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.io.*;

/**
 * @author Thomas Heller
 * @author Paul Hammant
 */
public class DefaultPicoContainerTreeSerializationTestCase extends AbstractPicoContainerTestCase {
    protected MutablePicoContainer createPicoContainer() {
        DefaultPicoContainer parent = new DefaultPicoContainer();
        DefaultPicoContainer child = new DefaultPicoContainer(parent);

        child.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        child.registerComponentImplementation(DependsOnTouchable.class);
        return parent;
    }

    public void testContainerIsDeserializableWithParent() throws PicoException, PicoInitializationException,
            IOException, ClassNotFoundException {

        PicoContainer parent = createPicoContainer();
        MutablePicoContainer child = createPicoContainer();
        child.setParent(parent);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(child);

        // yeah yeah, is not needed.
        child = null;

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

        child = (MutablePicoContainer) ois.readObject();

        assertNotNull(child.getParent());
    }
}
