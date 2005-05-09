package cdibook.ioc;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
// START SNIPPET: class
public class CDIComponent {

    private final PersistenceStore persistenceStore;
    private final StockQuoteEngine stockQuoteEngine;

    public CDIComponent(PersistenceStore persistenceStore, StockQuoteEngine stockQuoteEngine) {
        this.persistenceStore = persistenceStore;
        this.stockQuoteEngine = stockQuoteEngine;
    }

    // methods that use PersistenceStore or StockQuoteEngine .....
}
// END SNIPPET: class
