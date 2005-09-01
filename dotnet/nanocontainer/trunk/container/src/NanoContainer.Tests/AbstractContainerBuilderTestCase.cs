using System.Collections;
using NanoContainer.IntegrationKit;
using PicoContainer;

namespace NanoContainer.Tests
{
	public class AbstractContainerBuilderTestCase
	{
		protected IMutablePicoContainer BuildContainer(ContainerBuilder containerBuilder,
			IList assemblies)
		{
			ContainerBuilderFacade cbf = new ContainerBuilderFacade(containerBuilder);
			return cbf.Build(assemblies);
		}

		protected IMutablePicoContainer BuildContainer(ContainerBuilder containerBuilder,
		                                        IMutablePicoContainer parentContainer,
		                                        IList assemblies)
		{
			ContainerBuilderFacade cbf = new ContainerBuilderFacade(containerBuilder);
			return cbf.Build(parentContainer, assemblies);
		}
	}
}