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
 * CallbackException occurs on failure to expose
 * the object for calbacks
 *
 * @author <a href="mailto:vinayc77@yahoo.com">Vinay Chandran</a>
 * @version $Revision: 1.2 $
 */
public class CallbackException extends Exception {

    static final long serialVersionUID = -6179989997797491904L;

    /**
     * Constructor CallbackException
     *
     * @param msg the message that is the root cause of the exception.
     */
    public CallbackException(String msg) {
        super(msg);
    }
}
