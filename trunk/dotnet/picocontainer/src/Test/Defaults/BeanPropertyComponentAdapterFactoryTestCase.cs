using System;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;
using System.Collections;
using PicoContainer.Tests.TestModel;
using PicoContainer.Tests.Tck;

namespace Test.Defaults
{
	[TestFixture]
	public class BeanPropertyComponentAdapterFactoryTestCase : AbstractComponentAdapterFactoryTestCase
	{
		public class Foo
		{
			private String message;

			public String Message
			{
				get { return message; }
				set { message = value; }
			}
		}

		public class Failing
		{
			public String Message
			{
				set { throw new ApplicationException(); }
			}
		}

		public class A
		{
			private B b;

			public B B
			{
				get { return b; }
				set { b = value; }
			}

			public void setB(B b)
			{
				this.b = b;
			}

			public B getB()
			{
				return b;
			}
		}

		public class B
		{
		}

		[Test]
		public void testSetProperties()
		{
			IComponentAdapter adapter = CreateAdapterCallingSetMessage(typeof (Foo));
			Foo foo = (Foo) adapter.ComponentInstance;
			Assert.IsNotNull(foo);
			Assert.AreEqual("hello", foo.Message);
		}

		[Test]
		[ExpectedException(typeof (PicoInitializationException))]
		public void testFailingSetter()
		{
			IComponentAdapter adapter = CreateAdapterCallingSetMessage(typeof (Failing));
			object instance = adapter.ComponentInstance;
		}

		[Test]
		public void testPropertiesSetAfterAdapterCreationShouldBeTakenIntoAccount()
		{
			BeanPropertyComponentAdapterFactory factory = (BeanPropertyComponentAdapterFactory) CreateComponentAdapterFactory();

			BeanPropertyComponentAdapter adapter = (BeanPropertyComponentAdapter) factory.CreateComponentAdapter("foo", typeof (Foo), null);

			Hashtable properties = new Hashtable();
			properties.Add("Message", "hello");
			adapter.Properties = properties;

			Foo foo = (Foo) adapter.ComponentInstance;

			Assert.AreEqual("hello", foo.Message);
		}

		public void testDelegateIsAccessible()
		{
			DecoratingComponentAdapter componentAdapter =
				(DecoratingComponentAdapter) CreateComponentAdapterFactory().CreateComponentAdapter(typeof (Touchable), typeof (SimpleTouchable), null);

			Assert.IsNotNull(componentAdapter.Delegate);
		}

		protected override IComponentAdapterFactory CreateComponentAdapterFactory()
		{
			return new BeanPropertyComponentAdapterFactory(new DefaultComponentAdapterFactory());
		}

		private IComponentAdapter CreateAdapterCallingSetMessage(Type impl)
		{
			BeanPropertyComponentAdapterFactory factory = (BeanPropertyComponentAdapterFactory) CreateComponentAdapterFactory();

			Hashtable properties = new Hashtable();
			properties.Add("Message", "hello");

			BeanPropertyComponentAdapter adapter = (BeanPropertyComponentAdapter) factory.CreateComponentAdapter(impl, impl, null);
			adapter.Properties = properties;
			return adapter;
		}

		public void testSetDependenComponentWillBeSetByTheAdapter()
		{
			picoContainer.RegisterComponentImplementation("b", typeof (B));
			BeanPropertyComponentAdapterFactory factory = (BeanPropertyComponentAdapterFactory) CreateComponentAdapterFactory();
			Hashtable properties = new Hashtable();

			// the second b is the key of the B implementation
			properties.Add("B", "b");
			BeanPropertyComponentAdapter adapter = (BeanPropertyComponentAdapter) factory.CreateComponentAdapter(typeof (A), typeof (A), null);
			adapter.Properties = properties;
			picoContainer.RegisterComponent(adapter);
			A a = (A) picoContainer.GetComponentInstance(typeof (A));

			Assert.IsNotNull(a);
			Assert.IsNotNull(a.getB());
		}

	}
}