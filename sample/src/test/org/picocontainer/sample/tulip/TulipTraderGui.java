package org.picocontainer.sample.tulip;

import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class TulipTraderGui {
    public static void main(String[] args) {
        DefaultPicoContainer container = new DefaultPicoContainer();
        container.registerComponentImplementation(FlowerPriceProviderGui.class);
        container.registerComponentImplementation(FlowerMarketGui.class);
        container.registerComponentImplementation(TulipTrader.class);
        container.start();
    }
}
