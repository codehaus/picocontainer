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

import java.io.Serializable;

/**
 * Class FacadeRefHolder
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public final class FacadeRefHolder implements Serializable {
    static final long serialVersionUID = 4517499953558154280L;

    private Long m_referenceID;
    private String m_objectName;

    /**
     * Constructor FacadeRefHolder
     *
     * @param referenceID the reference ID
     * @param objectName  the object name
     */
    public FacadeRefHolder(Long referenceID, String objectName) {
        m_referenceID = referenceID;
        m_objectName = objectName;
    }

    /**
     * Get the reference ID
     *
     * @return the reference ID
     */
    public Long getReferenceID() {
        return m_referenceID;
    }

    /**
     * Get the object name.
     *
     * @return the object name
     */
    public String getObjectName() {
        return m_objectName;
    }
}
