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

import java.io.Serializable;

/**
 * Class InvocationException
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class InvocationException extends RuntimeException implements Serializable {

    private Throwable m_throwableCause;

    /**
     * Constructor InvocationException
     *
     * @param msg the message that is the root cause.
     */
    public InvocationException(String msg) {
        super(msg);
    }

    public InvocationException(String message, Throwable cause) {
        super(message);
        m_throwableCause = cause;
    }

    public Throwable getCause() {
        return m_throwableCause;
    }

    public String getMessage() {
        return super.getMessage() + (m_throwableCause != null ? " : " + m_throwableCause.getMessage() : "");
    }

}
