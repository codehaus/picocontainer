/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.picoextras.swing;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class IconHelper {
    public static final String DEFAULT_COMPONENT_ICON = "/org/picoextras/swing/icons/defaultcomponent.gif";
    public static final String PICO_CONTAINER_ICON = "/org/picoextras/swing/icons/picocontainer.gif";

    private static Map cache;

	static {
		cache = new HashMap();
	}

	public static Icon getIcon(String path) {
		if (cache.containsKey(path)) {
			return (Icon) cache.get(path);
		}
        URL url = IconHelper.class.getResource(path);
        Icon icon = new ImageIcon(url);
		cache.put(path, icon);
		return icon;
    }
}