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

public class DuplicateComponentClassRegistrationException extends PicoRegistrationException
{
    private Class clazz;

    public DuplicateComponentClassRegistrationException(Class clazz)
    {
        this.clazz = clazz;
    }

    public Class getDuplicateClass()
    {
        return clazz;
    }

    public String getMessage()
    {
        return "Class " + clazz.getName() + " duplicated";
    }
}
