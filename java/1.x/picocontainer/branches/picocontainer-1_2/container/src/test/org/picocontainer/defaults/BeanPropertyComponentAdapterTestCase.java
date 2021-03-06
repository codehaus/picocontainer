package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.testmodel.CoupleBean;

/**
 *
 * @author greg
 * @author $Author: $ (last edit)
 * @version $Revision: $ 
 */
public class BeanPropertyComponentAdapterTestCase extends TestCase {
    public void testBeanPropertyComponentAdapterCanUsePropertyEditors() throws ClassNotFoundException {
        Object c = BeanPropertyComponentAdapter.convert(CoupleBean.class.getName(), "a's name:Camilla;b's name:Charles;", this.getClass().getClassLoader());
        assertNotNull(c);
        assertTrue(c instanceof CoupleBean);
        assertEquals("Camilla", ((CoupleBean) c).getPersonA().getName());
        assertEquals("Charles", ((CoupleBean) c).getPersonB().getName());
    }

}
