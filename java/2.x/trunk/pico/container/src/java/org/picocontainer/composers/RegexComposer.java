/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.composers;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.monitors.ComposingMonitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Subsets components in a container, the keys for which match a regular expression.
 */
public class RegexComposer implements ComposingMonitor.Composer {

    private final Pattern pattern;
    private final String forNamedComponent;

    public RegexComposer(String pattern, String forNamedComponent) {
        this.pattern = Pattern.compile(pattern);
        this.forNamedComponent = forNamedComponent;
    }

    public RegexComposer() {
        pattern = null;
        forNamedComponent = null;
    }

    public Object compose(PicoContainer container, Object componentKey) {
        if (componentKey instanceof String
                && (forNamedComponent == null || forNamedComponent.equals(componentKey))) {
            Pattern pat = null;
            if (pattern == null) {
                pat = Pattern.compile((String) componentKey);
            } else {
                pat = pattern;
            }
            Collection<ComponentAdapter<?>> cas = container.getComponentAdapters();
            List retVal = new ArrayList();
            for (ComponentAdapter<?> componentAdapter : cas) {
                Object key = componentAdapter.getComponentKey();
                if (key instanceof String) {
                    Matcher matcher = pat.matcher((String) key);
                    if (matcher != null && matcher.find()) {
                        retVal.add(componentAdapter.getComponentInstance(container, ComponentAdapter.NOTHING.class));
                    }
                }
            }
            return retVal;
        }
        return null;
    }
}
