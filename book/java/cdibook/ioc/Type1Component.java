package cdibook.ioc;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
// START SNIPPET: class
public class Type1Component implements RequiresDependencies {

    private PersistenceStore persistenceStore;
    private StockQuoteEngine stockQuoteEngine;


    public void takeDependencies(DependencyManager dm) {
        this.persistenceStore = (PersistenceStore) dm.lookup("PersistenceStore");
        this.stockQuoteEngine = (StockQuoteEngine) dm.lookup("StockQuoteEngine");
    }

    // methods that use PersistenceStore or StockQuoteEngine .....
}
// END SNIPPET: class
