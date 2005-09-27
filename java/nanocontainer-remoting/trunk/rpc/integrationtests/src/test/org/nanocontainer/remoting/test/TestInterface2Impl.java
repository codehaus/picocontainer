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
 * Class TestInterface2Impl
 *
 * @author Paul Hammant
 * @version * $Revision: 1.2 $
 */
public class TestInterface2Impl implements TestInterface2 {

    private String mName;

    /**
     * Constructor TestInterface2Impl
     *
     * @param name
     */
    public TestInterface2Impl(String name) {
        mName = name;
    }

    /**
     * Method setName
     *
     * @param newThingName
     */
    public void setName(String newThingName) {
        mName = newThingName;
    }

    /**
     * Method getName
     *
     * @return
     */
    public String getName() {
        return mName;
    }

    public boolean equals(Object obj) {

        // This is a bit unusual, but it is for the AbstractHelloTestCase testEquals method

        TestInterface2 other = (TestInterface2) obj;

        if (mName.equals("equals-test-one") && other.getName().equals("equals-test-two")) {
            return true;
        }

        return false;

    }

}
