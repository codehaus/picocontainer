package org.picocontainer.parameters;

import org.picocontainer.ComponentAdapter;

/**
 * anybody but me. 
 * @author ko5tik
 *
 */
public class NotMe extends Filter {

	Object me;

	public NotMe(Lookup delegate, Object me) {
		super(delegate);
		this.me = me;
	}

	@Override
	public boolean evaluate(ComponentAdapter adapter) {
		return me != adapter;
	}

}
