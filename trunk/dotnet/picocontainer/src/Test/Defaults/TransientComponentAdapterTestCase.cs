using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;

namespace Test.Defaults
{
	/// <summary>
	/// Summary description for TransientIComponentAdapterTestCase.
	/// </summary>
	[TestFixture]
	public class TransientIComponentAdapterTestCase
	{
		public void testNonCachingComponentAdapterReturnsNewInstanceOnEachCallToGetComponentInstance()
		{
			ConstructorInjectionComponentAdapter componentAdapter = new ConstructorInjectionComponentAdapter("blah", typeof (object));
			object o1 = componentAdapter.ComponentInstance;
			object o2 = componentAdapter.ComponentInstance;
			Assert.IsNotNull(o1);
			Assert.IsFalse(o1.Equals(o2));
		}


		public class Service
		{
		}

		public class TransientComponent
		{
			public Service service;

			public TransientComponent(Service service)
			{
				this.service = service;
			}
		}

		[Test]
		public void testDefaultPicoContainerReturnsNewInstanceForEachCallWhenUsingTransientIComponentAdapter()
		{
			DefaultPicoContainer picoContainer = new DefaultPicoContainer();
			picoContainer.RegisterComponentImplementation(typeof (Service));
			picoContainer.RegisterComponent(new ConstructorInjectionComponentAdapter(typeof (TransientComponent), typeof (TransientComponent)));
			TransientComponent c1 = (TransientComponent) picoContainer.GetComponentInstance(typeof (TransientComponent));
			TransientComponent c2 = (TransientComponent) picoContainer.GetComponentInstance(typeof (TransientComponent));
			Assert.IsFalse(c1.Equals(c2));
			Assert.AreSame(c1.service, c2.service);
		}

		public class A
		{
			public A()
			{
				Assert.Fail("verification should not instantiate");
			}
		}

		public class B
		{
			public B(A a)
			{
				Assert.Fail("verification should not instantiate");
			}
		}


		[Test]
		public void testSuccessfulVerificationWithNoDependencies()
		{
			InstantiatingComponentAdapter componentAdapter = new ConstructorInjectionComponentAdapter("foo", typeof (A));
			componentAdapter.Verify();
		}

		[Test]
		public void testFailingVerificationWithUnsatisfiedDependencies()
		{
			IComponentAdapter componentAdapter = new ConstructorInjectionComponentAdapter("foo", typeof (B));
			componentAdapter.Container = new DefaultPicoContainer();
			try
			{
				componentAdapter.Verify();
				Assert.Fail();
			}
			catch (UnsatisfiableDependenciesException)
			{
			}
		}

	}
}