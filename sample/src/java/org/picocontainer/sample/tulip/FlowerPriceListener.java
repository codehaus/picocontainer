package org.picocontainer.sample.tulip;

/**
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public interface FlowerPriceListener {
    void flowerPriceChanged(String flower, int price);
}
