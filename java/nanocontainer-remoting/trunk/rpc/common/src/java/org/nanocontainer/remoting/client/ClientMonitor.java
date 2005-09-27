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
package org.nanocontainer.remoting.client;

import org.nanocontainer.remoting.commands.Request;

import java.io.IOException;

/**
 * Interface ClientMonitor
 *
 * @author Paul Hammant
 * @version * $Revision: 1.2 $
 */
public interface ClientMonitor {

    /**
     * Method methodCalled
     *
     * @param methodSignature
     * @param duration
     */
    void methodCalled(Class clazz, String methodSignature, long duration, String annotation);

    /**
     * Method methodLogging tests if the implementing class intends to do method logging.
     *
     * @return
     */
    boolean methodLogging();

    /**
     * Method serviceSuspended
     *
     * @param request
     * @param attempt
     * @param suggestedWaitMillis
     */
    void serviceSuspended(Class clazz, Request request, int attempt, int suggestedWaitMillis);

    /**
     * Method serviceAbend
     *
     * @param attempt
     */
    void serviceAbend(Class clazz, int attempt, IOException cause);

    void invocationFailure(Class clazz, String name, InvocationException ie);

    void unexpectedClosedConnection(Class clazz, String name, ConnectionClosedException cce);

    void unexpectedInterruption(Class clazz, String name, InterruptedException ie);
}
