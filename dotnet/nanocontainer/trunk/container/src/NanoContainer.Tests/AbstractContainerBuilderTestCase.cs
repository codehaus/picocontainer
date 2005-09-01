using System.Collections;
using NanoContainer.IntegrationKit;
using NanoContainer.Script;
using PicoContainer;

namespace NanoContainer.Tests
{
	public class AbstractContainerBuilderTestCase
	{
		protected IMutablePicoContainer BuildContainer(ContainerBuilder containerBuilder,
			IList assemblies)
		{
			ContainerBuilderHelper cbh = new ContainerBuilderHelper(containerBuilder);
			return cbh.Build(assemblies);
		}

		protected IMutablePicoContainer BuildContainer(ContainerBuilder containerBuilder,
		                                        IMutablePicoContainer parentContainer,
		                                        IList assemblies)
		{
			ContainerBuilderHelper cbh = new ContainerBuilderHelper(containerBuilder);
			return cbh.Build(parentContainer, assemblies);
		}
	}
}