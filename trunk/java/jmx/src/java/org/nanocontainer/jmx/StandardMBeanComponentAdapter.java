/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Ward                                    		 *
 *****************************************************************************/

package org.nanocontainer.jmx;

import org.picocontainer.defaults.InstanceComponentAdapter;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;

import javax.management.NotCompliantMBeanException;

/**
 * This component adapter is needed to ensure that a StandardMBean is registered with the MBeanServer
 *
 * @author Michael Ward
 * @version $Revision$
 */
public class StandardMBeanComponentAdapter extends InstanceComponentAdapter {

	/**
	 *
	 * @param implementation the actual instance of the management interface
	 * @param management represents the interface to expose via JMX
	 */
	public StandardMBeanComponentAdapter(Object implementation, Class management) throws AssignabilityRegistrationException, NotConcreteRegistrationException, NotCompliantMBeanException {
		super(management, StandardMBeanFactory.buildStandardMBean(implementation, management));
	}

	protected void checkTypeCompatibility() throws AssignabilityRegistrationException {
		// override and skip check standardMBean utilizes a proxy so check may not be valid
    }
}
