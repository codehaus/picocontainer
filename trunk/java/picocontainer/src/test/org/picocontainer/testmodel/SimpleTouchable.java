/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.testmodel;

import java.io.Serializable;


/**
 * @author steve.freeman@m3p.co.uk
 */
public class SimpleTouchable implements Touchable, Serializable {

    public boolean wasTouched;

    public void wasTouched() {
        wasTouched = true;
    }
}
