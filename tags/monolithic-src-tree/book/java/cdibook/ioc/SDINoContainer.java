package cdibook.ioc;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class SDINoContainer {

    public SDINoContainer() {

        PersistenceStore quantumPersistenceStore = null;
        StockQuoteEngine reutersSourcingStockQuoteEngine = null;

// START SNIPPET: block
        SDIComponent sdiComponent = new SDIComponent();
        // cdiComponent is NOT ready to use now.        
        sdiComponent.setPersistenceStore(quantumPersistenceStore);
        sdiComponent.setStockQuoteEngine(reutersSourcingStockQuoteEngine);
        // sdiComponent is ready to use now.
// END SNIPPET: block
    }
}
