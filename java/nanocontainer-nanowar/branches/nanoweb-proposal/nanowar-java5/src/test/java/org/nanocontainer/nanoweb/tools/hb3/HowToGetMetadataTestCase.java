package org.nanocontainer.nanoweb.tools.hb3;

import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.nanocontainer.nanoweb.tools.hb3.test.util.HB3AbstractTestCase;

public class HowToGetMetadataTestCase extends HB3AbstractTestCase {

    public void testHow() throws Exception {

        PersistentClass info = configuration.getClassMapping("org.nanocontainer.nanoweb.hb3.test.util.Dummy");
        assertNotNull(info);

        Property p = info.getProperty("name");
        assertNotNull(p);

        Column col = getColumn(p);
        assertNotNull(col);

        assertEquals(123, col.getLength());
    }

    private static Column getColumn(Property p) {
        Iterator i = p.getColumnIterator();
        if (i.hasNext()) {
            return (Column) i.next();
        }

        return null;
    }

}
