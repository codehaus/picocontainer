using System;
using System.Collections;
using NUnit.Framework;
using PicoContainer.Defaults;
using PicoContainer;

namespace Test.Defaults
{
	[TestFixture]
	public class ConstructorInjectionComponentAdapterTestCase
	{
		public void testNonCachingComponentAdapterReturnsNewInstanceOnEachCallToGetComponentInstance()
		{
			ConstructorInjectionComponentAdapter componentAdapter = new ConstructorInjectionComponentAdapter("blah", typeof (object));
			object o1 = componentAdapter.ComponentInstance;
			object o2 = componentAdapter.ComponentInstance;
			Assert.IsNotNull(o1);
			Assert.IsFalse(o1 == o2);
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

		public void testDefaultPicoContainerReturnsNewInstanceForEachCallWhenUsingTransientComponentAdapter()
		{
			DefaultPicoContainer picoContainer = new DefaultPicoContainer();
			picoContainer.RegisterComponentImplementation(typeof (Service));
			picoContainer.RegisterComponent(new ConstructorInjectionComponentAdapter(typeof (TransientComponent), typeof (TransientComponent)));
			TransientComponent c1 = (TransientComponent) picoContainer.GetComponentInstance(typeof (TransientComponent));
			TransientComponent c2 = (TransientComponent) picoContainer.GetComponentInstance(typeof (TransientComponent));
			Assert.IsFalse(c1 == c2);
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
		public void SuccessfulVerificationWithNoDependencies()
		{
			InstantiatingComponentAdapter componentAdapter = new ConstructorInjectionComponentAdapter("foo", typeof (A));
			componentAdapter.Verify(componentAdapter.Container);
		}

		[ExpectedException(typeof (UnsatisfiableDependenciesException))]
		public void testFailingVerificationWithUnsatisfiedDependencies()
		{
			IComponentAdapter componentAdapter = new ConstructorInjectionComponentAdapter("foo", typeof (B));
			componentAdapter.Container = new DefaultPicoContainer();
			componentAdapter.Verify(componentAdapter.Container);
			Assert.Fail();
		}

		public class C1
		{
			public C1(C2 c2)
			{
				Assert.Fail("verification should not instantiate");
			}
		}

		public class C2
		{
			public C2(C1 i1)
			{
				Assert.Fail("verification should not instantiate");
			}
		}

		[Test]
		public void FailingVerificationWithCyclicDependencyException()
		{
			DefaultPicoContainer picoContainer = new DefaultPicoContainer();
			picoContainer.RegisterComponentImplementation(typeof (C1));
			picoContainer.RegisterComponentImplementation(typeof (C2));
			try
			{
				picoContainer.Verify();
				Assert.Fail();
			}
			catch (CyclicDependencyException e)
			{
				String message = e.Message;
				Assert.IsTrue(message.IndexOf("C1") + message.IndexOf("C2") > 0);
			}
		}

		public class D
		{
			public D(A a)
			{
			}
		}

		[Test]
		public void FailingVerificationWithPicoInitializationException()
		{
			DefaultPicoContainer picoContainer = new DefaultPicoContainer();
			picoContainer.RegisterComponentImplementation(typeof (A));
			picoContainer.RegisterComponentImplementation(typeof (B));
			picoContainer.RegisterComponentImplementation(typeof (D), typeof (D),
			                                              new IParameter[] {new ComponentParameter(), new ComponentParameter()});
			try
			{
				picoContainer.Verify();
				Assert.Fail();
			}
			catch (PicoInitializationException e)
			{
				string message = e.Message;
				string test = typeof (D).Name + "(" + typeof (A).FullName + ")";
				Assert.IsTrue(message.IndexOf(typeof (D).Name + "(" + typeof (A).FullName + ")") > 0);
			}
		}


	}
}