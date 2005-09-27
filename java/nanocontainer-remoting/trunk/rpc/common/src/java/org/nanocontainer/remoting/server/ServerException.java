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
package org.nanocontainer.remoting.server;


/**
 * Class ServerException
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class ServerException extends Exception {

    Throwable m_throwable;

    /**
     * Construct an ServerException with a message
     *
     * @param message the message
     */
    public ServerException(String message) {
        super(message);
    }

    /**
     * Construct an ServerException with a message
     *
     * @param message   the message
     * @param throwable the exception
     */
    public ServerException(String message, Throwable throwable) {
        super(message);
        m_throwable = throwable;
    }

    /**
     * Retrieve root cause of the exception.
     *
     * @return the root cause
     */
    public final Throwable getCause() {
        return m_throwable;
    }

}
