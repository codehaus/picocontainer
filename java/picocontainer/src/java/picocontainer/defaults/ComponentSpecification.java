/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.defaults;

class ComponentSpecification
{
    private final Class compType;
    private final Class comp;

    public ComponentSpecification(final Class compType, final Class comp)
    {
        this.compType = compType;
        this.comp = comp;
    }

    public Class getComponentType()
    {
        return compType;
    }

    public Class getComponentImplementation()
    {
        return comp;
    }


}
