/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 * Ported to .NET by Jeremey Stell-Smith                                     *
 *****************************************************************************/



using System;
using System.Collections;
using System.Reflection;
using System.Threading;

using NUnit.Framework;

using PicoContainer;
using PicoContainer.Test.TestModel;

namespace PicoContainer.Test
{
	[TestFixture]
	public class HierarchicalPicoContainerTestCase : Assertion 
	{
		[Test, ExpectedException(typeof(PicoNullReferenceException))]
		public void TestNullContainer() 
		{
			new HierarchicalPicoContainer(null, new NullStartableLifecycleManager(), new DefaultComponentFactory());
		}

		[Test, ExpectedException(typeof(PicoNullReferenceException))]
		public void TestNullLifecycleManager() 
		{
			new HierarchicalPicoContainer(new NullContainer(), null, new DefaultComponentFactory());
		}

		[Test, ExpectedException(typeof(PicoNullReferenceException))]
		public void TestNullComponentFactory() 
		{
			new HierarchicalPicoContainer(new NullContainer(), new NullStartableLifecycleManager(), null);
		}

		[Test]
		public void TestBasicRegAndStart() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			pico.RegisterComponent(typeof(FredImpl));
			pico.RegisterComponent(typeof(WilmaImpl));

			pico.Start();

			AssertEquals("There should be two comps in the container", 2, pico.Components.Length);

			Assert("There should have been a Fred in the container", pico.HasComponent(typeof(FredImpl)));
			Assert("There should have been a Wilma in the container", pico.HasComponent(typeof(WilmaImpl)));
		}

		[Test]
		public void TestTooFewComponents() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			pico.RegisterComponent(typeof(FredImpl));

