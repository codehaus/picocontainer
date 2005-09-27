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

import org.nanocontainer.remoting.Authentication;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class UserPasswordAuthentication
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public final class UserPasswordAuthentication extends Authentication {
    static final long serialVersionUID = -1387784311223571160L;

    private String m_userID;
    private String m_password;

    /**
     * Constructor UserPasswordAuthentication
     *
     * @param userID   the user ID
     * @param password the password
     */
    public UserPasswordAuthentication(String userID, String password) {
        m_userID = userID;
        m_password = password;
    }

    /**
     * Constructor UserPasswordAuthentication for Externalization
     */
    public UserPasswordAuthentication() {
    }

    /**
     * Get user ID.
     *
     * @return the user ID
     */
    public String getUserID() {
        return m_userID;
    }

    /**
     * Get password.
     *
     * @return the password
     */
    public String getPassword() {
        return m_password;
    }

    /**
     * The object implements the writeExternal method to save its contents
     * by calling the methods of DataOutput for its primitive values or
     * calling the writeObject method of ObjectOutput for objects, strings,
     * and arrays.
     *
     * @param out the stream to write the object to
     * @throws IOException Includes any I/O exceptions that may occur
     * @serialData Overriding methods should use this tag to describe
     * the data layout of this Externalizable object.
     * List the sequence of element types and, if possible,
     * relate the element to a public/protected field and/or
     * method of this Externalizable class.
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(m_userID);
        out.writeObject(m_password);
    }

    /**
     * The object implements the readExternal method to restore its
     * contents by calling the methods of DataInput for primitive
     * types and readObject for objects, strings and arrays.  The
     * readExternal method must read the values in the same sequence
     * and with the same types as were written by writeExternal.
     *
     * @param in the stream to read data from in order to restore the object
     * @throws IOException            if I/O errors occur
     * @throws ClassNotFoundException If the class for an object being
     *                                restored cannot be found.
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        m_userID = (String) in.readObject();
        m_password = (String) in.readObject();
    }
}
