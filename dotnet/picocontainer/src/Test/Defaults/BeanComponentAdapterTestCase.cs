using System;
using System.Collections;
using PicoContainer;
using PicoContainer.Defaults;
using PicoContainer.Tests.Tck;
using PicoContainer.Tests.TestModel;

using NUnit.Framework;

namespace Test.Defaults {
  /// <summary>
  /// Summary description for BeanIComponentAdapterTestCase.
  /// </summary>
  [TestFixture]
  public class BeanComponentAdapterTestCase {
    public class A {
      private B b;
      private string theString;
      private IList list;


      public B B {
        get {
          return b;
        }
        set {
          b = value;
        }
      }

      public string TheString {
        get {
          return theString;
        }
        set {
          theString = value;
        }
      }


      public IList List {
        get {
          return list;
        }
        set {
          list = value;
        }
      }
    }

    public class B {
    }

    [Test]
    public void testDependenciesAreResolved() {
      BeanComponentAdapter aAdapter = new BeanComponentAdapter(new ConstructorInjectionComponentAdapter("a", typeof(A), null));
      BeanComponentAdapter bAdapter = new BeanComponentAdapter(new ConstructorInjectionComponentAdapter("b", typeof(B), null));

      IMutablePicoContainer pico = new DefaultPicoContainer();
      pico.RegisterComponent(bAdapter);
      pico.RegisterComponent(aAdapter);
      pico.RegisterComponentInstance("YO");
      pico.RegisterComponentImplementation(typeof(ArrayList));

      A a = (A) aAdapter.ComponentInstance;
      Assert.IsNotNull(a.B);
      Assert.IsNotNull(a.TheString);
      Assert.IsNotNull(a.List);
    }

    [Test]
    public void testAllUnsatisfiableDependenciesAreSignalled() {
      BeanComponentAdapter aAdapter = new BeanComponentAdapter(new ConstructorInjectionComponentAdapter("a", typeof(A), null));
      BeanComponentAdapter bAdapter = new BeanComponentAdapter(new ConstructorInjectionComponentAdapter("b", typeof(B), null));

      IMutablePicoContainer pico = new DefaultPicoContainer();
      pico.RegisterComponent(bAdapter);
      pico.RegisterComponent(aAdapter);

      try {
        object o = aAdapter.ComponentInstance;
      } catch (UnsatisfiableDependenciesException e) {
        Assert.IsTrue(e.UnsatisfiableDependencies.Contains(typeof(IList)));
        Assert.IsTrue(e.UnsatisfiableDependencies.Contains(typeof(string)));
      }
    }

  }
}
