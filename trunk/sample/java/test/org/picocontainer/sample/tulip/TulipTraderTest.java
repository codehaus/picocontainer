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

    public static interface FlowerPriceListener {
        void flowerPriceChanged(String flower, int price);
    }

    // TODO is this really called "ticker"?
    public static interface FlowerTicker {
        void addFlowerPriceListener(FlowerPriceListener flowerPriceListener);
    }

    public static class FlowerTickerStub implements FlowerTicker {
        public void addFlowerPriceListener(FlowerPriceListener flowerPriceListener) {

        }

        public void changeFlowerPrice(String flower, int value) {
        }
    }

    public static interface FlowerMarket {
        void sellBid(String flower);
    }

    public static class FlowerMarketStub implements FlowerMarket {
        private List currentSellBids = new ArrayList();

        public void sellBid(String flower) {
            currentSellBids.add(flower);
        }

        public List currentSellBids() {
            return currentSellBids;
        }
    }

    public static class TulipTrader implements FlowerPriceListener {
        public void flowerPriceChanged(String flower, int price) {

        }

        public TulipTrader(FlowerTicker ticker,  FlowerMarket market) {
            ticker.addFlowerPriceListener(this);
            market.sellBid("TULIP");
        }
    }

    public void testSellTulipsWhenAboveHundred() {
        FlowerTickerStub ticker = new FlowerTickerStub();
        FlowerMarketStub market = new FlowerMarketStub();
        new TulipTrader(ticker, market);
        ticker.changeFlowerPrice("TULIP", 101);
        assertEquals(Collections.singletonList("TULIP"), market.currentSellBids());
    }
}
