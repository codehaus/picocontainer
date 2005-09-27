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
package org.nanocontainer.remoting.server.transports.socket;

import org.nanocontainer.remoting.common.RegistryHelper;
import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.server.ServerMonitor;
import org.nanocontainer.remoting.server.ServerSideClientContextFactory;
import org.nanocontainer.remoting.server.adapters.InvocationHandlerAdapter;
import org.nanocontainer.remoting.server.transports.AbstractServerStreamReadWriter;
import org.nanocontainer.remoting.server.transports.ServerObjectStreamReadWriter;


/**
 * Class PartialSocketObjectStreamServer
 *
 * @author Paul Hammant
 * @author Peter Royal
 * @version $Revision: 1.2 $
 */
public class PartialSocketObjectStreamServer extends AbstractPartialSocketStreamServer {

    private CompleteSocketObjectStreamPipedBinder m_completeSocketObjectStreamPipedBinder;


    /*
     * Construct a PartialSocketObjectStreamServer
     *
     * @param invocationHandlerAdapter the handler
     * @param serverMonitor the monitor
     * @param port ther port
     */
    public PartialSocketObjectStreamServer(InvocationHandlerAdapter invocationHandlerAdapter, ServerMonitor serverMonitor, ThreadPool threadPool, ServerSideClientContextFactory contextFactory, int port) {
        super(invocationHandlerAdapter, serverMonitor, threadPool, contextFactory);

        m_completeSocketObjectStreamPipedBinder = new CompleteSocketObjectStreamPipedBinder(super.getInovcationHandlerAdapter(), serverMonitor, threadPool, contextFactory);
        new RegistryHelper().put("/.nanocontainerRemoting/optimizations/port=" + port, m_completeSocketObjectStreamPipedBinder);
    }

    /**
     * Stop
     */
    public void stop() {
        super.stop();
        m_completeSocketObjectStreamPipedBinder.stop();
    }

    /**
     * Create a Server Stream Read Writer.
     *
     * @return The Server Stream Read Writer.
     */
    protected AbstractServerStreamReadWriter createServerStreamReadWriter() {
        return new ServerObjectStreamReadWriter(m_serverMonitor, m_threadPool);
    }

}
