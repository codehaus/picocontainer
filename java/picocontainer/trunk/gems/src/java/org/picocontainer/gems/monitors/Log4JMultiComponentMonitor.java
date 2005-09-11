/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/
package org.picocontainer.gems.monitors;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import java.lang.reflect.Member;

public class Log4JMultiComponentMonitor extends Log4JComponentMonitor {

    protected Logger getLogger(Member member) {

        return LogManager.getLogger(member.getDeclaringClass());
    }


}
