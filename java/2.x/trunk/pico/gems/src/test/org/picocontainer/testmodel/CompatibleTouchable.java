/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaibe                                            *
 *****************************************************************************/

package org.picocontainer.testmodel;

import java.io.Serializable;


/**
 * Method compatible Touchable.
 * 
 * @author J&ouml;rg Schaible
 */
public class CompatibleTouchable implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -364907438040592500L;
	private boolean wasTouched;

    public void touch() {
        wasTouched = true;
    }

    public boolean wasTouched() {
        return wasTouched;
    }
}