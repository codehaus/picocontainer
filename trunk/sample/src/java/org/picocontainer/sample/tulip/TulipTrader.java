package org.picocontainer.sample.tulip;

/**
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class TulipTrader implements FlowerPriceListener {
    private final FlowerMarket market;

    public TulipTrader(FlowerPriceProvider ticker, FlowerMarket market) {
        this.market = market;
        ticker.addFlowerPriceListener(this);
    }

    public void flowerPriceChanged(String flower, int price) {
        System.out.println(flower + " price " + price);
        if (!flower.equals("Tulip")) {
            return;
        }

        if (price > 100) {
            market.sellBid(flower);
        }
        if (price < 70) {
            market.buyBid(flower);
        }
    }
}
