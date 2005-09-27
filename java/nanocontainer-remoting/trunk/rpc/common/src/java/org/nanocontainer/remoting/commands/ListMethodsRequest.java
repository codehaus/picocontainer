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
 * Request the list of remote methods within the published Object.
 *
 * @author <a href="mailto:vinayc@apache.org">Vinay Chandrasekharan</a>
 */
public final class ListMethodsRequest extends Request {

    static final long serialVersionUID = 466389099971742704L;
    /**
     * Name of the published object whose remote methods is sought
     */
    private String m_publishedName;


    /**
     * Constructor .
     *
     * @param publishedName
     */
    public ListMethodsRequest(String publishedName) {
        m_publishedName = publishedName;
    }

    /**
     * default constructor needed for externalization
     */
    public ListMethodsRequest() {
    }

    //--------Methods---------------//
    /**
     * Get the published objects name
     */
    public String getPublishedName() {
        return m_publishedName;
    }

    //----Request Override--//
    /**
     * Gets number that represents type for this class.
     * This is quicker than instanceof for type checking.
     *
     * @return the representative code
     * @see org.nanocontainer.remoting.commands.RequestConstants
     */
    public int getRequestCode() {
        return RequestConstants.LISTMETHODSREQUEST;
    }

    //----Externalizable Overrides---//
    /**
     * @see java.io.Externalizable#writeExternal(ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(m_publishedName);
    }

    /**
     * @see java.io.Externalizable#readExternal(ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        m_publishedName = (String) in.readObject();
    }
}
