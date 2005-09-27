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

public interface ReplyConstants {

    // 'good' replies after 100
    /**
     * A reply of type class (generated proxy)
     */
    int CLASSREPLY = 1;
    /**
     * A reply of type method
     */
    int METHODREPLY = 2;
    /**
     * A reply of type exception
     */
    int EXCEPTIONREPLY = 3;
    /**
     * A reply of type lookup
     */
    int LOOKUPREPLY = 4;
    /**
     * A reply of facade as a result of a method request
     */
    int METHODFACADEREPLY = 5;
    /**
     * An ack on open of connection
     */
    int OPENCONNECTIONREPLY = 6;
    /**
     * An ack on simple ping
     */
    int PINGREPLY = 7;
    /**
     * A list of published services
     */
    int LISTREPLY = 8;
    /**
     * A reply of and array of facades
     */
    int METHODFACADEARRAYREPLY = 9;
    /**
     * A grabage collection reply
     */
    int GCREPLY = 10;

    /**
     * An instruction try again in local modes.  Used instread of an OpenConnectionReply.
     */
    int SAMEVMREPLY = 11;

    /**
     * The list of remote methods within the published Object
     */
    int LISTMETHODSREPLY = 12;

    // 'bad' replies after 100

    /**
     * A some type of problem occured.
     */
    int PROBLEMREPLY = 100;
    /**
     * The service requested was not published
     */
    int NOTPUBLISHEDREPLY = 102;
    /**
     * The request failed
     */
    int REQUESTFAILEDREPLY = 103;
    /**
     * The service is suspended
     */
    int SUSPENDEDREPLY = 104;
    /**
     * The connection has been ended
     */
    int ENDCONNECTIONREPLY = 105;
    /**
     * There is no such reference
     */
    int NOSUCHREFERENCEREPLY = 106;
    /**
     * The proxy class could not be retrieved.
     */
    int CLASSRETRIEVALFAILEDREPLY = 107;

    int CLIENTABEND = 108;

    int INVOCATIONEXCEPTIONREPLY = 109;

    int NOSUCHSESSIONREPLY = 110;
}
