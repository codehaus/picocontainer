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

/**
 * RumtimeException with cascade features.
 * Allows recording of nested exceptions.
 */
public class NanoContainerRemotingRuntimeException extends RuntimeException {
    private final Throwable m_throwable;

    /**
     * Construct a new <code>CascadingRuntimeException</code> instance.
     *
     * @param message   The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public NanoContainerRemotingRuntimeException(final String message, final Throwable throwable) {
        super(message);
        m_throwable = throwable;
    }

    public NanoContainerRemotingRuntimeException(final String message) {
        this(message, null);
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
