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
 * @author Stephen Molitor
 */
public class NameMatchesComponentPointcut implements ComponentPointcut {

    private final Pattern patern;
    private final Perl5Matcher matcher = new Perl5Matcher();

    public NameMatchesComponentPointcut(String regex) {
        Perl5Compiler compiler = new Perl5Compiler();
        try {
            patern = compiler.compile(regex);
        } catch (MalformedPatternException e) {
            throw new MalformedRegularExpressionException("malformed component name regular expression", e);
        }
    }

    public boolean picks(Object componentKey) {
        if (!(componentKey instanceof String)) {
            return false;
        }
        String componentName = (String) componentKey;
        return matcher.contains(componentName, patern);
    }

}