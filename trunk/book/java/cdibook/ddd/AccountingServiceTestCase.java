package cdibook.ddd;

import junit.framework.TestCase;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author Paul Hammant & Kraig Parkinson
 * @version $Revision$
 */
public class AccountingServiceTestCase extends TestCase {

    public void testShouldCreateAccount() {

// START SNIPPET: one
        AccountingService as = new DefaultAccountingService(new AccountFactory());

        Account newAcc = as.createAccount();

// END SNIPPET: one

        assertNotNull(newAcc);
    }

    public void testShouldCreateAccountPicoified() {

// START SNIPPET: two
        DefaultAccountingService das = new DefaultAccountingService(new AccountFactory());

        DefaultPicoContainer dpc = new DefaultPicoContainer();
        dpc.registerComponentImplementation(AccountFactory.class);
        dpc.registerComponentImplementation(AccountingService.class, DefaultAccountingService.class);

        AccountingService as = (AccountingService) dpc.getComponentInstance(AccountingService.class);

// END SNIPPET: two
        assertNotNull(as);

        Account newAcc = as.createAccount();
        assertNotNull(newAcc);

    }

}
