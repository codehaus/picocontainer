package org.picocontainer.web.sample.jqueryemail;

import org.picocontainer.web.remoting.NullPicoWebRemotingMonitor;

public class JQueryEmailWebRemotingMonitor extends NullPicoWebRemotingMonitor {

    protected Class<? extends RuntimeException> getAppBaseRuntimeException() {
        return JQueryEmailException.class;
    }
}
