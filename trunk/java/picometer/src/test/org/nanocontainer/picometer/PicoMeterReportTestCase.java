package org.nanocontainer.picometer;

import java.io.IOException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoMeterReportTestCase extends AbstractPicoMeterTestCase {
    public void testSourceIsHighlighted() throws IOException {
        PicoMeterClass instantiatesThree = new PicoMeterClass(PicoMeterClassTestCase.InstantiatesThree.class, source);
        PicoMeterReport picoMeterReport = new PicoMeterReport();
//        picoMeterReport.writeReport(instantiatesThree, new PrintWriter(System.out));
    }

}
