package org.picoextras.piccolo;

import java.awt.Color;

import javax.swing.ImageIcon;

public interface Constants {
	public static final Color PICO_GREEN = new Color(42, 165, 38);
	public static final Color PICO_STROKE = Color.BLACK;
	public static final Color PICO_FILL = Color.WHITE;
	public static final String USER_OBJECT = "UserObject";
	public static final String TOOL_TIP = "ToolTip";
	public static final ImageIcon picoContainerIcon = new ImageIcon(Constants.class.getResource("/picocontainer.gif"));
	public static final ImageIcon defaultComponentIcon = new ImageIcon(Constants.class.getResource("/defaultcomponent.gif"));
}
