package org.picocontainer.sample.tulip;

import junit.framework.TestCase;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.Collections;

/**
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class TulipTraderTest extends TestCase {
    private FlowerPriceProviderStub ticker;
    private FlowerMarketStub market;

    protected void setUp() throws Exception {
        DefaultPicoContainer container = new DefaultPicoContainer();
        container.registerComponentImplementation(FlowerPriceProviderStub.class);
        container.registerComponentImplementation(FlowerMarketStub.class);
        container.registerComponentImplementation(TulipTrader.class);
        container.start();

        ticker = (FlowerPriceProviderStub) container.getComponentInstance(FlowerPriceProvider.class);
        market = (FlowerMarketStub) container.getComponentInstance(FlowerMarketStub.class);
    }

    public void testSellTulipsWhenAboveHundred() {
        ticker.changeFlowerPrice("Tulip", 101);
        assertEquals(Collections.singletonList("Tulip"), market.currentSellBids());
    }

    public void testDontSellTulipsWhenBelowHundred() {
        ticker.changeFlowerPrice("Tulip", 99);
        assertEquals(Collections.EMPTY_LIST, market.currentSellBids());
    }

    public void testBuyTulipsWhenBelowSeventy() {
        ticker.changeFlowerPrice("Tulip", 69);
        assertEquals(Collections.singletonList("Tulip"), market.currentBuyBids());
    }

    public void testDontBotherAboutOtherFlowers() {
        ticker.changeFlowerPrice("Forgetmenot", 150);
        ticker.changeFlowerPrice("Rose", 10);
        assertEquals(Collections.EMPTY_LIST, market.currentSellBids());
        assertEquals(Collections.EMPTY_LIST, market.currentBuyBids());
    }
}
