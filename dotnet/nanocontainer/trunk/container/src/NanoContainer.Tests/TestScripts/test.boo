import PicoContainer from PicoContainer
import PicoContainer.Defaults from PicoContainer

class BooInjector:
	
	[Property(Parent)]
	_parent as IPicoContainer
	
	def Compose() as IMutablePicoContainer:
		p = DefaultPicoContainer(_parent)
		p.RegisterComponentInstance("foo", "Boo")
		return p
