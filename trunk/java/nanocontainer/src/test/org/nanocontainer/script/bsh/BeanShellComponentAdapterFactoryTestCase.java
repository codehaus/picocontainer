/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.picoextras.script.bsh;

import junit.framework.TestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.ArrayList;

/**
 * <a href="http://www.junit.org/">JUnit</a>
 * {@link junit.framework.TestCase testcase} for
 * BeanShellComponentAdapter.
 *
 * @author <a href="mail at leosimons dot com">Leo Simons</a>
 * @version $Id$
 */
public class BeanShellComponentAdapterFactoryTestCase extends TestCase {
    public void testGetComponentInstance() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation("whatever", ArrayList.class);

        ComponentAdapter adapter = new BeanShellComponentAdapterFactory().createComponentAdapter(
                "thekey", ScriptableDemoBean.class, null);

        ScriptableDemoBean bean = (ScriptableDemoBean) adapter.getComponentInstance(pico);

        assertEquals("Bsh demo script should have set the key", "thekey", bean.key);

        assertTrue(bean.whatever instanceof ArrayList);
    }
}
