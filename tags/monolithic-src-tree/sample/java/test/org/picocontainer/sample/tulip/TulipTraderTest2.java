package org.picocontainer.sample.tulip;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 *
 * Is this a dupe of TulipTraderTest that was in sample/java/test/ ???
 *
 *
 */
public class TulipTraderTest2 extends TestCase {
    private FlowerTickerStub ticker;
    private FlowerMarketStub market;

    public static interface FlowerPriceListener {
        void flowerPriceChanged(String flower, int price);
    }

    // TODO is this really called "ticker"?
    public static interface FlowerTicker {
        void addFlowerPriceListener(FlowerPriceListener flowerPriceListener);
    }

    public static class FlowerTickerStub implements FlowerTicker {
        private List listeners = new ArrayList();

        public void addFlowerPriceListener(FlowerPriceListener flowerPriceListener) {
            listeners.add(flowerPriceListener);
        }

        public void changeFlowerPrice(String flower, int value) {
            for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
                FlowerPriceListener flowerPriceListener = (FlowerPriceListener) iterator.next();
                flowerPriceListener.flowerPriceChanged(flower, value);
            }
        }
    }

    public static interface FlowerMarket {
        void sellBid(String flower);

        void buyBid(String flower);
    }

    public static class FlowerMarketStub implements FlowerMarket {
        private List currentSellBids = new ArrayList();
        private List currentBuyBids = new ArrayList();

        public void sellBid(String flower) {
            currentSellBids.add(flower);
        }

        public void buyBid(String flower) {
            currentBuyBids.add(flower);
        }

        public List currentSellBids() {
            return currentSellBids;
        }

        public List currentBuyBids() {
            return currentBuyBids;
        }
    }

    public static class TulipTrader implements FlowerPriceListener {
        private final FlowerMarket market;

        public void flowerPriceChanged(String flower, int price) {
            if (price > 100) {
                market.sellBid(flower);
            }
            if (price < 70) {
                market.buyBid(flower);
            }
        }

        public TulipTrader(FlowerTicker ticker, FlowerMarket market) {
            this.market = market;
            ticker.addFlowerPriceListener(this);
        }
    }

    protected void setUp() throws Exception {
        ticker = new FlowerTickerStub();
        market = new FlowerMarketStub();
        new TulipTrader(ticker, market);
    }

    public void testSellTulipsWhenAboveHundred() {
        ticker.changeFlowerPrice("TULIP", 101);
        assertEquals(Collections.singletonList("TULIP"), market.currentSellBids());
    }

    public void testDontSellTulipsWhenBelowHundred() {
        ticker.changeFlowerPrice("TULIP", 99);
        assertEquals(Collections.EMPTY_LIST, market.currentSellBids());
    }

    public void testBuyTulipsWhenBelowSeventy() {
        ticker.changeFlowerPrice("TULIP", 69);
        assertEquals(Collections.singletonList("TULIP"), market.currentBuyBids());
    }
}
