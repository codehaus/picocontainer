package org.picocontainer.sample.tulip;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class TulipTraderTest extends TestCase {

    public static interface TickerListener {
        void flowerPriceChanged(String flower, int price);
    }

    // TODO is this really called "ticker"?
    public static interface FlowerTicker {
        void addTickerListener(TickerListener tickerListener);
    }

    public static class FlowerTickerStub implements FlowerTicker {
        public void addTickerListener(TickerListener tickerListener) {

        }

        // TODO is this really called "ticker"?
        public void changeFlowerPrice(String ticker, int value) {
        }
    }

    public static interface FlowerMarket {
        void sellBid(String ticker); // TODO: is this really called "bid"?
    }

    public static class FlowerMarketStub implements FlowerMarket {
        private List currentSellBids = new ArrayList();

        public void sellBid(String ticker) {
            currentSellBids.add(ticker);
        }

        public List currentSellBids() {
            return currentSellBids;
        }
    }

    public static class TulipTrader implements TickerListener {
        public void flowerPriceChanged(String flower, int price) {

        }

        public TulipTrader(FlowerTicker ticker,  FlowerMarket market) {
            ticker.addTickerListener(this);
            market.sellBid("TULIP");
        }
    }

    public void testSellMicrosoftWhenAboveHundred() {
        FlowerTickerStub ticker = new FlowerTickerStub();
        FlowerMarketStub market = new FlowerMarketStub();
        new TulipTrader(ticker, market);
        ticker.changeFlowerPrice("TULIP", 101);
        assertEquals(Collections.singletonList("TULIP"), market.currentSellBids());
    }
}
