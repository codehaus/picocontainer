/* ====================================================================
 * Copyright 2005 NanoContainer Committers
 * Portions copyright 2001 - 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.nanocontainer.remoting.test.tools.generator;

import org.nanocontainer.remoting.client.ClientMonitor;
import org.nanocontainer.remoting.client.ConnectionPinger;
import org.nanocontainer.remoting.client.transports.AbstractClientInvocationHandler;
import org.nanocontainer.remoting.commands.ExceptionReply;
import org.nanocontainer.remoting.commands.MethodReply;
import org.nanocontainer.remoting.commands.MethodRequest;
import org.nanocontainer.remoting.commands.OpenConnectionReply;
import org.nanocontainer.remoting.commands.OpenConnectionRequest;
import org.nanocontainer.remoting.commands.Reply;
import org.nanocontainer.remoting.commands.Request;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.server.ServerInvocationHandler;

import java.lang.reflect.Method;

/**
 * TestInvocationHandler
 *
 * @author <a href="mailto:vinayc@apache">Vinay Chandrasekharan</a>
 * @version 1.0
 */
public class TestInvocationHandler extends AbstractClientInvocationHandler implements ServerInvocationHandler {


    public TestInvocationHandler(ThreadPool threadPool, ClientMonitor clientMonitor, ConnectionPinger connectionPinger) {
        super(threadPool, clientMonitor, connectionPinger);
    }

    public Reply handleInvocation(Request request) {
        return handleInvocation(request, "test");
    }

    public Reply handleInvocation(Request request, Object connectionDetails) {
        if (request instanceof OpenConnectionRequest) {
            return new OpenConnectionReply();
        } else if (request instanceof MethodRequest) {
            MethodRequest methodRequest = (MethodRequest) request;
            //System.out.println("methodRequest[" + methodRequest.getMethodSignature() + "]");
            Method[] methods = TestRemoteInterface.class.getMethods();
            for (int i = 0; i < methods.length; i++) {
                try {
                    if (methodRequest.getMethodSignature().indexOf(methods[i].getName()) != -1) {
                        Object[] _arguments = methodRequest.getArgs();
                        for (int j = 0; j < _arguments.length; j++) {

                            if (!TestRemoteInterface.class.getField(methods[i].getName() + "_arg" + j).get(null).equals(_arguments[j])) {
                                return new ExceptionReply(new Exception(methodRequest.getMethodSignature() + ": arguments not marshalled correctly \n expected[" + TestRemoteInterface.class.getField(methods[i].getName() + "_arg" + j).get(null) + "] received[" + _arguments[j] + "]"));
                            }
                        }
                        MethodReply methodReply = null;
                        if (methods[i].getReturnType() != Void.TYPE) {
                            methodReply = new MethodReply(TestRemoteInterface.class.getField(methods[i].getName() + "_retValue").get(null));
                        } else {
                            methodReply = new MethodReply();
                        }
                        return methodReply;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ExceptionReply(e);
                }

            }
        }
        return null;
    }

    /*
     * @see AbstractClientInvocationHandler#tryReconnect()
     */
    protected boolean tryReconnect() {
        return true;
    }

    /*
     * @see ClientInvocationHandler#getLastRealRequest()
     */
    public long getLastRealRequest() {
        return 0;
    }

}