package org.picocontainer.sample.tulip;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class FlowerTickerStub implements FlowerPriceProvider {
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
