/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.picocontainer.testmodel;

import junit.framework.TestCase;

import org.picocontainer.Disposable;
import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;


public abstract class RecordingLifecycle implements Startable, Disposable {
    private final StringBuffer recording;

    protected RecordingLifecycle(StringBuffer recording) {
        this.recording = recording;
    }

    public void start() {
        recording.append("<" + code());
    }

    public void stop() {
        recording.append(code() + ">");
    }

    public void dispose() {
        recording.append("!" + code());
    }

    private String code() {
        String name = getClass().getName();
        return name.substring(name.indexOf('$') + 1);
    }
    

    public static class One extends RecordingLifecycle {
        public One(StringBuffer sb) {
            super(sb);
        }
    }

    public static class Two extends RecordingLifecycle {
        public Two(StringBuffer sb, One one) {
            super(sb);
            TestCase.assertNotNull(one);
        }
    }

    public static class Three extends RecordingLifecycle {
        public Three(StringBuffer sb, One one, Two two) {
            super(sb);
            TestCase.assertNotNull(one);
            TestCase.assertNotNull(two);
        }
    }

    public static class Four extends RecordingLifecycle {
        public Four(StringBuffer sb, Two two, Three three, One one) {
            super(sb);
            TestCase.assertNotNull(one);
            TestCase.assertNotNull(two);
            TestCase.assertNotNull(three);
        }
    }

    public static class FiveTriesToBeMalicious extends RecordingLifecycle {
        public FiveTriesToBeMalicious(StringBuffer sb, PicoContainer pc) {
            super(sb);
            TestCase.assertNotNull(pc);
            sb.append("Whao! Should not get instantiated!!");
        }
    }
    
}