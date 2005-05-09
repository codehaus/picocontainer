package cdibook.ioc;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
// START SNIPPET: class
public class SDIComponent {

    private PersistenceStore persistenceStore;
    private StockQuoteEngine stockQuoteEngine;

    public void setPersistenceStore(PersistenceStore persistenceStore) {
        this.persistenceStore = persistenceStore;
    }

    public void setStockQuoteEngine(StockQuoteEngine stockQuoteEngine) {
        this.stockQuoteEngine = stockQuoteEngine;
    }

    // methods that use PersistenceStore or StockQuoteEngine .....
}
// END SNIPPET: class
