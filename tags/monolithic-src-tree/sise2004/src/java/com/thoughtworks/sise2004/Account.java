/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package com.thoughtworks.sise2004;

public class Account {
    private long money;
    private final EmailSender emailSender;

    public Account() {
        this(EmailSender.NULL_SENDER);
    }

    public Account(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public long getMoney() {
        long result = money;
        money = 0;
        return result;
    }

    public void putMoney(int someMoney) {
        money = someMoney;
    }

    public long viewMoney() {
        return money;
    }

    public long getMoney(long amount) {
        money -= amount;
        if (money < 0) {
            emailSender.sendMail("erik@thoughtworks.com", "Your account is overdrawn.");
        }
        return amount;
    }
}