package org.picoextras.picometer;

import java.util.Comparator;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class InstantiationCountSorter implements Comparator {
    public int compare(Object o1, Object o2) {
        PicoMeterClass c1 = (PicoMeterClass) o1;
        PicoMeterClass c2 = (PicoMeterClass) o2;
        return c1.getInstantiations().size() - c2.getInstantiations().size();
    }
}
