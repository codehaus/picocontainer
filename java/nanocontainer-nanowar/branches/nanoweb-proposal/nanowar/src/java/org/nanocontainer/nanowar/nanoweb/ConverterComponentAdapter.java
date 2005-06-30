package org.nanocontainer.nanowar.nanoweb;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.defaults.DecoratingComponentAdapter;

public class ConverterComponentAdapter extends DecoratingComponentAdapter {

	private static final long serialVersionUID = 3258407326930513973L;

	private Class type;

	public ConverterComponentAdapter(Class type, ComponentAdapter ca) {
		super(ca);
		this.type = type;
	}

	public Class getType() {
		return type;
	}

}
