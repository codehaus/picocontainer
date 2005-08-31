using PicoContainer;

namespace NanoContainer.Script
{
	public interface IScript
	{
		IPicoContainer Parent
		{
			set;
		}

		IMutablePicoContainer Compose();
	}
}
