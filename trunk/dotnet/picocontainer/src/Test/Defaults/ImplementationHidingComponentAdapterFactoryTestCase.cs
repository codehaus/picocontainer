using System;
using System.Collections;
using PicoContainer;
using PicoContainer.Defaults;
using PicoContainer.Tests.Tck;
using PicoContainer.Tests.TestModel;

using NUnit.Framework;

namespace Test.Defaults {
  /// <summary>
  /// Summary description for ImplementationHidingComponentAdapterFactoryTestCase.
  /// </summary>
  [TestFixture]
  public class ImplementationHidingComponentAdapterFactoryTestCase {// : AbstractComponentAdapterFactoryTestCase {
    private static ImplementationHidingComponentAdapterFactory implementationHiddingComponentAdapterFactory = new ImplementationHidingComponentAdapterFactory(new DefaultComponentAdapterFactory());
    private CachingComponentAdapterFactory cachingComponentAdapterFactory = new CachingComponentAdapterFactory(implementationHiddingComponentAdapterFactory);

    public  interface Man {
      Woman getWoman();

      void kiss();

      bool wasKissed();
    }

    public interface Woman {
      Man getMan();
    }

    public interface SuperWoman : Woman {
    }

    public class Husband : Man {
      public readonly Woman partner;
      private bool _wasKissed;

      public Husband(Woman partner) {
        this.partner = partner;
      }

      public Woman getWoman() {
        return partner;
      }

      public void kiss() {
        _wasKissed = true;
      }

      public bool wasKissed() {
        return _wasKissed;
      }
    }

    public class Wife : SuperWoman {
      public readonly Man partner;

      public Wife(Man partner) {
        this.partner = partner;
      }

      public Man getMan() {
        return partner;
      }
    }

    [Test]
    public void testLowLevelCheating() {
      IComponentAdapterFactory caf = CreateComponentAdapterFactory();
      DefaultPicoContainer pico = new DefaultPicoContainer(caf);

      CachingComponentAdapter wifeAdapter = (CachingComponentAdapter) caf.CreateComponentAdapter("wife", typeof(Wife), null);
      CachingComponentAdapter husbandAdapter = (CachingComponentAdapter) caf.CreateComponentAdapter("husband", typeof(Husband), null);

      pico.RegisterComponent(wifeAdapter);
      pico.RegisterComponent(husbandAdapter);

      Woman wife = (Woman) wifeAdapter.ComponentInstance;
      wife.getMan().kiss();
      Man man = (Man) husbandAdapter.ComponentInstance;
      Assert.IsTrue(man.wasKissed());

      Assert.AreSame(man, wife.getMan());
      Assert.AreSame(wife, man.getWoman());

      // Let the wife use another (single) man
      Man newMan = new Husband(null);
      Man oldMan = (Man) ((ISwappable) man).HotSwap(newMan);

      Assert.IsFalse(newMan.wasKissed());
      wife.getMan().kiss();
      Assert.IsTrue(newMan.wasKissed());
      Assert.IsFalse(man.Equals(oldMan));
      Assert.IsFalse(oldMan.Equals(newMan));
      Assert.IsFalse(newMan.Equals(man));

      Assert.IsFalse(man.GetHashCode() == oldMan.GetHashCode());
      Assert.IsFalse(oldMan.GetHashCode() == newMan.GetHashCode());
      // C# implementation fails, there is for as far as I know no System.identityHashCode available for c#
      //      Assert.IsFalse(newMan.GetHashCode() == man.GetHashCode());
    }

    
    [Test]
    public void testHighLevelCheating() {
      IMutablePicoContainer pico = new DefaultPicoContainer(CreateComponentAdapterFactory());

      // Register two classes with mutual dependencies in the constructor (!!!)
      pico.RegisterComponentImplementation(typeof(Wife));
      pico.RegisterComponentImplementation(typeof(Husband));

      Woman wife = (Woman) pico.GetComponentInstance(typeof(Wife));
      Man man = (Man) pico.GetComponentInstance(typeof(Husband));

      Assert.AreSame(man, wife.getMan());
      Assert.AreSame(wife, man.getWoman());

      // Let the wife use another (single) man
      Man newMan = new Husband(null);
      Man oldMan = (Man) ((ISwappable) man).HotSwap(newMan);

      wife.getMan().kiss();
      Assert.IsFalse(oldMan.wasKissed());
      Assert.IsTrue(newMan.wasKissed());

    }


