package cdibook.ioc;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class CDINoContainer {

    public CDINoContainer() {

        PersistenceStore quantumPersistenceStore = null;
        StockQuoteEngine reutersSourcingStockQuoteEngine = null;

// START SNIPPET: block
        CDIComponent cdiComponent = new CDIComponent(quantumPersistenceStore,
                reutersSourcingStockQuoteEngine);
        // cdiComponent is ready to use now.
// END SNIPPET: block
    }
}
