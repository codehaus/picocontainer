/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

public class UnsatisfiedDependencyStartupException extends PicoStartException
{
    private Class classThatNeedsDeps;

    public UnsatisfiedDependencyStartupException(Class classThatNeedsDeps)
    {
        this.classThatNeedsDeps = classThatNeedsDeps;
    }

    public String getMessage()
    {
        return "Class " + classThatNeedsDeps.getName() + " needs unnamed depenencies";
    }

    public Class getClassThatNeedsDeps()
    {
        return classThatNeedsDeps;
    }
}
