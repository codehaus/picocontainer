/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package com.thoughtworks.sise2004;

import junit.framework.TestCase;
import org.jmock.Mock;
import org.jmock.C;

public class AccountTestCase extends TestCase {

    //////// ITERATION I ////////
    public void testNewAccountShouldBeEmpty() {
        Account account = new Account();
        assertEquals(0, account.getMoney());
    }

    public void testAccountShouldBeAbleToStoreMoney() {
        Account account = new Account();
        account.putMoney(20);
        assertEquals(20, account.getMoney());
    }

    public void testAccountsContentShouldBeViewable() {
        Account account = new Account();
        assertEquals(0, account.viewMoney());
        account.putMoney(20);
        assertEquals(20, account.viewMoney());
    }

    public void testAccountShouldContainLessMoneyAfterTakingSomeOut() throws NotEnoughMoneyException {
        Account account = new Account();
        account.putMoney(15);
        account.getMoney(2);
        assertEquals(13, account.viewMoney());
    }

    // Requirements that weren't specified by the user

    public void testAccountShouldBeEmptyAfterGettingMoney() {
        Account account = new Account();
        account.putMoney(20);
        account.getMoney();

        assertEquals(0, account.getMoney());
    }

    public void testAccountShouldBeAbleToGiveBackAllTheMoney() throws NotEnoughMoneyException {
        Account account = new Account();
        account.putMoney(4);
        account.getMoney(4);
    }

    //////// ITERATION II ////////

    public void testAccountShouldAllowToBeOverdrawn() {
        Account account = new Account();
        account.putMoney(50);
        account.getMoney(70);

        assertEquals(-20, account.viewMoney());
    }

    public void testAccountShouldSendMailWhenGoingIntoOverdraft() {
        Mock emailSenderMock = new Mock(EmailSender.class);

        // emailSender.sendMail("erik@thoughtworks.com", "Your account is overdrawn.")
        emailSenderMock.expect("sendMail",
                C.args(C.eq("erik@thoughtworks.com"),
                        C.eq("Your account is overdrawn.")));

        EmailSender emailSender = (EmailSender) emailSenderMock.proxy();
        Account account = new Account(emailSender);
        account.putMoney(50);
        account.getMoney(70);

        emailSenderMock.verify();
    }

    public void testAccountsShouldContainEmailAddress() {
        Account aslak = new Account();
        Account erik = new Account();

        aslak.setEmail("aslak@thoughtworks.com");
        aslak.setEmail("aslak@thoughtworks.com");
    }

}