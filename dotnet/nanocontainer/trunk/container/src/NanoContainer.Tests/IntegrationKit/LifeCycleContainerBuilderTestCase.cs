using System;
using NanoContainer.IntegrationKit;
using NMock;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;

namespace NanoContainer.Tests.IntegrationKit
{
	[TestFixture]
	public class LifeCycleContainerBuilderTestCase
	{
		[Test]
		public void KillContainer()
		{
			Mock mockPicoContainer = new DynamicMock(typeof(IMutablePicoContainer));
			IMutablePicoContainer picoContainer = mockPicoContainer.MockInstance as IMutablePicoContainer;
			mockPicoContainer.Expect("Stop");
			mockPicoContainer.Expect("Dispose");
			mockPicoContainer.ExpectAndReturn("Parent", new DefaultPicoContainer(picoContainer));
			mockPicoContainer.Strict = true;

			LifeCycleContainerBuilder lifeCycleContainerBuilder = new StubLifeCycleContainerBuilder();
			SimpleReference reference = new SimpleReference();
			reference.Set(mockPicoContainer.MockInstance);

			lifeCycleContainerBuilder.KillContainer(reference);
			mockPicoContainer.Verify();
		}
	}

	public class StubLifeCycleContainerBuilder : LifeCycleContainerBuilder
	{
		protected override void ComposeContainer(IMutablePicoContainer container, object assemblyScope)
		{
			throw new NotImplementedException();
		}

		protected override IMutablePicoContainer CreateContainer(IPicoContainer parentContainer, object assemblyScope)
		{
			throw new NotImplementedException();
		}
	}
}
