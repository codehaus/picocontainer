package cdibook.ddd;

/**
 * @author Paul Hammant & Kraig Parkinson
 * @version $Revision$
 */
public class AccountFactory {
    public Account createAccount() {
        return new DefaultAccount();
    }
}
