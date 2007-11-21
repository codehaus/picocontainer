package org.picocontainer.parameters;

import org.picocontainer.Parameter;
import org.picocontainer.PicoVisitor;

public abstract  class AbstractParameter implements Parameter {
	
	Lookup lookup;
	

	public AbstractParameter(Lookup lookup) {
		this.lookup = lookup;
	}


	public void accept(PicoVisitor visitor) {
		visitor.visitParameter(this);
	}
}
