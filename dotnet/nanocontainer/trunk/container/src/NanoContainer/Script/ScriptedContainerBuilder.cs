using System.IO;
using NanoContainer.IntegrationKit;
using PicoContainer;

namespace NanoContainer.Script
{
	public abstract class ScriptedContainerBuilder : LifeCycleContainerBuilder
	{
		private readonly StreamReader script;

		public ScriptedContainerBuilder(StreamReader script)
		{
			this.script = script;
		}

		protected StreamReader Script
		{
			get { return script; }
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
					Script.Close();
				}
				catch (IOException)
				{
					// ignore
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