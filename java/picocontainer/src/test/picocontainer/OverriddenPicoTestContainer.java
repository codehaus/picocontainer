/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

import picocontainer.testmodel.Wilma;
import picocontainer.testmodel.WilmaImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class OverriddenPicoTestContainer extends PicoContainerImpl.WithStartableLifecycleManager
{
    private Wilma wilma;

    public OverriddenPicoTestContainer(Wilma wilma, StartableLifecycleManager slm)
    {
        super(slm);
        this.wilma = wilma;
    }

    protected Object makeComponentInstance(Class compType, Constructor constructor, Object[] args) throws InstantiationException, IllegalAccessException, InvocationTargetException
    {

        if (constructor.getDeclaringClass() == WilmaImpl.class) {
            return wilma;
        }
        return super.makeComponentInstance(compType, constructor, args);
    }


}
