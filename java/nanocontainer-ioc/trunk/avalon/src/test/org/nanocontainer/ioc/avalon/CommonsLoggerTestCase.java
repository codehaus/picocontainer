/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.nanocontainer.ioc.avalon;

import junit.framework.TestCase;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easymock.MockControl;
import org.nanocontainer.ioc.avalon.CommonsLogger;
import org.nanocontainer.ioc.avalon.NullArgumentException;

/**
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Id$
 */
public class CommonsLoggerTestCase extends TestCase {
    final MockControl mockControl = MockControl.createStrictControl(Log.class);
    final Log mock = (Log) mockControl.getMock();
    
    final CommonsLogger cl = new CommonsLogger( mock );
    final Logger logger = cl;
    final Log log = cl;
    
    public void testConstructor()
    {
        new CommonsLogger( LogFactory.getLog("blah") );
        try
        {
            new CommonsLogger( null );
            fail( "Expected an exception!" );
        }
        catch( NullArgumentException th ) {}
        
    }
    
    public void testLoggerDebug()
    {
        final Exception ex = new Exception("blah");
        mock.isDebugEnabled();
        mockControl.setReturnValue(true);
        mock.debug( "blah" );
        mock.debug( "blah", ex );
        mockControl.replay();
        
        assertTrue(logger.isDebugEnabled());
        logger.debug("blah");
        logger.debug("blah",ex);
        
        mockControl.verify();
    }

    public void testLoggerInfo()
    {
        final Exception ex = new Exception("blah");
        mock.isInfoEnabled();
        mockControl.setReturnValue(true);
        mock.info( "blah" );
        mock.info( "blah", ex );
        mockControl.replay();
        
        assertTrue(logger.isInfoEnabled());
        logger.info("blah");
        logger.info("blah",ex);

        mockControl.verify();
    }

    public void testLoggerWarn()
    {
        final Exception ex = new Exception("blah");
        mock.isWarnEnabled();
        mockControl.setReturnValue(true);
        mock.warn( "blah" );
        mock.warn( "blah", ex );
        mockControl.replay();
        
        assertTrue(logger.isWarnEnabled());
        logger.warn("blah");
        logger.warn("blah",ex);

        mockControl.verify();
    }

    public void testLoggerError()
    {
        final Exception ex = new Exception("blah");
        mock.isErrorEnabled();
        mockControl.setReturnValue(true);
        mock.error( "blah" );
        mock.error( "blah", ex );
        mockControl.replay();
        
        assertTrue(logger.isErrorEnabled());
        logger.error("blah");
        logger.error("blah",ex);

        mockControl.verify();
    }

    public void testLoggerFatalError()
    {
        final Exception ex = new Exception("blah");
        mock.isFatalEnabled();
        mockControl.setReturnValue(true);
        mock.fatal( "blah" );
        mock.fatal( "blah", ex );
        mockControl.replay();
        
        assertTrue(logger.isFatalErrorEnabled());
        logger.fatalError("blah");
        logger.fatalError("blah",ex);

        mockControl.verify();
    }

    
    public void testGetChildLogger()
    {
        assertNotNull(logger.getChildLogger("name"));
    }
    
    
    
    
    public void testLogTrace()
    {
        final Exception ex = new Exception("blah");
        mock.isTraceEnabled();
        mockControl.setReturnValue(true);
        mock.trace( "blah" );
        mock.trace( "blah", ex );
        mockControl.replay();
        
        assertTrue(log.isTraceEnabled());
        log.trace("blah");
        log.trace("blah",ex);
        
        mockControl.verify();
    }

    public void testLogDebug()
    {
        final Exception ex = new Exception("blah");
        mock.isDebugEnabled();
        mockControl.setReturnValue(true);
        mock.debug( "blah" );
        mock.debug( "blah", ex );
        mockControl.replay();
        
        assertTrue(log.isDebugEnabled());
        log.debug("blah");
        log.debug("blah",ex);
        
        mockControl.verify();
    }

    public void testLogInfo()
    {
        final Exception ex = new Exception("blah");
        mock.isInfoEnabled();
        mockControl.setReturnValue(true);
        mock.info( "blah" );
        mock.info( "blah", ex );
        mockControl.replay();
        
        assertTrue(log.isInfoEnabled());
        log.info("blah");
        log.info("blah",ex);

        mockControl.verify();
    }

    public void testLogWarn()
    {
        final Exception ex = new Exception("blah");
        mock.isWarnEnabled();
        mockControl.setReturnValue(true);
        mock.warn( "blah" );
        mock.warn( "blah", ex );
        mockControl.replay();
        
        assertTrue(log.isWarnEnabled());
        log.warn("blah");
        log.warn("blah",ex);

        mockControl.verify();
    }

    public void testLogError()
    {
        final Exception ex = new Exception("blah");
        mock.isErrorEnabled();
        mockControl.setReturnValue(true);
        mock.error( "blah" );
        mock.error( "blah", ex );
        mockControl.replay();
        
        assertTrue(log.isErrorEnabled());
        log.error("blah");
        log.error("blah",ex);

        mockControl.verify();
    }

    public void testLogFatalError()
    {
        final Exception ex = new Exception("blah");
        mock.isFatalEnabled();
        mockControl.setReturnValue(true);
        mock.fatal( "blah" );
        mock.fatal( "blah", ex );
        mockControl.replay();
        
        assertTrue(log.isFatalEnabled());
        log.fatal("blah");
        log.fatal("blah",ex);

        mockControl.verify();
    }

}
