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
 * Class GroupedMethodRequest
 *
 * @author Paul Hammant
 * @version $Revision: 1.2 $
 */
public class GroupedMethodRequest extends Request {

    static final long serialVersionUID = 2433454402872395509L;

    private String m_methodSignature;
    private Object[] m_args;

    public GroupedMethodRequest() {
    }

    public GroupedMethodRequest(String methodSignature, Object[] args) {
        m_methodSignature = methodSignature;
        m_args = args;
    }

    public String getMethodSignature() {
        return m_methodSignature;
    }

    public Object[] getArgs() {
        return m_args;
    }

    public int getRequestCode() {
        return RequestConstants.METHODASYNCREQUEST;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(m_methodSignature);
        out.writeObject(m_args);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        m_methodSignature = (String) in.readObject();
        m_args = (Object[]) in.readObject();
    }

}
