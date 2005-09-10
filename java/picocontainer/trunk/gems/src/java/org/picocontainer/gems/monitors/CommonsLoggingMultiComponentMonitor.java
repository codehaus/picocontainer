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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Member;

public class CommonsLoggingMultiComponentMonitor extends CommonsLoggingComponentMonitor {

    protected Log getLog(Member member) {

        return LogFactory.getLog(member.getDeclaringClass());
    }


}
