using NUnit.Framework;
using PicoContainer.Core;
using PicoContainer.Core.Alternatives;
using PicoContainer.Core.Defaults;

namespace Test.Alternatives
{
	[TestFixture]
	public class ImplementationHidingWithDefaultPicoContainerTestCase : AbstractImplementationHidingPicoContainerTestCase
	{
		protected override IMutablePicoContainer CreateImplementationHidingPicoContainer() 
		{
			return CreatePicoContainer(null);
		}

		protected override IMutablePicoContainer CreatePicoContainer(IPicoContainer parent) 
		{
			return new DefaultPicoContainer(new CachingComponentAdapterFactory(new ImplementationHidingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory(), false)), parent);
		}

		protected override IMutablePicoContainer CreatePicoContainer(IPicoContainer parent, ILifecycleManager lifecycleManager) 
		{
			return new DefaultPicoContainer(new CachingComponentAdapterFactory(new ImplementationHidingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory(), false)), parent, lifecycleManager);
		}
	}
}
