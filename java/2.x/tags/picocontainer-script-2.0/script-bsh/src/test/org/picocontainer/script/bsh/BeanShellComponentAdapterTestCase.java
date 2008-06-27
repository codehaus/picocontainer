/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.picocontainer.script.bsh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;

/**
 * @author <a href="mail at leosimons dot com">Leo Simons</a>
 * @author Nick Sieger
 */
public class BeanShellComponentAdapterTestCase {

    private MutablePicoContainer pico;

    ComponentAdapter<?> setupComponentAdapter(Class<?> implementation) {
        pico = new DefaultPicoContainer();
        pico.addComponent("whatever", ArrayList.class);

        ComponentAdapter<?> adapter = new BeanShellAdapter("thekey", implementation, (Parameter[])null);
        pico.addAdapter(adapter);
        return adapter;
    }

    @Test public void testGetComponentInstance() {
        ComponentAdapter<?> adapter = setupComponentAdapter(ScriptableDemoBean.class);

        ScriptableDemoBean bean = (ScriptableDemoBean) adapter.getComponentInstance(pico, null);

        assertEquals("Bsh demo script should have set the key", "thekey", bean.key);

        assertTrue(bean.whatever instanceof ArrayList);
    }

    @Test public void testGetComponentInstanceBadScript() {
        ComponentAdapter<?> adapter = setupComponentAdapter(BadScriptableDemoBean.class);

        try {
            adapter.getComponentInstance(pico, null);
            fail("did not throw exception on missing 'instance' variable");
        } catch (BeanShellScriptCompositionException bssie) {
            // success
        }
    }

}
