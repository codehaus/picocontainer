/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.defaults;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.nanocontainer.nanoaop.ComponentPointcut;
import org.nanocontainer.nanoaop.MalformedRegularExpressionException;

/**
 * Component pointcut that matches the component name against a regular
 * expression.
 * 
 * @author Stephen Molitor
 * @version $Revision$
 */
public class NameMatchesComponentPointcut implements ComponentPointcut {

    private final Pattern patern;
    private final Perl5Matcher matcher = new Perl5Matcher();

    /**
     * Creates a new <code>NameMatchesComponentPointcut</code> that will match
     * the component key against the given regular expression. The regular
     * expression must be an <a
     * href="http://jakarta.apache.org/oro/index.html">ORO </a> Perl5 regular
     * expression.
     * 
     * @param regex the regular expression to match against the component name.
     * @throws MalformedRegularExpressionException if the regular expression is
     *         invalid.
     */
    public NameMatchesComponentPointcut(String regex) throws MalformedRegularExpressionException {
        Perl5Compiler compiler = new Perl5Compiler();
        try {
            patern = compiler.compile(regex);
        } catch (MalformedPatternException e) {
            throw new MalformedRegularExpressionException("malformed component name regular expression", e);
        }
    }

    /**
     * Tests to see if the component key matches the regular expression passed
     * to the constructor. Always returns false if <code>componentKey</code>
     * is not an instance of</code> java.lang.String</code>.
     * 
     * @param componentKey the component key to match against.
     * @return true if the regular expression passed to the constructor matches
     *         against <code>componentKey</code>, else false.
     */
    public boolean picks(Object componentKey) {
        if (!(componentKey instanceof String)) {
            return false;
        }
        String componentName = (String) componentKey;
        return matcher.contains(componentName, patern);
    }

}