using System.Collections;
using System.IO;
using NanoContainer.IntegrationKit;
using PicoContainer;

namespace NanoContainer.Script
{
	public abstract class ScriptedContainerBuilder : LifeCycleContainerBuilder
	{
		private readonly StreamReader streamReader;

		public ScriptedContainerBuilder(StreamReader script)
		{
			this.streamReader = script;
		}

		protected StreamReader StreamReader
		{
			get { return streamReader; }
		}

		protected override IMutablePicoContainer CreateContainer(IPicoContainer parentContainer, object assemblyScope)
		{
			try
			{
				return CreateContainerFromScript(parentContainer, assemblyScope as IList);
			}
			finally
			{
				try
				{
					StreamReader.Close();
				}
				catch (IOException)
				{
					// ignore
				}
			}
		}

		protected abstract IMutablePicoContainer CreateContainerFromScript(IPicoContainer parentContainer, IList assemblyScope);

		protected override void ComposeContainer(IMutablePicoContainer container, object assemblyScope)
		{
			// do nothing. assume that this is done in createContainer().
		}
	}
}