package test;
import PicoContainer.Core.*;
import PicoContainer.Defaults.*;

class test
{
	private IMutablePicoContainer container;
	private IPicoContainer parent;
		
	/** @property */
	public void set_Parent(IPicoContainer value) 
	{
		parent = value;
	}

	public IMutablePicoContainer Compose()
	{
		container = new DefaultPicoContainer(parent);
		container.RegisterComponentInstance("hello","J#");

		return container;
	}

}
