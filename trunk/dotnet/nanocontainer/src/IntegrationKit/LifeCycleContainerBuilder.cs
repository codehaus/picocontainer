using System;
using PicoContainer.Core;
using PicoContainer.Defaults;

namespace NanoContainer.IntegrationKit
{
	public abstract class LifeCycleContainerBuilder : ContainerBuilder
	{
		public void BuildContainer(IObjectReference containerRef,
		                           IObjectReference parentContainerRef,
		                           Object assemblyScope)
		{
			IMutablePicoContainer parent = parentContainerRef == null ? null : parentContainerRef.Get() as IMutablePicoContainer;
			IMutablePicoContainer container = CreateContainer(parent, assemblyScope);

			// register the child in the parent so that lifecycle can be propagated down the hierarchy
			if (parent != null)
			{
				parent.UnregisterComponentByInstance(container);
				parent.RegisterComponentInstance(container, container);
			}

			ComposeContainer(container, assemblyScope);
			container.Start();

			containerRef.Set(container);

		}

		public void KillContainer(IObjectReference containerRef)
		{
			IMutablePicoContainer pico = containerRef == null ? null : containerRef.Get() as IMutablePicoContainer;

			try
			{
				if (pico != null)
				{
					pico.Stop();
					pico.Dispose();

					IMutablePicoContainer parent = pico.Parent as IMutablePicoContainer;
					if (parent != null)
					{
						parent.UnregisterComponentByInstance(pico);
					}
				}
			}
			finally
			{
				pico = null;
			}
		}

		protected abstract void ComposeContainer(IMutablePicoContainer container, Object assemblyScope);

		protected abstract IMutablePicoContainer CreateContainer(IPicoContainer parentContainer,
		                                                         Object assemblyScope);
	}
}