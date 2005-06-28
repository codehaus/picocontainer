package org.nanocontainer.nanoweb.tools.hb3.impl;


import org.nanocontainer.nanoweb.tools.hb3.impl.FieldInfo;
import org.nanocontainer.nanoweb.tools.hb3.test.util.Dummy;
import org.nanocontainer.nanoweb.tools.hb3.test.util.HB3AbstractTestCase;


public class FieldInfoTestCase extends HB3AbstractTestCase {

    public void testFieldInfo() throws Exception {
        FieldInfo info = new FieldInfo(configuration, Dummy.class, null, "name");
        
        assertNotNull(info.persistentClass);
        assertNotNull(info.property);
        assertNotNull(info.column);
        assertEquals(123, info.column.getLength());
    }
    
}
