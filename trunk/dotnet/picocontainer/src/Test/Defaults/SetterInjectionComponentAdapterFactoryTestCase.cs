using System;
using PicoContainer.Tests.Tck;
using PicoContainer.Defaults;
using PicoContainer;
using NUnit.Framework;

namespace Test.Defaults
{
	/// <summary>
	/// Summary description for SetterInjectionComponentAdapterFactoryTestCase.
	/// </summary>
	public class SetterInjectionComponentAdapterFactoryTestCase : AbstractComponentAdapterFactoryTestCase
	{
    [SetUp]
    protected void setUp() {
      picoContainer = new DefaultPicoContainer(CreateComponentAdapterFactory());
    }

    protected override IComponentAdapterFactory CreateComponentAdapterFactory() {
        return new SetterInjectionComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory());
    }

    public interface Bean {
    }

    public class NamedBean : Bean {
        private String name;

      public String Name {
        get { return name; }
        set { name = value; }
      }
    }

    public class NamedBeanWithPossibleDefault : NamedBean {
        private bool byDefault = false;
        public NamedBeanWithPossibleDefault() {
        }
        public NamedBeanWithPossibleDefault(String name) {
            Name = name;
            byDefault = true;
        }
        public bool ByDefault {
          get {
            return byDefault;
          }
        }
    }

    public class NoBean : NamedBean {
        public NoBean(String name) {
            Name = name;
        }
    }

    public void testContainerIgnoresParameters() {
        picoContainer.RegisterComponentImplementation(typeof(Bean), typeof(NamedBean), new IParameter[] {
                new ConstantParameter("Dick")
                });
        picoContainer.RegisterComponentInstance("Tom");
        NamedBean bean = (NamedBean) picoContainer.GetComponentInstance(typeof(Bean));
        Assert.IsNotNull(bean);
        Assert.AreEqual("Tom", bean.Name);
    }

    public void testContainerUsesStandardConstructor() {
        picoContainer.RegisterComponentImplementation(typeof(Bean), typeof(NamedBeanWithPossibleDefault));
        picoContainer.RegisterComponentInstance("Tom");
        NamedBeanWithPossibleDefault bean = (NamedBeanWithPossibleDefault) picoContainer.GetComponentInstance(typeof(Bean));
        Assert.IsFalse(bean.ByDefault);
    }

    public void testContainerUsesOnlyStandardConstructor() {
        picoContainer.RegisterComponentImplementation(typeof(Bean), typeof(NoBean));
        picoContainer.RegisterComponentInstance("Tom");
        try {
            picoContainer.GetComponentInstance(typeof(Bean));
            Assert.Fail("Instantiation should have failed.");
        } catch(PicoInitializationException) {
        }
    }
 
  }
}
