package org.picocontainer.sample.tulip;

import junit.framework.TestCase;

import java.util.Collections;

/**
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class TulipTraderTest extends TestCase {
    private FlowerTickerStub ticker;
    private FlowerMarketStub market;

    public static class TulipTrader implements FlowerPriceListener {
        private final FlowerMarket market;

        public TulipTrader(FlowerPriceProvider ticker, FlowerMarket market) {
            this.market = market;
            ticker.addFlowerPriceListener(this);
        }

        public void flowerPriceChanged(String flower, int price) {
            if (price > 100) {
                market.sellBid(flower);
            }
            if (price < 70) {
                market.buyBid(flower);
            }
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
