/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package picocontainer;

import picocontainer.testmodel.Wilma;
import picocontainer.testmodel.WilmaImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class OveriddenPicoTestContainer extends PicoContainerImpl.Default
{
    private Wilma wilma;
    private List started = new ArrayList();
    private List stopped = new ArrayList();

    public OveriddenPicoTestContainer(Wilma wilma)
    {
        this.wilma = wilma;
    }

    protected Object makeComponentInstance(Class compType, Constructor constructor, Object[] args) throws InstantiationException, IllegalAccessException, InvocationTargetException
    {

        if (constructor.getDeclaringClass() == WilmaImpl.class) {
            return wilma;
        }
        return super.makeComponentInstance(compType, constructor, args);
    }

    protected void startComponent(Object component) {
        started.add(component.getClass());
    }

    protected void stopComponent(Object component) {
        stopped.add(component.getClass());
    }

    public List getStarted() {
        return started;
    }

    public List getStopped() {
        return stopped;
    }
}
