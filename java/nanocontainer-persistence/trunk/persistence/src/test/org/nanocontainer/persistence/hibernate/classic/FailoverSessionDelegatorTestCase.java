/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.persistence.hibernate.classic;

import junit.framework.TestCase;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

import org.nanocontainer.persistence.hibernate.pojo.Pojo;

/**
 * test case for session delegator
 * @author Konstantin Pribluda
 * @version $Revision$
 * @deprecated together with class to be tested
 */
public class FailoverSessionDelegatorTestCase extends TestCase {
    
    
    public void testSessionCreationAndDisposal() throws Exception {
        
        SessionFactory factory = (new ConstructableConfiguration()).buildSessionFactory();
        
        FailoverSessionDelegator delegator = new FailoverSessionDelegator(factory);
        
        Session session = delegator.getSession();
        assertNotNull(session);
        
        assertSame(session,delegator.getSession());
        
        // test that closing invalidates session
        delegator.close();
        assertNull(delegator.session);
        assertNotSame(session,delegator.getSession());
        
        session = delegator.getSession();
        
        // produce error
        try {
            assertNotNull(delegator.save(new Pojo()));
            fail("did not bombed on hibernate error");
        } catch(HibernateException ex) {
            // that's ok
        }
        assertNotSame(session,delegator.getSession());      
                     
    }
}

