package org.nanocontainer.remoting.server.transports.socket;

import org.nanocontainer.remoting.common.ThreadPool;
import org.nanocontainer.remoting.server.ServerMonitor;
import org.nanocontainer.remoting.server.ServerSideClientContextFactory;
import org.nanocontainer.remoting.server.adapters.InvocationHandlerAdapter;
import org.nanocontainer.remoting.server.transports.AbstractServerStreamReadWriter;
import org.nanocontainer.remoting.server.transports.ServerCustomStreamReadWriter;
import org.nanocontainer.remoting.server.transports.piped.AbstractPipedServer;


public class CompleteCustomStreamPipedServer extends AbstractPipedServer {
    public CompleteCustomStreamPipedServer(InvocationHandlerAdapter invocationHandlerAdapter, ServerMonitor serverMonitor, ThreadPool threadPool, ServerSideClientContextFactory contextFactory) {
        super(invocationHandlerAdapter, serverMonitor, threadPool, contextFactory);
    }

    protected AbstractServerStreamReadWriter createServerStreamReadWriter() {
        return new ServerCustomStreamReadWriter(m_serverMonitor, m_threadPool);
    }
}
