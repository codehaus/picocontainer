package org.picocontainer.sample.tulip;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class FlowerMarketStub implements FlowerMarket {
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
