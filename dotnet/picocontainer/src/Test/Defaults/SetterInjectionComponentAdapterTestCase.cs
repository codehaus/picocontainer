using System.Collections;
using PicoContainer;
using PicoContainer.Defaults;
using NUnit.Framework;

namespace Test.Defaults
{
	/// <summary>
	/// Summary description for BeanIComponentAdapterTestCase.
	/// </summary>
	[TestFixture]
	public class SetterInjectionComponentAdapterTestCase
	{
		public class A
		{
			private B b;
			private string theString;
			private IList list;


			public B B
			{
				get { return b; }
				set { b = value; }
			}

			public string TheString
			{
				get { return theString; }
				set { theString = value; }
			}


			public IList List
			{
				get { return list; }
				set { list = value; }
			}
		}

		public class B
		{
		}

		[Test]
		public void testDependenciesAreResolved()
		{
			SetterInjectionComponentAdapter aAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("a", typeof (A), null));
			SetterInjectionComponentAdapter bAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("b", typeof (B), null));

			IMutablePicoContainer pico = new DefaultPicoContainer();
			pico.RegisterComponent(bAdapter);
			pico.RegisterComponent(aAdapter);
			pico.RegisterComponentInstance("YO");
			pico.RegisterComponentImplementation(typeof (ArrayList));

			A a = (A) aAdapter.ComponentInstance;
			Assert.IsNotNull(a.B);
			Assert.IsNotNull(a.TheString);
			Assert.IsNotNull(a.List);
		}

		[Test]
		public void testAllUnsatisfiableDependenciesAreSignalled()
		{
			SetterInjectionComponentAdapter aAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("a", typeof (A), null));
			SetterInjectionComponentAdapter bAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("b", typeof (B), null));

			IMutablePicoContainer pico = new DefaultPicoContainer();
			pico.RegisterComponent(bAdapter);
			pico.RegisterComponent(aAdapter);

			try
			{
				object o = aAdapter.ComponentInstance;
			}
			catch (UnsatisfiableDependenciesException e)
			{
				Assert.IsTrue(e.UnsatisfiableDependencies.Contains(typeof (IList)));
				Assert.IsTrue(e.UnsatisfiableDependencies.Contains(typeof (string)));
			}
		}

/*    public void testBeanWithParameters() {
      BeanComponentAdapter aAdapter = new BeanComponentAdapter(new ConstructorInjectionComponentAdapter("a", typeof(A), new IParameter[] {
                                                                                                                                           new ComponentParameter(),
                                                                                                                                           new ConstantParameter("YO"),
                                                                                                                                           new ConstantParameter(new ArrayList())
                                                                                                                                         }));
      BeanComponentAdapter bAdapter = new BeanComponentAdapter(new ConstructorInjectionComponentAdapter("b", typeof(B), null));
      IMutablePicoContainer pico = new DefaultPicoContainer();
      pico.RegisterComponent(bAdapter);
      pico.RegisterComponent(aAdapter);
      A a = (A) aAdapter.ComponentInstance;
      Assert.IsNotNull(a.B);
      Assert.IsNotNull(a.TheString);
      Assert.IsNotNull(a.List);
    }
        
    public class C {
      private B b;
      private IList l;
      private bool asBean;
      public C() {
        asBean = true;
      }
      public C(B b) {
        this.l = new ArrayList();
        this.b = b;
        asBean = false;
      }
      public B B{
        set {
          this.b = value;
        }
        get
        {
          return b;
        }
      }

      public IList List{
        set {
          this.l = value;
        }
        get
        {
          return l;
        }
      }

      public bool instantiatedAsBean() {
        return asBean;
      }
    }
        
    public void testHybrids() {
      BeanComponentAdapter bAdapter = new BeanComponentAdapter(new ConstructorInjectionComponentAdapter("b", typeof(B), new IParameter[] {}));
      BeanComponentAdapter cAdapter = new BeanComponentAdapter(new ConstructorInjectionComponentAdapter("c", typeof(C), new IParameter[] {}));
      BeanComponentAdapter cNullAdapter = new BeanComponentAdapter(new ConstructorInjectionComponentAdapter("c0", typeof(C), null));
                                                                                                                             
      IMutablePicoContainer pico = new DefaultPicoContainer();
      pico.RegisterComponent(bAdapter);
      pico.RegisterComponent(cAdapter);
      pico.RegisterComponent(cNullAdapter);
      pico.RegisterComponentImplementation(typeof(ArrayList));
                                                                                                                                                                                       
      C c = (C) cAdapter.ComponentInstance;
      Assert.IsTrue(c.instantiatedAsBean());
      C c0 = (C) cNullAdapter.ComponentInstance;
      Assert.IsTrue(c0.instantiatedAsBean());
    }
    */
	}
}