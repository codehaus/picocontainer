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
package org.nanocontainer.remoting.test.clientcontext;

import org.nanocontainer.remoting.ClientContext;
import org.nanocontainer.remoting.server.ServerSideClientContextFactory;

/**
 * @author Paul Hammant and Rune Johanessen (pairing for part)
 * @version $Revision: 1.2 $
 */

public class AccountImpl implements Account {

    private ServerSideClientContextFactory m_clientContextFactory;
    String m_symbolicKey;
    private int m_balance = 123;
    private AccountListener m_accountListener;

    public AccountImpl(ServerSideClientContextFactory clientContextFactory, String symbolicKey, AccountListener accountListener) {
        m_clientContextFactory = clientContextFactory;
        m_symbolicKey = symbolicKey;
        m_accountListener = accountListener;
    }

    public String getSymbolicKey() {
        return m_symbolicKey;
    }

    public int getBalance() {
        return m_balance;
    }

    public void debit(int amt) throws DebitBarfed {
        ClientContext cc = m_clientContextFactory.get();
        m_balance = m_balance - amt;
        m_accountListener.record(getSymbolicKey() + ":debit:" + amt, cc);
    }

    public void credit(int amt) throws CreditBarfed {
        ClientContext cc = m_clientContextFactory.get();
        m_balance = m_balance + amt;
        m_accountListener.record(getSymbolicKey() + ":credit:" + amt, cc);
    }
}
