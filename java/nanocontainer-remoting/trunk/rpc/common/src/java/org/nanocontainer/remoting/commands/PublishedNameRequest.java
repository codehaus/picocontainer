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

import org.nanocontainer.remoting.ClientContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class PublishedNameRequest
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public abstract class PublishedNameRequest extends Request {
    static final long serialVersionUID = 5995735372269955205L;

    private String m_publishedServiceName;
    private String m_objectName;
    private ClientContext m_context;

    /**
     * Constructor PublishedNameRequest
     *
     * @param publishedServiceName the published service name
     * @param objectName           the object name within that
     */
    public PublishedNameRequest(String publishedServiceName, String objectName) {
        m_publishedServiceName = publishedServiceName;
        m_objectName = objectName;
    }

    public void setContext(ClientContext context) {
        m_context = context;
    }

    public ClientContext getContext() {
        return m_context;
    }

    /**
     * Constructor PublishedNameRequest for Externalization
     */
    public PublishedNameRequest() {
    }

    /**
     * Get published service name
     *
     * @return the published service name
     */
    public String getPublishedServiceName() {
        return m_publishedServiceName;
    }

    /**
     * Get object name
     *
     * @return the object name
     */
    public String getObjectName() {
        return m_objectName;
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
        out.writeObject(m_publishedServiceName);
        out.writeObject(m_objectName);
        out.writeObject(m_context);
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
        m_publishedServiceName = (String) in.readObject();
        m_objectName = (String) in.readObject();
        m_context = (ClientContext) in.readObject();
    }
}
