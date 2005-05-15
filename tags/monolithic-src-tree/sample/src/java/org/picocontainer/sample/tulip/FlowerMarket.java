package org.picocontainer.sample.tulip;

/**
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public interface FlowerMarket {
    void sellBid(String flower);

    void buyBid(String flower);
}
