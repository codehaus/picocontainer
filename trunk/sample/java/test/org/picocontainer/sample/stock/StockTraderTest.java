package org.picocontainer.sample.stock;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class StockTraderTest extends TestCase {

    public static interface StockTicker {
    }

    public static class StockTickerStub implements StockTicker {
        public void changeTickerValue(String ticker, int value) {
        }
    }

    public static interface StockMarket {
    }

    public static class StockMarketStub implements StockMarket {
        public List currentSellBids() {
            return Collections.singletonList("MSFT");
        }
    }

    public static class StockTrader {
        public StockTrader(StockTicker ticker,  StockMarket market) {
        }
    }

    public void testSellMicrosoftWhenAboveHundred() {
        StockTickerStub ticker = new StockTickerStub();
        StockMarketStub market = new StockMarketStub();
        new StockTrader(ticker, market);
        ticker.changeTickerValue("MSFT", 101);
        assertEquals(Collections.singletonList("MSFT"), market.currentSellBids());
    }
}
