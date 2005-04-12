using System.IO;
using NanoContainer.IntegrationKit;
using PicoContainer;

namespace NanoContainer.Script
{
	public abstract class ScriptedContainerBuilder : LifeCycleContainerBuilder
	{
		protected readonly StreamReader stream;

		public ScriptedContainerBuilder(StreamReader reader)
		{
			this.stream = reader;
		}

		protected override IMutablePicoContainer CreateContainer(IPicoContainer parentContainer, object assemblyScope)
		{
			try
			{
				return CreateContainerFromScript(parentContainer, assemblyScope);
			}
			finally
			{
				try
				{
					stream.Close();
				}
				catch (IOException)
				{
				}
			}
		}

		protected abstract IMutablePicoContainer CreateContainerFromScript(IPicoContainer parentContainer, object assemblyScope);

		protected override void ComposeContainer(IMutablePicoContainer container, object assemblyScope)
		{
			// do nothing. assume that this is done in createContainer().
		}
	}
}