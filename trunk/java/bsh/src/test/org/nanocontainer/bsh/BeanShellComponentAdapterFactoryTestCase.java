/* ====================================================================
 The Jicarilla Software License

 Copyright (c) 2003 Leo Simons.
 All rights reserved.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
==================================================================== */
package org.nanocontainer.bsh;

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
