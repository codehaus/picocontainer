/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package com.thoughtworks.sise2004;

import junit.framework.TestCase;

public class WalletTestCase extends TestCase {

    public void testNewWalletShouldBeEmpty() {
        Wallet wallet = new Wallet();
        assertEquals(0, wallet.getMoney());
    }

    public void testWalletShouldBeAbleToStoreMoney() {
        Wallet wallet = new Wallet();
        wallet.putMoney(20);
        assertEquals(20, wallet.getMoney());
    }

    public void testWalletsContentShouldBeViewable() {
        Wallet wallet = new Wallet();
        assertEquals(0, wallet.viewMoney());
        wallet.putMoney(20);
        assertEquals(20, wallet.viewMoney());
    }

    public void testWalletShouldContainLessMoneyAfterTakingSomeOut() throws NotEnoughMoneyException {
        Wallet wallet = new Wallet();
        wallet.putMoney(15);
        wallet.getMoney(2);
        assertEquals(13, wallet.viewMoney());
    }

    // Requirements that weren't specified by the user

    public void testWalletShouldBeEmptyAfterGettingMoney() {
        Wallet wallet = new Wallet();
        wallet.putMoney(20);
        wallet.getMoney();

        assertEquals(0, wallet.getMoney());
    }

    public void testWalletShouldNotGiveBackMoreMoneyThanItContains() {
        Wallet wallet = new Wallet();
        wallet.putMoney(18);
        try {
            wallet.getMoney(20);
            fail();
        } catch (NotEnoughMoneyException e) {
        }
    }

    public void testWalletShouldBeAbleToGiveBackAllTheMoney() throws NotEnoughMoneyException {
        Wallet wallet = new Wallet();
        wallet.putMoney(4);
        wallet.getMoney(4);
    }
}