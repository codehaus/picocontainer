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

import java.util.HashMap;

/**
 * @author Paul Hammant and Rune Johanessen (pairing for part)
 * @version $Revision: 1.2 $
 */

public class AccountManagerImpl implements AccountManager {

    private HashMap m_accounts = new HashMap();
    private ServerSideClientContextFactory m_clientContextFactory;

    public AccountManagerImpl(ServerSideClientContextFactory clientContextFactory, Account one, Account two) {
        m_clientContextFactory = clientContextFactory;
        m_accounts.put(one.getSymbolicKey(), one);
        m_accounts.put(two.getSymbolicKey(), two);
    }


    public void transferAmount(String acct1, String acct2, int amt) throws TransferBarfed {

        Account from = (Account) m_accounts.get(acct1);
        Account to = (Account) m_accounts.get(acct2);

        ClientContext cc = m_clientContextFactory.get();

        try {
            from.debit(amt);
            to.credit(amt);
        } catch (DebitBarfed debitBarfed) {
            throw new TransferBarfed();
        } catch (CreditBarfed creditBarfed) {
            throw new TransferBarfed();
        } finally {
            // ?
        }

    }
}
