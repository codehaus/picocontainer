/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.aop.pointcuts;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.picocontainer.aop.ComponentPointcut;

/**
 * Component pointcut that matches the component name against a regular
 * expression.
 * 
 * @author Stephen Molitor
 */
public class NameMatchesComponentPointcut implements ComponentPointcut {

    private final Pattern pattern;

    /**
     * Creates a new <code>NameMatchesComponentPointcut</code> that will match
     * the component key against the given regular expression.
     * 
     * @param regex the regular expression to match against the component name.
     * @throws PatternSyntaxException if the regular expression is invalid.
     */
    public NameMatchesComponentPointcut(String regex) throws PatternSyntaxException {
        pattern = Pattern.compile(regex);
    }

    /**
     * Tests to see if the component key's toString() value matches the regular
     * expression passed to the constructor.
     * 
     * @param componentKey the component key to match against.
     * @return true if the regular expression passed to the constructor matches
     *         against <code>componentKey</code>, else false.
     */
    public boolean picks(Object componentKey) {
        String componentName = componentKey.toString();
        return pattern.matcher(componentName).matches();
    }

}
