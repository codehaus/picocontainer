package picocontainer;

import junit.framework.TestCase;
import picocontainer.testmodel.FlintstonesImpl;
import picocontainer.testmodel.FredImpl;
import picocontainer.testmodel.WilmaImpl;

public class NoPicoTestCase extends TestCase {

    /**
     * A demonstration of using components WITHOUT Pico (or Nano)
     * This was one of the design goals.
     *
     * This is manual lacing of components.
     *
     */
    public void testWilmaWithoutPicoTestCase() {

        WilmaImpl wilma = new WilmaImpl();
        FredImpl fred = new FredImpl(wilma);

        assertTrue("Wilma should have had her hello method called",
                wilma.helloCalled());
    }


}
