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

public class AssignabilityRegistrationException extends PicoRegistrationException
{
    private final Class type;
    private final Class clazz;

    public AssignabilityRegistrationException(Class type, Class clazz)
    {
        this.type = type;
        this.clazz = clazz;
    }

    public String getMessage()
    {
        return "The type " + type.getDeclaredClasses() +  "  was not assignable from the class " + clazz.getName();
    }
}
