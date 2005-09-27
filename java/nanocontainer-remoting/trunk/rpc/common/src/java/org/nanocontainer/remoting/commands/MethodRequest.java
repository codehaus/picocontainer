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

import org.nanocontainer.remoting.Contextualizable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class MethodRequest
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class MethodRequest extends PublishedNameRequest implements Contextualizable {
    static final long serialVersionUID = -4850912985882914299L;

    private String m_methodSignature;
    private Object[] m_args;
    private Long m_referenceID;
    private Long m_session;

    /**
     * Constructor MethodRequest
     *
     * @param publishedServiceName the published service name
     * @param objectName           the object Name
     * @param methodSignature      the method signature
     * @param args                 an array of args for the method invocation
     * @param referenceID          the reference ID
     * @param session              the session ID
     */
    public MethodRequest(String publishedServiceName, String objectName, String methodSignature, Object[] args, Long referenceID, Long session) {

        super(publishedServiceName, objectName);

        m_methodSignature = methodSignature;
        m_args = args;
        m_referenceID = referenceID;
        m_session = session;
    }

    /**
     * Constructor MethodRequest for Externalization
     */
    public MethodRequest() {
    }

    /**
     * Get method signature in string form.
     *
     * @return the method signature
     */
    public String getMethodSignature() {
        return m_methodSignature;
    }

    /**
     * Get arguments.
     *
     * @return the invocation arguments
     */
    public Object[] getArgs() {
        return m_args;
    }

    /**
     * Get the reference ID.
     *
     * @return the reference ID
     */
    public Long getReferenceID() {
        return m_referenceID;
    }

    /**
     * Gets number that represents type for this class.
     * This is quicker than instanceof for type checking.
     *
     * @return the representative code
     * @see org.nanocontainer.remoting.commands.RequestConstants
     */
    public int getRequestCode() {
        return RequestConstants.METHODREQUEST;
    }

    /**
     * Get the session ID.
     *
     * @return the session ID
     */
    public Long getSession() {
        return m_session;
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

        super.writeExternal(out);
        out.writeObject(m_methodSignature);
        out.writeObject(m_args);
        out.writeObject(m_referenceID);
        out.writeObject(m_session);
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

        super.readExternal(in);

        m_methodSignature = (String) in.readObject();
        m_args = (Object[]) in.readObject();
        m_referenceID = (Long) in.readObject();
        m_session = (Long) in.readObject();
    }
}
