using System;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;
using System.Collections;
using PicoContainer.Tests.TestModel;

namespace Test.Defaults {

  [TestFixture]
  public class BeanPropertyComponentAdapterFactoryTestCase {
    public class Foo {
      private String message;

      public String Message {
        get { return message; }
        set { message = value; }
      }
    }

    public class Failing {
      public String Message {
        set {
          throw new ApplicationException();
        }
      }
    }

    public class A {
      private B b;

      public void setB(B b) {
        this.b = b;
      }
    }

    public class B {
    }

    [Test]
    public void testSetProperties() {
      IComponentAdapter adapter = CreateAdapterCallingSetMessage(typeof(Foo));
      Foo foo = (Foo) adapter.ComponentInstance;
      Assert.IsNotNull(foo);
      Assert.AreEqual("hello", foo.Message);
    }
  
    [Test]
    [ExpectedException(typeof(PicoInitializationException))]
    public void testFailingSetter() {
      IComponentAdapter adapter = CreateAdapterCallingSetMessage(typeof(Failing));
      object instance = adapter.ComponentInstance;
    }

    [Test]
    public void testPropertiesSetAfterAdapterCreationShouldBeTakenIntoAccount() {
      BeanPropertyComponentAdapterFactory factory = (BeanPropertyComponentAdapterFactory) CreateComponentAdapterFactory();

      BeanPropertyComponentAdapter adapter = (BeanPropertyComponentAdapter) factory.CreateComponentAdapter("foo", typeof(Foo), null);

      Hashtable properties = new Hashtable();
      properties.Add("Message", "hello");
      adapter.Properties = properties;

      Foo foo = (Foo) adapter.ComponentInstance;

      Assert.AreEqual("hello", foo.Message);
    }

    public void testDelegateIsAccessible() {
      DecoratingComponentAdapter componentAdapter =
        (DecoratingComponentAdapter) CreateComponentAdapterFactory().CreateComponentAdapter(typeof(Touchable), typeof(SimpleTouchable), null);

      Assert.IsNotNull(componentAdapter.Delegate);
    }

    protected IComponentAdapterFactory CreateComponentAdapterFactory() {
      return new BeanPropertyComponentAdapterFactory(new DefaultComponentAdapterFactory());
    }

    private IComponentAdapter CreateAdapterCallingSetMessage(Type impl) {
      BeanPropertyComponentAdapterFactory factory = (BeanPropertyComponentAdapterFactory) CreateComponentAdapterFactory();

      Hashtable properties = new Hashtable();
      properties.Add("Message", "hello");

      BeanPropertyComponentAdapter adapter = (BeanPropertyComponentAdapter) factory.CreateComponentAdapter(impl, impl, null);
      adapter.Properties = properties;
      return adapter;
    }


  }
}
