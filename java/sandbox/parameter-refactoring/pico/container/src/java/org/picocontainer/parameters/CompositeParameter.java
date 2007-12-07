package org.picocontainer.parameters;

import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;

public class CompositeParameter implements Parameter {

	Scalar[] scalars;
	
	
	public CompositeParameter(Scalar... scalars) {
		this.scalars = scalars;
	}


	public Object resolveInstance(PicoContainer container) {
		Object retval;
		for( Scalar scalar: scalars) {
			retval = scalar.resolveInstance(container);
			if(retval != null) {
				return retval;
			}
		}
		return null;
	}

	public boolean isResolvable(PicoContainer container) {
		for( Scalar scalar: scalars) {
			if(scalar.isResolvable(container)) {
				return true;
			}
		}	
		return false;
	}

	public boolean canSatisfy(PicoContainer container, Class expectedType) {
		for( Scalar scalar: scalars) {
			if(scalar.canSatisfy(container,expectedType)) {
				return true;
			}
		}	
		return false;
	}

	public void verify(PicoContainer container) {
		for( Scalar scalar: scalars) {
			try {
				scalar.verify(container);
				// at leas on ehas to be verified properly
				return;
			} catch(PicoCompositionException ex) {
				// that's ok, maybe collect them? 
			}
		}
		throw new PicoCompositionException("no scalar was verified successfully");
	}

	public void accept(PicoVisitor visitor) {
		visitor.visitParameter(this);
	}

}
