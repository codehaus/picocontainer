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

import org.picocontainer.PicoInstantiationException;

public class UnsatisfiedDependencyInstantiationException extends PicoInstantiationException {
    private Class classThatNeedsDeps;
    private Object componentKey;
    private Class neededDep;

    public UnsatisfiedDependencyInstantiationException(Class classThatNeeds, Object componentKey, Class neededDep) {
        this.classThatNeedsDeps = classThatNeeds;
        this.componentKey = componentKey;
        this.neededDep = neededDep;
    }

    public String getMessage() {
        return "Component " + classThatNeedsDeps.getName() + " needs " +
                (componentKey == null ? neededDep.getName() : componentKey);
    }

    public Class getClassThatNeedsDeps() {
        return classThatNeedsDeps;
    }
}
