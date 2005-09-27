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

import java.io.DataInputStream;
import java.io.IOException;

/**
 * ReqRepBytes
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class ReqRepBytes {
    int m_byteSize;
    byte[] m_bytes;
    Boolean m_isRequest;
    IOException m_ioe;

    public ReqRepBytes(int byteSize, byte[] bytes, Boolean isRequest, IOException ioe) {
        m_byteSize = byteSize;
        m_bytes = bytes;
        m_isRequest = isRequest;
        m_ioe = ioe;
    }

    public boolean ioeDuringReadInt() {
        return (m_ioe != null & m_byteSize == 0);
    }

    public boolean hadIOE() {
        return (m_ioe != null);
    }

    public int getByteSize() {
        return m_byteSize;
    }

    public byte[] getBytes() {
        return m_bytes;
    }

    // request or reply
    public boolean isRequest() {
        return m_isRequest.booleanValue();
    }

    public IOException getIOException() {
        return m_ioe;
    }

    public static ReqRepBytes getRequestReplyBytesFromDataStream(DataInputStream dis) {
        int byteArraySize = 0;
        Boolean isRequest = null;
        byte[] byteArray = null;
        IOException ioe = null;
        try {
            byteArraySize = dis.readInt();
            isRequest = dis.readBoolean() ? Boolean.TRUE : Boolean.FALSE;
            byteArray = new byte[byteArraySize];
            dis.read(byteArray);
        } catch (IOException e) {
            ioe = e;
        }
        return new ReqRepBytes(byteArraySize, byteArray, isRequest, ioe);
    }


}
