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
package org.nanocontainer.remoting.test;

/**
 * Class MemoryLeakImpl
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.2 $
 */
public class MemoryLeakImpl implements MemoryLeak {

    /**
     * Constructor MemoryLeakImpl
     */
    MemoryLeakImpl() {
    }

    /**
     * Method getNewMemoryLeak
     *
     * @return
     */
    public MemoryLeak getNewMemoryLeak() {

        System.gc();    // pointless ?

        return new MemoryLeakImpl();
    }

    /**
     * Method getHugeString
     *
     * @return
     */
    public String getHugeString() {

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < 1000; i++) {
            sb.append("" + i);
        }

        return sb.toString();
    }

    protected void finalize() throws Throwable {

        super.finalize();

        //System.out.println("impl finalized");
    }
}
