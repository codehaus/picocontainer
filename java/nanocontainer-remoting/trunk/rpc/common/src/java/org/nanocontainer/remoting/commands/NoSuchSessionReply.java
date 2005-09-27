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
 * Class NoSuchSessionReply
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public final class NoSuchSessionReply extends NotPublishedReply {
//    static final long serialVersionUID = TODO;

    private Long m_sessionID;

    /**
     * Constructor NoSuchSessionReply
     */
    public NoSuchSessionReply() {
    }

    /**
     * Constructor NoSuchSessionReply
     *
     * @param sessionID the reference identifier
     */
    public NoSuchSessionReply(Long sessionID) {
        m_sessionID = sessionID;
    }

    /**
     * Gets number that represents type for this class.
     * This is quicker than instanceof for type checking.
     *
     * @return the representative code
     * @see org.nanocontainer.remoting.commands.ReplyConstants
     */
    public int getReplyCode() {
        return ReplyConstants.NOSUCHSESSIONREPLY;
    }

    /**
     * Get the session ID.
     *
     * @return the session id
     */
    public Long getSessionID() {
        return m_sessionID;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(m_sessionID);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        super.readExternal(in);

        m_sessionID = (Long) in.readObject();
    }
}
