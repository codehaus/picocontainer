using PicoContainer.Core;
using PicoContainer.Defaults;

namespace Test
{
	public class NameTranslator
	{
		private IPicoContainer parent;

		public IPicoContainer Parent
		{
			set { parent = value; }
		}

		public IMutablePicoContainer Compose()
		{
			DefaultPicoContainer p = new DefaultPicoContainer(parent);

			p.RegisterComponentInstance("hello", "C#");
			return p;
		}
	}
}