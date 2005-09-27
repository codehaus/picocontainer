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
package org.nanocontainer.remoting.server;

import org.nanocontainer.remoting.Authentication;
import org.nanocontainer.remoting.common.AuthenticationException;

/**
 * Interface Authenticator
 *
 * @author Paul Hammant
 * @version * $Revision: 1.2 $
 */
public interface Authenticator {

    /**
     * Check the authority of a particular 'user' to a service
     *
     * @param auth             the authenicatin that needs to be checked.
     * @param publishedService the name of the published service.
     * @throws AuthenticationException if autheication fails.
     */
    void checkAuthority(Authentication auth, String publishedService) throws AuthenticationException;

    /**
     * Get the text to sign for PKI style autheticators
     *
     * @return the signed text.
     */
    String getTextToSign();
}
