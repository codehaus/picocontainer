/* ====================================================================
 * Copyright 2005 NanoContainer Committers
 * Portions copyright 2001 - 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.nanocontainer.remoting.server.monitors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nanocontainer.remoting.common.BadConnectionException;
import org.nanocontainer.remoting.server.ServerMonitor;

import java.io.IOException;

/**
 * Class CommonsLoggingServerMonitor
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class CommonsLoggingServerMonitor implements ServerMonitor {

    public void closeError(Class clazz, String s, IOException e) {
        Log log = LogFactory.getLog(clazz);
        if (log.isDebugEnabled()) {
            log.debug("<closeError>" + s, e);
        }
    }

    public void badConnection(Class clazz, String s, BadConnectionException bce) {
        Log log = LogFactory.getLog(clazz);
        if (log.isDebugEnabled()) {
            log.debug("<badConnection>" + s, bce);
        }
    }

    public void classNotFound(Class clazz, ClassNotFoundException e) {
        Log log = LogFactory.getLog(clazz);
        if (log.isDebugEnabled()) {
            log.debug("<classNotFound>", e);
        }
    }

    public void unexpectedException(Class clazz, String s, Exception e) {
        Log log = LogFactory.getLog(clazz);
        if (log.isDebugEnabled()) {
            log.debug("<unexpectedException>" + s, e);
        }
    }

    public void stopServerError(Class clazz, String s, Exception e) {
        Log log = LogFactory.getLog(clazz);
        if (log.isErrorEnabled()) {
            log.error("<stopServerError>" + s, e);
        }
    }

}
