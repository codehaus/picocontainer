/*****************************************************************************
 * Copyright (Cc) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.defaults;

import picocontainer.PicoInstantiationException;

public class UnsatisfiedDependencyInstantiationException extends PicoInstantiationException {
    private Class classThatNeedsDeps;
    private Class neededDep;

    public UnsatisfiedDependencyInstantiationException(Class classThatNeeds, Class neededDep) {
        this.classThatNeedsDeps = classThatNeeds;
        this.neededDep = neededDep;
    }

    public String getMessage() {
        return "Component " + classThatNeedsDeps.getName() + " needs " +
                (neededDep == null ? "unnamed dependencies" : neededDep.getName());
    }

    public Class getClassThatNeedsDeps() {
        return classThatNeedsDeps;
    }
}
