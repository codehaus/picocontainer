package org.picoextras.picometer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoMeterClassTestCase extends AbstractPicoMeterTestCase {

    public void testSourceLocation() throws IOException {
        PicoMeterClass instantiatesThree = new PicoMeterClass(InstantiatesThree.class, source);
        Instantiation i0 = (Instantiation) instantiatesThree.getInstantiations().get(0);
        assertEquals(30, i0.getStartLine());
        assertEquals(16, i0.getStartColumn());
        assertEquals(27, i0.getEndColumn());

        Instantiation i1 = (Instantiation) instantiatesThree.getInstantiations().get(1);
        assertEquals(33, i1.getStartLine());
        assertEquals(27, i1.getStartColumn());

        Instantiation i2 = (Instantiation) instantiatesThree.getInstantiations().get(2);
        assertEquals(37, i2.getStartLine());
        assertEquals(26, i2.getStartColumn());
    }

    public void testObjectInstantiationsAreCountedInClassHeaderAndConstructorAndMethods() throws IOException {
        PicoMeterClass instantiatesThree = new PicoMeterClass(InstantiatesThree.class, source);
        assertEquals(3, instantiatesThree.getInstantiations().size());

        Instantiation i0 = (Instantiation) instantiatesThree.getInstantiations().get(0);
        assertEquals(Dummy.class.getName(), i0.getClassName());

        Instantiation i1 = (Instantiation) instantiatesThree.getInstantiations().get(1);
        assertEquals(Dummy.class.getName(), i1.getClassName());

        Instantiation i2 = (Instantiation) instantiatesThree.getInstantiations().get(2);
        assertEquals(Dummy.class.getName(), i2.getClassName());
    }

    public void testNoInstantiations() throws IOException {
        PicoMeterClass oneInjection = new PicoMeterClass(OneInjection.class, source);
        assertEquals(0, oneInjection.getInstantiations().size());
    }

    public void testSorting() throws IOException {
        PicoMeterClass oneInjection = new PicoMeterClass(OneInjection.class, source);
        PicoMeterClass instantiatesThree = new PicoMeterClass(InstantiatesThree.class, source);
        PicoMeterClass instantiatesOne = new PicoMeterClass(InstantiatesOne.class, source);

        List l = new ArrayList();
        l.add(oneInjection);
        l.add(instantiatesThree);
        l.add(instantiatesOne);
        Collections.sort(l, new InstantiationCountSorter());

        assertSame(oneInjection, l.get(0));
        assertSame(instantiatesOne, l.get(1));
        assertSame(instantiatesThree, l.get(2));
    }

}
