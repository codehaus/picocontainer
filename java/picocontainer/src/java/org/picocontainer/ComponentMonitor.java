/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant & Obie Fernandez & Aslak                    *
 *****************************************************************************/

package org.picocontainer;

import java.lang.reflect.Constructor;

/**
 * @author Paul Hammant & Obie Fernandez & Aslak Hellesoy
 * @version $Revision$
 */
public interface ComponentMonitor {

    void instantiating(Constructor constructor);

    void instantiated(Constructor constructor, long beforeTime, long duration);
    //void instantiationFailed(Constructor constructor, Exception e)


    //ComponentInteraction invoking(Method method, Object instance, String context);

}
