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

import java.util.Date;

/**
 * Class TestInterface3Impl
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version * $Revision: 1.2 $
 */
public class TestInterface3Impl extends TestInterface2Impl implements TestInterface3 {

    private Date mDob;

    /**
     * Constructor TestInterface3Impl
     *
     * @param name
     */
    public TestInterface3Impl(Date dob, String name) {
        super(name);

        mDob = dob;
    }

    /**
     * Method setDOB
     *
     * @param dob
     */
    public void setDOB(Date dob) {

        mDob = dob;

    }

    /**
     * Method getDob
     *
     * @return
     */
    public Date getDOB() {
        return mDob;
    }
}