			try 
			{
				pico.Start();
				Fail("should need a wilma");
			} 
			catch (UnsatisfiedDependencyStartupException e) 
			{
				// expected
				Assert(e.ClassThatNeedsDeps == typeof(FredImpl));
				Assert(e.Message.IndexOf(typeof(FredImpl).Name) > 0);

			}
		}

		[Test]
		public void TestDupeImplementationsOfComponents() 
		{
			ArrayList messages = new ArrayList();
			IPicoContainer pico = new HierarchicalPicoContainer.Default();
			try 
			{
				pico.RegisterComponent(typeof(ArrayList), messages);
				pico.RegisterComponent(typeof(Dictionary), typeof(Webster));
				pico.RegisterComponent(typeof(Thesaurus), typeof(Webster));
				pico.Start();

				AssertEquals("Should only have one instance of Webster", 1, messages.Count);
				object dictionary = pico.GetComponent(typeof(Dictionary));
				object thesaurus = pico.GetComponent(typeof(Thesaurus));
				AssertSame("The dictionary and the thesaurus should heve been the same object", dictionary, thesaurus);
			} 
			catch (PicoRegistrationException) 
			{
				Fail("Should not have barfed with dupe registration");
			}
		}

		[Test]
		public void TestDupeTypesWithClass() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			pico.RegisterComponent(typeof(WilmaImpl));
			try 
			{
				pico.RegisterComponent(typeof(WilmaImpl));
				Fail("Should have barfed with dupe registration");
			} 
			catch (DuplicateComponentTypeRegistrationException e) 
			{
				// expected
				Assert(e.DuplicateClass == typeof(WilmaImpl));
				Assert(e.Message.IndexOf(typeof(WilmaImpl).Name) > 0);
			}
		}

		[Test]
		public void TestDupeTypesWithObject() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			pico.RegisterComponent(typeof(WilmaImpl));
			try 
			{
				pico.RegisterComponent(typeof(WilmaImpl), new WilmaImpl());
				Fail("Should have barfed with dupe registration");
			} 
			catch (DuplicateComponentTypeRegistrationException e) 
			{
				// expected
				Assert(e.DuplicateClass == typeof(WilmaImpl));
				Assert(e.Message.IndexOf(typeof(WilmaImpl).Name) > 0);
			}
		}

		private class DerivedWilma : WilmaImpl 
		{
			public DerivedWilma() 
			{
			}
		}

		[Test]
		public void TestAmbiguousHierarchy() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			// Register two Wilmas that Fred will be confused about
			pico.RegisterComponent(typeof(WilmaImpl));
			pico.RegisterComponent(typeof(DerivedWilma));

			// Register a confused Fred
			pico.RegisterComponent(typeof(FredImpl));

			try 
			{
				pico.Start();
				Fail("Fred should have been confused about the two Wilmas");
			} 
			catch (AmbiguousComponentResolutionException e) 
			{
				// expected
				ArrayList ambiguous = new ArrayList();;
				foreach(Type type in e.AmbiguousClasses) 
				{
					ambiguous.Add(type);
				}
				Assert(ambiguous.Contains(typeof(DerivedWilma)));
				Assert(ambiguous.Contains(typeof(WilmaImpl)));
				Assert(e.Message.IndexOf(typeof(WilmaImpl).Name) > 0);
				Assert(e.Message.IndexOf(typeof(DerivedWilma).Name) > 0);
			}
		}

		[Test]
		public void TestRegisterComponentWithObject() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			pico.RegisterComponent(typeof(FredImpl));
			pico.RegisterComponent(new WilmaImpl());

			pico.Start();

			Assert("There should have been a Fred in the container", pico.HasComponent(typeof(FredImpl)));
			Assert("There should have been a Wilma in the container", pico.HasComponent(typeof(WilmaImpl)));
		}

		[Test]
		public void TestRegisterComponentWithObjectBadType() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			try 
			{
				pico.RegisterComponent(typeof(System.Runtime.Serialization.ISerializable), new object());
				Fail("Shouldn't be able to register an object as Serializable");
			} 
			catch (PicoRegistrationException) 
			{
			}
		}

		[Test]
		public void TestComponentRegistrationMismatch() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			try 
			{
				pico.RegisterComponent(typeof(ArrayList), typeof(WilmaImpl));
			} 
			catch (AssignabilityRegistrationException e) 
			{
				// not worded in message
				Assert(e.Message.IndexOf(typeof(ArrayList).Name) > 0);
				Assert(e.Message.IndexOf(typeof(WilmaImpl).Name) > 0);
			}
		}

		[Test]
		public void TestMultipleArgumentConstructor() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			pico.RegisterComponent(typeof(FredImpl));
			pico.RegisterComponent(typeof(Wilma), typeof(WilmaImpl));
			pico.RegisterComponent(typeof(FlintstonesImpl));

			pico.Start();

			Assert("There should have been a FlintstonesImpl in the container", pico.HasComponent(typeof(FlintstonesImpl)));
		}

		[Test]
		public void TestGetComponentTypes() 
		{
			// ASLAK: don't declare as Impl. For IDEA jumps only
			HierarchicalPicoContainer pico = new HierarchicalPicoContainer.Default();

			pico.RegisterComponent(typeof(FredImpl));
			pico.RegisterComponent(typeof(Wilma), typeof(WilmaImpl));

			// You might have thought that starting the container shouldn't be necessary
			// just to get the types, but it is. The map holding the types->component instances
			// doesn't receive anything until the components are instantiated.
			pico.Start();

			ArrayList types = new ArrayList();
			foreach (Type type in pico.ComponentTypes) 
			{
				types.Add(type);
			}
			AssertEquals("There should be 2 types", 2, types.Count);
			Assert("There should be a FredImpl type", types.Contains(typeof(FredImpl)));
			Assert("There should be a Wilma type", types.Contains(typeof(Wilma)));
			Assert("There should not be a WilmaImpl type", !types.Contains(typeof(WilmaImpl)));
		}

		private class WilmaContainer : IContainer 
		{
			readonly Wilma wilma = new WilmaImpl();

			public bool HasComponent(Type componentType) 
			{
				return componentType == typeof(Wilma);
			}

			public object GetComponent(Type componentType) 
			{
				return componentType == typeof(Wilma) ? wilma : null;
			}

			public object[] Components
			{
				get { return new object[]{wilma}; }
			}

			public Type[] ComponentTypes
			{
				get { return new Type[]{typeof(Wilma)}; }
			}
		}

		[Test]
		public void TestParentContainer() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.WithParentContainer(new WilmaContainer());

			pico.RegisterComponent(typeof(FredImpl));

			pico.Start();

			AssertEquals("The parent should return 2 components (one from the parent)", 2, pico.Components.Length);
			Assert("Wilma should have been passed through the parent container", pico.HasComponent(typeof(Wilma)));
			Assert("There should have been a FredImpl in the container", pico.HasComponent(typeof(FredImpl)));

		}

		public class One 
		{
			ArrayList msgs = new ArrayList();

			public One() 
			{
				ping("One");
			}

			public void ping(string s) 
			{
				msgs.Add(s);
			}

			public ArrayList getMsgs() 
			{
				return msgs;
			}
		}

		public class Two 
		{
			public Two(One one) 
			{
				one.ping("Two");
			}
		}

		public class Three 
		{
			public Three(One one, Two two) 
			{
				one.ping("Three");
			}
		}

		public class Four 
		{
			public Four(Two two, Three three, One one) 
			{
				one.ping("Four");
			}
		}

		[Test]
		public void TestOrderOfInstantiation() 
		{
			OverriddenStartableLifecycleManager osm = new OverriddenStartableLifecycleManager();
			OveriddenPicoTestContainerWithLifecycleManager pico = new OveriddenPicoTestContainerWithLifecycleManager(null, osm);

			pico.RegisterComponent(typeof(Four));
			pico.RegisterComponent(typeof(Two));
			pico.RegisterComponent(typeof(One));
			pico.RegisterComponent(typeof(Three));

			pico.Start();

			Assert("There should have been a 'One' in the container", pico.HasComponent(typeof(One)));

			One one = (One) pico.GetComponent(typeof(One));

			// instantiation - would be difficult to do these in the wrong order!!
			AssertEquals("Should be four elems", 4, one.getMsgs().Count);
			AssertEquals("Incorrect Order of Instantiation", "One", one.getMsgs()[0]);
			AssertEquals("Incorrect Order of Instantiation", "Two", one.getMsgs()[1]);
			AssertEquals("Incorrect Order of Instantiation", "Three", one.getMsgs()[2]);
			AssertEquals("Incorrect Order of Instantiation", "Four", one.getMsgs()[3]);

			// post instantiation startup
			AssertEquals("Should be four elems", 4, osm.getStarted().Count);
			AssertEquals("Incorrect Order of Starting", typeof(One), osm.getStarted()[0]);
			AssertEquals("Incorrect Order of Starting", typeof(Two), osm.getStarted()[1]);
			AssertEquals("Incorrect Order of Starting", typeof(Three), osm.getStarted()[2]);
			AssertEquals("Incorrect Order of Starting", typeof(Four), osm.getStarted()[3]);

			pico.Stop();

			// post instantiation shutdown - REVERSE order.
			AssertEquals("Should be four elems", 4, osm.getStopped().Count);
			AssertEquals("Incorrect Order of Stopping", typeof(Four), osm.getStopped()[0]);
			AssertEquals("Incorrect Order of Stopping", typeof(Three), osm.getStopped()[1]);
			AssertEquals("Incorrect Order of Stopping", typeof(Two), osm.getStopped()[2]);
			AssertEquals("Incorrect Order of Stopping", typeof(One), osm.getStopped()[3]);
		}

		[Test]
		public void TestTooManyContructors() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			try 
			{
				pico.RegisterComponent(typeof(ArrayList));
				Fail("Should Fail because there are more than one constructors");
			} 
			catch (WrongNumberOfConstructorsRegistrationException e) 
			{
				Assert(e.Message.IndexOf("3") > 0);            //expected
				// expected;
			}
		}

		[Test]
		public void TestRegisterAbstractShouldFail() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			try 
			{
				pico.RegisterComponent(typeof(ICollection));
				Fail("Shouldn't be allowed to register abstract classes or interfaces.");
			} 
			catch (NotConcreteRegistrationException e) 
			{
				AssertEquals(typeof(ICollection), e.ComponentImplementation);
				Assert(e.Message.IndexOf(typeof(ICollection).Name) > 0);
			}
		}


		public class A 
		{
			public A(B b) 
			{
			}
		}

		public class B 
		{
			public B(C c, D d) 
			{
			}
		}

		public class C 
		{
			public C(A a, B b) 
			{
			}
		}

		public class D 
		{
			public D() 
			{
			}
		}

		private class StubFactory : IComponentFactory 
		{
			private object obj;
		
			public StubFactory(object obj) 
			{
				this.obj = obj;
			}

			public object CreateComponent(Type componentType, ConstructorInfo constructor, object[] args) 
			{
				return obj;
			}
		}

		[Test]
		public void TestWithComponentFactory() 
		{
			WilmaImpl wilma = new WilmaImpl();
			IPicoContainer pc = new HierarchicalPicoContainer.WithComponentFactory(new StubFactory(wilma));
			pc.RegisterComponent(typeof(WilmaImpl));
			pc.Start();
			AssertEquals(pc.GetComponent(typeof(WilmaImpl)), wilma);
		}

		class Barney 
		{
			public Barney() 
			{
				throw new Exception("Whoa!");
			}
		}

		[Test]
		public void TestInvocationTargetException() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();
			pico.RegisterComponent(typeof(Barney));
			try 
			{
				pico.Start();
			} 
			catch (PicoInvocationTargetStartException e) 
			{
				AssertEquals("Whoa!", e.InnerException.Message);
				AssertEquals("Whoa!", e.Message);
			}
		}

		public class BamBam 
		{
			public BamBam() 
			{
			}
		}

		[Test, Ignore("TODO uncomment this and make it pass")]
		public void TestCircularDependencyShouldFail() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			try 
			{
				pico.RegisterComponent(typeof(A));
				pico.RegisterComponent(typeof(B));
				pico.RegisterComponent(typeof(C));
				pico.RegisterComponent(typeof(D));

				pico.Start();
				Fail("Should have gotten a CircularDependencyRegistrationException");
			} 
			catch (CircularDependencyRegistrationException)
			{
				// ok
			}
		}

		interface Animal 
		{

			string getFood();
		}

		public class Dino : Animal 
		{
			string food;

			public Dino(string food) 
			{
				this.food = food;
			}

			public string getFood() 
			{
				return food;
			}
		}

		[Test]
		public void TestParameterCanBePassedToConstructor() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();
			pico.RegisterComponent(typeof(Animal), typeof(Dino));
			pico.AddParameterToComponent(typeof(Dino), typeof(string), "bones");
			pico.Start();

			Animal animal = (Animal) pico.GetComponent(typeof(Animal));
			AssertNotNull("Component not null", animal);
			AssertEquals("bones", animal.getFood());
		}

		public class Dino2 : Dino 
		{
			public Dino2(int number) : base(Convert.ToString(number)) {}
		}

		[Test]
		public void TestParameterCanBePrimitive() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();
			pico.RegisterComponent(typeof(Animal), typeof(Dino2));
			pico.AddParameterToComponent(typeof(Dino2), typeof(int), 22);
			pico.Start();

			Animal animal = (Animal) pico.GetComponent(typeof(Animal));
			AssertNotNull("Component not null", animal);
			AssertEquals("22", animal.getFood());
		}

		public class Dino3 : Dino 
		{
			public Dino3(string a, string b) : base(a + b) {}
		}

		[Test]
		public void TestMultipleParametersCanBePassed() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();
			pico.RegisterComponent(typeof(Animal), typeof(Dino3));
			pico.AddParameterToComponent(typeof(Dino3), typeof(string), "a");
			pico.AddParameterToComponent(typeof(Dino3), typeof(string), "b");
			pico.Start();

			Animal animal = (Animal) pico.GetComponent(typeof(Animal));
			AssertNotNull("Component not null", animal);
			AssertEquals("ab", animal.getFood());
		}

		public class Dino4 : Dino 
		{
			public Dino4(string a, int n, string b, Wilma wilma) : base(a + n + b + " " + wilma.GetType().FullName) {}
		}

		[Test]
		public void TestParametersCanBeMixedWithComponentsCanBePassed() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();
			pico.RegisterComponent(typeof(Animal), typeof(Dino4));
			pico.RegisterComponent(typeof(Wilma), typeof(WilmaImpl));
			pico.AddParameterToComponent(typeof(Dino4), typeof(string), "a");
			pico.AddParameterToComponent(typeof(Dino4), typeof(int), 3);
			pico.AddParameterToComponent(typeof(Dino4), typeof(string), "b");
			pico.Start();

			Animal animal = (Animal) pico.GetComponent(typeof(Animal));
			AssertNotNull("Component not null", animal);
			AssertEquals("a3b PicoContainer.Test.TestModel.WilmaImpl", animal.getFood());
		}

		[Test]
		public void TestStartStopStartStopAndDispose() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			pico.RegisterComponent(typeof(FredImpl));
			pico.RegisterComponent(typeof(WilmaImpl));

			pico.Start();
			pico.Stop();

			pico.Start();
			pico.Stop();

			pico.Dispose();
		}

		[Test]
		public void TestStartStartCausingBarf() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			pico.RegisterComponent(typeof(FredImpl));
			pico.RegisterComponent(typeof(WilmaImpl));

			pico.Start();
			try 
			{
				pico.Start();
				Fail("Should have barfed");
			} 
			catch (InvalidOperationException) 
			{
				// expected;
			}
		}

		[Test]
		public void TestStartStopStopCausingBarf() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			pico.RegisterComponent(typeof(FredImpl));
			pico.RegisterComponent(typeof(WilmaImpl));

			pico.Start();
			pico.Stop();
			try 
			{
				pico.Stop();
				Fail("Should have barfed");
			} 
			catch (InvalidOperationException) 
			{
				// expected;
			}
		}

		[Test]
		public void TestStartStopDisposeDisposeCausingBarf() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.Default();

			pico.RegisterComponent(typeof(FredImpl));
			pico.RegisterComponent(typeof(WilmaImpl));

			pico.Start();
			pico.Stop();
			pico.Dispose();
			try 
			{
				pico.Dispose();
				Fail("Should have barfed");
			} 
			catch (InvalidOperationException)
			{
				// expected;
			}
		}

		public class Foo 
		{
			private int runCount;
			private Thread thread;
			private bool interrupted;

			public Foo() 
			{
			}

			public int RunCount() 
			{
				return runCount;
			}

			public bool isInterrupted() 
			{
				return interrupted;
			}

			public void Start() 
			{
				thread = new Thread(new ThreadStart(run));
				thread.Start();
			}

			public void Stop() 
			{
				thread.Interrupt();
			}

			// this would do something a bit more concrete
			// than counting in real life !
			public void run() 
			{
				runCount++;
				try 
				{
					Thread.Sleep(10000);
				} 
				catch (System.Threading.ThreadInterruptedException) 
				{
					interrupted = true;
				}
			}
		}

		[Test]
		public void TestStartStopOfDaemonizedThread() 
		{
			IPicoContainer pico = new HierarchicalPicoContainer.WithStartableLifecycleManager(new ReflectionUsingLifecycleManager());

			pico.RegisterComponent(typeof(FredImpl));
			pico.RegisterComponent(typeof(WilmaImpl));
			pico.RegisterComponent(typeof(Foo));

			pico.Start();
			Thread.Sleep(100);
			pico.Stop();
			Foo foo = (Foo) pico.GetComponent(typeof(Foo));
			AssertEquals(1, foo.RunCount());
			pico.Start();
			Thread.Sleep(100);
			pico.Stop();
			AssertEquals(2, foo.RunCount());
		}
	}
}
