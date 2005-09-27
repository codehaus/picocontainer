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
package org.nanocontainer.remoting.commands;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class InvocationExceptionReply
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class InvocationExceptionReply extends Reply {

//    static final long serialVersionUID = TODO;

    private String m_message;

    public InvocationExceptionReply(String message) {
        m_message = message;
    }

    public String getMessage() {
        return m_message;
    }

    public int getReplyCode() {
        return ReplyConstants.INVOCATIONEXCEPTIONREPLY;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(m_message);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        m_message = (String) in.readObject();
    }

}
