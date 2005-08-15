using System.Collections;
using NanoContainer.IntegrationKit;
using PicoContainer;
using PicoContainer.Defaults;

namespace NanoContainer.Tests
{
	public class AbstractContainerBuilderTestCase
	{
		protected IPicoContainer BuildContainer(ContainerBuilder builder,
		                                        IPicoContainer parentContainer,
		                                        IList assemblies)
		{
			SimpleReference simpleReference = new SimpleReference();
			SimpleReference parentReference = new SimpleReference();
			parentReference.Set(parentContainer);
			builder.BuildContainer(simpleReference, parentReference, assemblies);
			return simpleReference.Get() as IPicoContainer;
		}
	}
}