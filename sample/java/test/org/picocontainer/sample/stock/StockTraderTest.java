package org.picocontainer.sample.stock;

import junit.framework.TestCase;

/**
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class StockTraderTest extends TestCase {

    public static interface StockTicker {
    }

    public static class MockStockTicker implements StockTicker {
    }

    public static interface StockMarket {
    }

    public static class MockStockMarket implements StockMarket {
    }

    public static class StockTrader {
        public StockTrader(StockTicker ticker,  StockMarket market) {
        }
    }

    public void testSellMicrosoftWhenAboveHundred() {
        StockTicker ticker = new MockStockTicker();
        StockMarket market = new MockStockMarket();
        StockTrader trader = new StockTrader(ticker, market);
    }
}
