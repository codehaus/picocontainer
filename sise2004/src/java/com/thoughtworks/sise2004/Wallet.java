/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package com.thoughtworks.sise2004;

public class Wallet {
    private long money;

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

    public long getMoney(long amount) throws NotEnoughMoneyException {
        if(amount > money) {
            throw new NotEnoughMoneyException();
        }
        money -= amount;
        return amount;
    }
}