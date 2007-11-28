package org.picocontainer.parameters;

import org.picocontainer.ComponentAdapter;

import sun.security.action.GetLongAction;

/**
 * anybody but me. 
 * @author ko5tik
 *
 */
public class NotMe extends Filter {

	ComponentAdapter me;

	public NotMe(Lookup delegate, ComponentAdapter me) {
		super(delegate);
		this.me = me;
	}

	@Override
	public boolean evaluate(ComponentAdapter adapter) {
		return me != adapter;
	}

	public String toString() {
		return "NotMe(" + me + ")[" + delegate + "]";
	}
}
