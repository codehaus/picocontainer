package org.picocontainer.sample.stock;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

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
        void sellBid(String ticker); // TODO: is this really called "bid"?
    }

    public static class StockMarketStub implements StockMarket {
        private List currentSellBids = new ArrayList();

        public void sellBid(String ticker) {
            currentSellBids.add(ticker);
        }

        public List currentSellBids() {
            return currentSellBids;
        }
    }

    public static class StockTrader {
        public StockTrader(StockTicker ticker,  StockMarket market) {
            market.sellBid("MSFT");
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
