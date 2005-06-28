package org.nanocontainer.nanoweb;

import org.picocontainer.PicoContainer;

public interface ExpressionEvaluator {

    public void set(PicoContainer pico, Object root, String expression, Object value) throws Exception;

}
