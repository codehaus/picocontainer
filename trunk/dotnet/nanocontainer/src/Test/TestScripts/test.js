import PicoContainer;
import PicoContainer.Defaults;

class Class1 {

	protected var parent : IPicoContainer;
	protected var container : IMutablePicoContainer;
	 
	public function set Parent(value : IPicoContainer) {
 		parent = value;
	}

	public function Compose() : IMutablePicoContainer {
		container = new DefaultPicoContainer(parent);
		
		container.RegisterComponentInstance("hello","JScript");
		return container;
	}
}