    [Test]
    public void testBigamy() {
      DefaultPicoContainer pico = new DefaultPicoContainer(new ImplementationHidingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory()));
      pico.RegisterComponentImplementation(typeof(Woman), typeof(Wife));
      Woman firstWife = (Woman) pico.GetComponentInstance(typeof(Woman));
      Woman secondWife = (Woman) pico.GetComponentInstance(typeof(Woman));
      Assert.IsFalse(firstWife == secondWife);

    }


    [Test]
    public void testComponentsRegisteredWithClassKeyOnlyImplementThatInterface() {
      DefaultPicoContainer pico = new DefaultPicoContainer(new ImplementationHidingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory()));
      pico.RegisterComponentImplementation(typeof(Woman), typeof(Wife));
      Woman wife = (Woman) pico.GetComponentInstance(typeof(Woman));
      Assert.IsFalse(wife is SuperWoman);
    }


    public void testIHCAFwithCTORandNoCaching() {
      // http://lists.codehaus.org/pipermail/picocontainer-dev/2004-January/001985.html
      IMutablePicoContainer pico = new DefaultPicoContainer();
      pico.RegisterComponent(new ImplementationHidingComponentAdapter(new ConstructorInjectionComponentAdapter("l", typeof(ArrayList))));

      IList list1 = (IList) pico.GetComponentInstance("l");
      IList list2 = (IList) pico.GetComponentInstance("l");

      Assert.IsFalse(list1 == list2);
      Assert.IsFalse(list1 is ArrayList);

      list1.Add("Hello");
      Assert.IsTrue(list1.Contains("Hello"));
      Assert.IsFalse(list2.Contains("Hello"));
    }

    public void testSwappingViaSwappableInterface() {
      IMutablePicoContainer pico = new DefaultPicoContainer();
      pico.RegisterComponent(new ImplementationHidingComponentAdapter(new ConstructorInjectionComponentAdapter("l", typeof(ArrayList))));
      IList l = (IList) pico.GetComponentInstance("l");
      l.Add("Hello");
      ArrayList newList = new ArrayList();
      ArrayList oldSubject = (ArrayList) ((ISwappable) l).HotSwap(newList);
      Assert.AreEqual("Hello", oldSubject[0]);
      Assert.IsTrue(l.Count == 0);
      l.Add("World");
      Assert.AreEqual("World", l[0]);
    }


    public interface OtherSwappable {
      object HotSwap(object newSubject);
    }

    public class OtherSwappableImpl : OtherSwappable {
      public virtual object HotSwap(object newSubject) {
        return "TADA";
      }
    }


    public void testInterferingSwapMethodsInComponentMasksHotSwappingFunctionality() {
      IMutablePicoContainer pico = new DefaultPicoContainer();
      pico.RegisterComponent(new ImplementationHidingComponentAdapter(new ConstructorInjectionComponentAdapter("os", typeof(OtherSwappableImpl))));
      OtherSwappable os = (OtherSwappable) pico.GetComponentInstance("os");
      OtherSwappable os2 = new OtherSwappableImpl();

      Assert.AreEqual("TADA", os.HotSwap(os2));
      ISwappable os_ = (ISwappable) os;
      Assert.AreEqual("TADA", os_.HotSwap(os2));
    }



    protected IComponentAdapterFactory CreateComponentAdapterFactory() {
      return cachingComponentAdapterFactory;
    }


  }
}
