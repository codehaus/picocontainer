/*****************************************************************************
 * Copyright (c) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Ward                                             *
 *****************************************************************************/

package org.nanocontainer.jmx;

/**
 * This is used by the tests!
 *
 * @author Michael Ward
 * @version $Revision$
 */
public class SampleMBean implements SampleInterface {

	public int getCount() {
		return 1;
	}

}
