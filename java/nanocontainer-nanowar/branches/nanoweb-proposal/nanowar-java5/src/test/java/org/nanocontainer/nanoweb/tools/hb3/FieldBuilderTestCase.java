package org.nanocontainer.nanoweb.tools.hb3;

import org.nanocontainer.nanoweb.tools.hb3.FieldBuilder;
import org.nanocontainer.nanoweb.tools.hb3.test.util.Dummy;
import org.nanocontainer.nanoweb.tools.hb3.test.util.HB3AbstractTestCase;

public class FieldBuilderTestCase extends HB3AbstractTestCase {

    public void testInput() throws Exception {
        Dummy dummy = new Dummy();
        FieldBuilder fb = new FieldBuilder(configuration);

        String ret = fb.field(Dummy.class, dummy, "name", "xyz.name");
        assertEquals("<input type=\"text\" name=\"xyz.name\" class=\"__gen\" maxlength=\"123\"></input>", ret);

        ret = fb.field(Dummy.class, dummy, "bigString", "xyz.name");
        assertEquals("<textarea name=\"xyz.name\" class=\"__gen\" maxlength=\"2000\"></textarea>", ret);

        ret = fb.field(Dummy.class, dummy, "blobString", "xyz.name");
        assertEquals("<textarea name=\"xyz.name\" class=\"__gen\"></textarea>", ret);
    }

}
