package cdibook.ddd;

/**
 * @author Paul Hammant & Kraig Parkinson
 * @version $Revision$
 */
public class DefaultAccountingService implements AccountingService {

    private final AccountFactory accountFactory;

    public DefaultAccountingService(AccountFactory accountFactory) {
        this.accountFactory = accountFactory;
    }

    public Account createAccount() {
        return accountFactory.createAccount(); 
    }
}
