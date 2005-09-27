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
package org.nanocontainer.remoting.common;

import java.io.IOException;

/**
 * Class ConnectionException
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class ConnectionException extends IOException {

    static final long serialVersionUID = -2991328464962422036L;

    // For the time being, this is backwards compatible with 1.3. It could
    // just as easily use the 1.4 constructors for Exception.
    private Throwable m_throwableCause;

    /**
     * Constructor ConnectionException
     *
     * @param msg the message that is the root cause.
     */
    public ConnectionException(String msg) {
        super(msg);
    }

    public ConnectionException(String message, Throwable cause) {
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
