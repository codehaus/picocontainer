/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.swing;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class IconHelper {
    public static final String DEFAULT_COMPONENT_ICON = "/org/picoextras/swing/icons/defaultcomponent.gif";
    public static final String PICO_CONTAINER_ICON = "/org/picoextras/swing/icons/picocontainer.gif";

    private static Map images = new HashMap();

    public static Icon getIcon(String path, boolean gray) {
        ImageIcon icon = (ImageIcon) images.get(path);
        if (icon == null) {
            URL url = IconHelper.class.getResource(path);
            if(url == null) {
                System.err.println("PicoContainer GUI: Couldn't load resource: " + path);
                return null;
            }
            icon = new ImageIcon(url);
            images.put(path, icon);
        }
        if(gray) {
            icon = new ImageIcon(GrayFilter.createDisabledImage(icon.getImage()));
        }
        return icon;
    }
}