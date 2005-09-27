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
package org.nanocontainer.remoting.test.async;

/**
 * @nanocontainer-remoting:facade async-mode1
 */
public interface AsyncTest {

    /**
     * @nanocontainer-remoting:method:async
     */
    void setOne(String one);

    /**
     * @nanocontainer-remoting:method:async
     */
    void setTwo(String two);

    /**
     * @nanocontainer-remoting:method:async
     */
    void setThree(String three);

    /**
     * @nanocontainer-remoting:method:commit
     */
    void fire();

    /**
     * @nanocontainer-remoting:method:rollback
     */
    void whoa();

}
